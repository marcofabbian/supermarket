#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

# deployment/local/teardown-local.sh
# Stops and removes local containers whose names start with "supermarket"
# and removes local images whose repository starts with "supermarket".
# Usage:
#   ./teardown-local.sh            # stops containers and deletes images
#   ./teardown-local.sh --yes      # don't prompt for confirmation
#   ./teardown-local.sh --containers-only
#   ./teardown-local.sh --images-only

DOCKER_CMD="docker"

CONFIRM=true
DO_CONTAINERS=true
DO_IMAGES=true

for arg in "$@"; do
  case "$arg" in
    --yes|-y)
      CONFIRM=false
      ;;
    --containers-only)
      DO_IMAGES=false
      ;;
    --images-only)
      DO_CONTAINERS=false
      ;;
    --help|-h)
      sed -n '1,200p' "$0"
      exit 0
      ;;
    *)
      ;;
  esac
done

info(){ printf "\n[INFO] %s\n" "$*"; }
warn(){ printf "\n[WARN] %s\n" "$*"; }
err(){ printf "\n[ERROR] %s\n" "$*"; exit 1; }

# Ensure docker is available
if ! command -v "$DOCKER_CMD" >/dev/null 2>&1; then
  err "docker command not found. Please install Docker and try again."
fi

# Find containers whose name starts with 'supermarket'
if [ "$DO_CONTAINERS" = true ]; then
  mapfile -t CONTAINERS_TO_REMOVE < <($DOCKER_CMD ps -a --format '{{.Names}}' | grep -E '^supermarket' || true)
else
  CONTAINERS_TO_REMOVE=()
fi

# Find images whose repository starts with 'supermarket'
if [ "$DO_IMAGES" = true ]; then
  mapfile -t IMAGES_TO_REMOVE < <($DOCKER_CMD images --format '{{.Repository}}:{{.Tag}} {{.ID}}' | awk '$1 ~ /^supermarket/ {print $2}' || true)
else
  IMAGES_TO_REMOVE=()
fi

if [ ${#CONTAINERS_TO_REMOVE[@]} -eq 0 ] && [ ${#IMAGES_TO_REMOVE[@]} -eq 0 ]; then
  info "No matching containers or images found (prefix 'supermarket'). Nothing to do."
  exit 0
fi

info "Planned actions:"
if [ ${#CONTAINERS_TO_REMOVE[@]} -gt 0 ]; then
  printf "  Containers to remove (%d):\n" ${#CONTAINERS_TO_REMOVE[@]}
  for c in "${CONTAINERS_TO_REMOVE[@]}"; do printf "    - %s\n" "$c"; done
fi
if [ ${#IMAGES_TO_REMOVE[@]} -gt 0 ]; then
  printf "  Images to remove (%d):\n" ${#IMAGES_TO_REMOVE[@]}
  for i in "${IMAGES_TO_REMOVE[@]}"; do printf "    - %s\n" "$i"; done
fi

if [ "$CONFIRM" = true ]; then
  read -r -p $'Proceed and remove the above containers/images? [y/N] ' reply
  case "$reply" in
    [yY]|[yY][eE][sS]) ;;
    *) info "Aborting."; exit 0 ;;
  esac
fi

# Stop and remove containers
if [ ${#CONTAINERS_TO_REMOVE[@]} -gt 0 ]; then
  info "Stopping and removing containers..."
  for c in "${CONTAINERS_TO_REMOVE[@]}"; do
    # attempt to force remove the container; warn on failure but continue
    if ! $DOCKER_CMD rm -f "$c" >/dev/null 2>&1; then
      warn "Failed to remove container $c"
    fi
  done
fi

# Remove images
if [ ${#IMAGES_TO_REMOVE[@]} -gt 0 ]; then
  info "Removing images..."
  for img in "${IMAGES_TO_REMOVE[@]}"; do
    $DOCKER_CMD rmi -f "$img" >/dev/null 2>&1 || warn "Failed to remove image $img"
  done
fi

info "Teardown complete."
