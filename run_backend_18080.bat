@echo off
setlocal
cd /d "%~dp0"

echo [INFO] Starting backend on port 18080...
call mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=18080"

endlocal
