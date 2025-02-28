@echo off
echo starting Receipt Processor

docker compose down

docker compose up --build -d

echo.
echo wait 5 seconds for containers to initialize
timeout /t 5 /nobreak

docker compose ps

echo.
echo showing logs
echo.
docker compose logs -f