#!/bin/bash

cleanup() {
  echo "shutting down containers"
  docker compose down
  echo "bye"
  exit 0
}

echo "starting Receipt Processor"

docker compose down

docker compose up --build -d

echo
echo "wait 5 seconds for containers to initialize"
sleep 5

docker compose ps

echo
echo "showing logs"
echo
docker compose logs -f