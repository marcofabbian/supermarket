#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

# deployment/local/run-db.sh
# Starts a local Postgres Docker container for development with sensible defaults.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Configuration (change if you need different values)
DB_IMAGE="postgres:15-alpine"
DB_CONTAINER="supermarket-db-local"
DB_PORT_HOST="5432"
DB_PORT_CONTAINER="5432"
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_NAME="supermarket"

info(){ printf "\n[INFO] %s\n" "$*"; }
warn(){ printf "\n[WARN] %s\n" "$*"; }
err(){ printf "\n[ERROR] %s\n" "$*"; exit 1; }

# Stop & remove any existing container with the same name
info "Stopping and removing existing container (if any): $DB_CONTAINER"
docker rm -f "$DB_CONTAINER" >/dev/null 2>&1 || true

# Pull image if needed
if docker image inspect "$DB_IMAGE" >/dev/null 2>&1; then
  info "Using existing image $DB_IMAGE"
else
  info "Pulling Postgres image $DB_IMAGE"
  docker pull "$DB_IMAGE"
fi

# Start Postgres container
info "Starting Postgres container: $DB_CONTAINER (db=$DB_NAME user=$DB_USER)"
docker run -d --name "$DB_CONTAINER" \
  -e POSTGRES_USER="$DB_USER" \
  -e POSTGRES_PASSWORD="$DB_PASSWORD" \
  -e POSTGRES_DB="$DB_NAME" \
  -p ${DB_PORT_HOST}:${DB_PORT_CONTAINER} \
  -v "$REPO_ROOT/database/src/main/resources/db/migration":/docker-entrypoint-initdb.d:ro \
  "$DB_IMAGE"

# Wait for Postgres to be ready
info "Waiting for Postgres to become available on localhost:${DB_PORT_HOST}..."
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

# Apply migration scripts explicitly (idempotent if scripts use IF NOT EXISTS)
MIG_DIR_IN_CONTAINER="/docker-entrypoint-initdb.d"
info "Applying SQL migrations from $MIG_DIR_IN_CONTAINER (if any)"

docker exec -i "$DB_CONTAINER" sh -c "
  set -e
  found=false
  for f in ${MIG_DIR_IN_CONTAINER}/*.sql; do
    if [ \"\$f\" = '${MIG_DIR_IN_CONTAINER}/*.sql' ]; then
      break
    fi
    found=true
    echo \"[DB] Applying \$f\"
    psql -U ${DB_USER} -d ${DB_NAME} -f \"\$f\"
  done
  if [ \"\$found\" = false ]; then
    echo '[DB] No SQL migration files found in ${MIG_DIR_IN_CONTAINER}'
  fi
"

info "Database container '$DB_CONTAINER' is up and listening on localhost:${DB_PORT_HOST}"
info "Connect with: psql -h localhost -p ${DB_PORT_HOST} -U ${DB_USER} ${DB_NAME}"

# Helpful note about cleanup
printf "\nTo stop and remove the container run:\n  docker rm -f %s\n\n" "$DB_CONTAINER"

