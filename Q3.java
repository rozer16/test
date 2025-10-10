@echo off
title Keep PC Active (11:00 AM - 7:30 PM)
echo This script will keep the system active between 11:00 AM and 7:30 PM.
echo Press Ctrl+C to stop.
echo.

:loop
for /f "tokens=1-2 delims=:" %%a in ("%time%") do (
    set hour=%%a
    set minute=%%b
)
set hour=%hour: =0%
set /a now=(1%hour% %% 100)*60 + (1%minute:~0,2% %% 100)
set /a start=11*60
set /a end=19*60 + 30

if %now% geq %start% if %now% leq %end% (
    echo [%time%] Within work hours — sending keep-alive signal.
    powershell -command "$wshell = New-Object -ComObject wscript.shell; $wshell.SendKeys('+')"
) else (
    echo [%time%] Outside work hours — sleeping.
)

timeout /t 240 >nul
goto loop
