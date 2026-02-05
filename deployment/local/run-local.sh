#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

# deployment/local/run-local.sh
# Builds the Gradle project, runs tests, builds Docker images and runs them locally.
# Adjust the IMAGE_* and PORT_* variables below if you want different names or ports.

# Resolve repository root (script lives in deployment/local)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Configuration - change as needed
WEB_API_IMAGE="supermarket-web-api:local"
ENGINE_IMAGE="supermarket-engine:local"
WEBSITE_IMAGE="supermarket-website:local"

WEB_API_CONTAINER="supermarket-web-api-local"
ENGINE_CONTAINER="supermarket-engine-local"
WEBSITE_CONTAINER="supermarket-website-local"

# Host:container port mappings
WEB_API_PORT_HOST="8080"
WEB_API_PORT_CONTAINER="8080"
ENGINE_PORT_HOST="8081"
ENGINE_PORT_CONTAINER="8081"
WEBSITE_PORT_HOST="3000"   # host port to access the website
WEBSITE_PORT_CONTAINER="80" # container exposes nginx on 80

# Postgres DB settings (local development)
DB_IMAGE="postgres:15-alpine"
DB_CONTAINER="supermarket-db-local"
DB_PORT_HOST="5432"
DB_PORT_CONTAINER="5432"
DB_USER="supermarket"
DB_PASSWORD="supermarket"
DB_NAME="supermarket_db"

# Helper logging
info(){ printf "\n[INFO] %s\n" "$*"; }
warn(){ printf "\n[WARN] %s\n" "$*"; }
err(){ printf "\n[ERROR] %s\n" "$*"; exit 1; }

# 1) Build the project (Gradle)
info "Building project (Gradle)":
if [ -x "$REPO_ROOT/gradlew" ]; then
  (cd "$REPO_ROOT" && ./gradlew clean assemble)
else
  (cd "$REPO_ROOT" && gradle clean assemble)
fi

# 2) Run tests
info "Running backend tests (Gradle)"
if [ -x "$REPO_ROOT/gradlew" ]; then
  (cd "$REPO_ROOT" && ./gradlew test)
else
  (cd "$REPO_ROOT" && gradle test)
fi

# Website (frontend) tests + build
if [ -f "$REPO_ROOT/website/package.json" ]; then
  info "Installing website dependencies and running website tests/build"
  (cd "$REPO_ROOT/website" && npm ci)
  # run tests if script exists; --if-present keeps things non-fatal when no test script
  (cd "$REPO_ROOT/website" && npm run test --if-present)
  (cd "$REPO_ROOT/website" && npm run build --if-present)
else
  info "No website package.json found at $REPO_ROOT/website - skipping frontend steps"
fi

# 3) Build Docker images
info "Building Docker images"

# web-api: Dockerfile expects full repo as context
if [ -f "$REPO_ROOT/web-api/Dockerfile" ]; then
  docker build -t "$WEB_API_IMAGE" -f "$REPO_ROOT/web-api/Dockerfile" "$REPO_ROOT"
else
  warn "No web-api Dockerfile found at $REPO_ROOT/web-api/Dockerfile - skipping"
fi

# engine: Dockerfile expects full repo as context
if [ -f "$REPO_ROOT/engine/Dockerfile" ]; then
  docker build -t "$ENGINE_IMAGE" -f "$REPO_ROOT/engine/Dockerfile" "$REPO_ROOT"
else
  warn "No engine Dockerfile found at $REPO_ROOT/engine/Dockerfile - skipping"
fi

# website: can build from website dir
if [ -f "$REPO_ROOT/website/Dockerfile" ]; then
  docker build -t "$WEBSITE_IMAGE" -f "$REPO_ROOT/website/Dockerfile" "$REPO_ROOT/website"
else
  warn "No website Dockerfile found at $REPO_ROOT/website/Dockerfile - skipping"
fi

# 4) Run images in local docker (stop + remove previous containers if present)
info "Stopping and removing existing containers (if any)"
docker rm -f "$WEB_API_CONTAINER" >/dev/null 2>&1 || true
docker rm -f "$ENGINE_CONTAINER" >/dev/null 2>&1 || true
docker rm -f "$WEBSITE_CONTAINER" >/dev/null 2>&1 || true
docker rm -f "$DB_CONTAINER" >/dev/null 2>&1 || true

info "Starting containers"

# Start Postgres first so backend services can connect to it.
if docker image inspect "$DB_IMAGE" >/dev/null 2>&1; then
  info "Using existing image $DB_IMAGE"
else
  info "Pulling Postgres image $DB_IMAGE"
  docker pull "$DB_IMAGE"
fi

info "Starting Postgres: $DB_CONTAINER"
docker run -d --name "$DB_CONTAINER" \
  -e POSTGRES_USER="$DB_USER" \
  -e POSTGRES_PASSWORD="$DB_PASSWORD" \
  -e POSTGRES_DB="$DB_NAME" \
  -p ${DB_PORT_HOST}:${DB_PORT_CONTAINER} \
  -v "$REPO_ROOT/database/src/main/resources/db/migration":/docker-entrypoint-initdb.d:ro \
  "$DB_IMAGE"

# Wait for Postgres to be ready (pg_isready in a loop)
info "Waiting for Postgres to become available..."
MAX_ATTEMPTS=30
attempt=1
until docker exec "$DB_CONTAINER" pg_isready -U "$DB_USER" >/dev/null 2>&1; do
  if [ $attempt -ge $MAX_ATTEMPTS ]; then
    err "Postgres did not become ready in time"
  fi
  printf "."
  attempt=$((attempt+1))
  sleep 1
done
info "Postgres is ready"

# Apply migration scripts explicitly (idempotent if scripts use IF NOT EXISTS as in our SQL)
info "Applying SQL migrations from database resources"

# Run migration loop inside the Postgres container so the glob is expanded in-container
# (the host filesystem does not contain /docker-entrypoint-initdb.d and expanding it on the host
# leads to the literal pattern and the "No such file or directory" error).
# We expand ${DB_USER} and ${DB_NAME} on the host so psql inside the container gets the correct user/db.

docker exec -i "$DB_CONTAINER" sh -c "
  set -e
  found=false
  for f in /docker-entrypoint-initdb.d/*.sql; do
    # If the glob didn't match any files, the pattern will remain literal and we should break
    if [ \"\$f\" = '/docker-entrypoint-initdb.d/*.sql' ]; then
      break
    fi
    found=true
    echo \"[DB] Applying \$f\"
    psql -U ${DB_USER} -d ${DB_NAME} -f \"\$f\"
  done
  if [ \"\$found\" = false ]; then
    echo '[DB] No SQL migration files found in /docker-entrypoint-initdb.d'
  fi
"

if docker image inspect "$WEB_API_IMAGE" >/dev/null 2>&1; then
  docker run -d --name "$WEB_API_CONTAINER" -p ${WEB_API_PORT_HOST}:${WEB_API_PORT_CONTAINER} "$WEB_API_IMAGE"
  info "Started web-api: http://localhost:${WEB_API_PORT_HOST}"
else
  warn "Image $WEB_API_IMAGE not found; skipped web-api run"
fi

if docker image inspect "$ENGINE_IMAGE" >/dev/null 2>&1; then
  docker run -d --name "$ENGINE_CONTAINER" -p ${ENGINE_PORT_HOST}:${ENGINE_PORT_CONTAINER} "$ENGINE_IMAGE"
  info "Started engine: http://localhost:${ENGINE_PORT_HOST}"
else
  warn "Image $ENGINE_IMAGE not found; skipped engine run"
fi

if docker image inspect "$WEBSITE_IMAGE" >/dev/null 2>&1; then
  docker run -d --name "$WEBSITE_CONTAINER" -p ${WEBSITE_PORT_HOST}:${WEBSITE_PORT_CONTAINER} "$WEBSITE_IMAGE"
  info "Started website: http://localhost:${WEBSITE_PORT_HOST}"
else
  warn "Image $WEBSITE_IMAGE not found; skipped website run"
fi

info "All done. Use 'docker ps' to inspect running containers."

# End of script
