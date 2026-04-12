@echo off
setlocal
cd /d "%~dp0"

echo [INFO] Starting backend on port 8080...
call mvnw.cmd spring-boot:run

endlocal
