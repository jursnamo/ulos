@echo off
setlocal

set BACKEND_DIR=%~dp0
for %%I in ("%BACKEND_DIR%..") do set JAVA_ROOT=%%~fI
set FRONTEND_DIR=%JAVA_ROOT%\ulosfrontend

echo [INFO] Starting backend terminal...
start "ULOS Backend :8080" cmd /k "cd /d %BACKEND_DIR% && call run_backend_8080.bat"

echo [INFO] Starting frontend terminal...
start "ULOS Frontend :3000 -> :8080" cmd /k "cd /d %FRONTEND_DIR% && call run_frontend_3000.bat"

echo [OK] Workspace started.
endlocal
