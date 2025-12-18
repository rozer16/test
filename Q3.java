@echo off
title Keep Alive (Teams) Until 8 PM IST

set LOGFILE=%~dp0keep_awake.log
echo ================================ >> "%LOGFILE%"
echo Script started at %date% %time% >> "%LOGFILE%"

:loop
:: Get current hour
for /f "tokens=1 delims=:" %%H in ("%time%") do set hour=%%H
set hour=%hour: =0%

:: Auto-stop at or after 8 PM
if %hour% GEQ 20 (
    echo [%date% %time%] 8 PM reached. Stopping script. >> "%LOGFILE%"
    exit /b
)

:: Send harmless key (F15 does nothing but counts as activity)
powershell -command "$wshell = New-Object -ComObject wscript.shell; $wshell.SendKeys('{F15}')"

echo [%date% %time%] Activity signal sent (Teams alive). >> "%LOGFILE%"

timeout /t 240 >nul
goto loop
