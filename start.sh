#!/bin/bash

echo "starting Receipt Processor"

sudo docker compose down

sudo docker compose up --build -d

echo
echo "wait 5 seconds for containers to initialize"
sleep 5

sudo docker compose ps

echo
echo "showing logs"
echo
sudo docker compose logs -f
echo "bye"