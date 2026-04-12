@echo off
setlocal

echo [INFO] Checking listener on port 8080...
set FOUND=0

for /f "tokens=5" %%P in ('netstat -ano ^| findstr /R /C:":8080 .*LISTENING"') do (
  set FOUND=1
  echo [INFO] Stopping PID %%P ...
  taskkill /PID %%P /F >nul 2>&1
  if errorlevel 1 (
    echo [WARN] Failed to stop PID %%P. Try Run as Administrator.
  ) else (
    echo [OK] PID %%P stopped.
  )
)

if "%FOUND%"=="0" (
  echo [OK] No process listening on 8080.
)

echo.
echo [INFO] Current 8080 listeners:
netstat -ano | findstr /R /C:":8080 .*LISTENING"
if errorlevel 1 (
  echo [OK] Port 8080 is free.
)

endlocal
pause
