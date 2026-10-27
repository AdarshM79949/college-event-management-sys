@echo off
title CampusPulse - Expose to Internet (No Docker)
echo ===============================================================================
echo   CampusPulse: Instant Internet Public URL Generator (No Docker Needed)
echo ===============================================================================
echo.
echo Option 1: Using Cloudflare Tunnel (Free, No Sign-up required)
echo Option 2: Using LocalTunnel (Free via NodeJS/npx)
echo.
echo Launching Cloudflare Tunnel on port 8080...
echo.

where cloudflared >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Cloudflared detected! Generating public HTTPS URL...
    cloudflared tunnel --url http://localhost:8080
    pause
    exit /b
)

where npx >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo NPX detected! Generating public HTTPS URL via localtunnel...
    npx localtunnel --port 8080
    pause
    exit /b
)

echo [INFO] To get a 100%% instant public HTTPS URL without deploying to any server:
echo 1. Download Cloudflare Tunnel (single .exe file): https://github.com/cloudflare/cloudflared/releases
echo    or run: npx localtunnel --port 8080
echo    or run: ngrok http 8080
echo.
pause
