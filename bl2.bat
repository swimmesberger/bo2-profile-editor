@echo off
setlocal enabledelayedexpansion
:: Borderlands 2 Profile Editor — Windows Launcher
:: Double-click this file to open the editor.
:: On first run, Windows will ask for administrator permission — click Yes.

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

set "SETUP_FLAG=%SCRIPT_DIR%bl2.setup"

:: ─────────────────────────────────────────────
:: First-time setup (skipped after first run)
:: ─────────────────────────────────────────────
if not exist "%SETUP_FLAG%" (

    :: Auto-elevate to admin if needed
    net session >nul 2>&1
    if !errorlevel! neq 0 (
        echo First-time setup requires administrator access.
        echo Windows will ask for your permission — click Yes to continue.
        echo.
        powershell -Command "Start-Process -FilePath \"%~f0\" -Verb RunAs"
        exit /b
    )

    echo ============================================
    echo    First-Time Setup
    echo    ^(This only runs once^)
    echo ============================================
    echo.

    :: ── Java ────────────────────────────────────
    java -version >nul 2>&1
    if !errorlevel! equ 0 (
        echo [✓] Java already installed.
    ) else (
        echo [→] Installing Java 11...
        winget install --id EclipseAdoptium.Temurin.11.JDK -e --silent --accept-package-agreements --accept-source-agreements
        if !errorlevel! neq 0 (
            echo [!] winget failed. Please install Java 11 from https://adoptium.net and re-run this file.
            pause
            exit /b 1
        )
        echo [✓] Java 11 installed.
    )

    :: Refresh PATH so java is available in this session
    for /f "usebackq tokens=*" %%P in (`powershell -NoProfile -Command "[System.Environment]::GetEnvironmentVariable('PATH','Machine')"`) do (
        set "PATH=%%P;%PATH%"
    )

    :: ── Build bl2.jar ───────────────────────────
    if exist "%SCRIPT_DIR%bl2.jar" (
        echo [✓] bl2.jar already built.
    ) else (
        echo [→] Building bl2.jar...
        call "%SCRIPT_DIR%gradlew.bat" shadowJar --quiet
        if !errorlevel! neq 0 (
            echo [!] Build failed. Please check that Java is installed and try again.
            pause
            exit /b 1
        )
        echo [✓] bl2.jar built.
    )

    :: Mark setup complete
    echo done > "%SETUP_FLAG%"
    echo.
    echo Setup complete! Future launches will skip this step.
    echo.
    timeout /t 3 /nobreak >nul
)

:: ─────────────────────────────────────────────
:: Launch
:: ─────────────────────────────────────────────
set get=java -jar bl2.jar get
set set=java -jar bl2.jar set
set cd=java -jar bl2.jar change-folder

cls
echo ============================================
echo    Borderlands 2 Profile Editor
echo ============================================
echo.
%get%
echo.
echo --------------------------------------------
echo   Ready. Example commands:
echo     %get%
echo     %set% max
echo     %set% GOLDEN_KEYS max BADASS_RANK max
echo     %cd%
echo   Type 'exit' to quit.
echo --------------------------------------------
echo.

cmd /k "cd /d "%SCRIPT_DIR%" & set get=java -jar bl2.jar get & set set=java -jar bl2.jar set & set cd=java -jar bl2.jar change-folder"
