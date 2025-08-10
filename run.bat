@echo off
REM ===== CONFIG =====
set JAVA_FX_LIB=javafx-sdk-24.0.1\lib
set SRC_DIR=src
set OUT_DIR=out\production\finance_management_app
set MAIN_CLASS=ExpenseManagerAppFX

echo.
echo ===== Compiling source code =====
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

REM Recursively find and compile all Java files
setlocal enabledelayedexpansion
set FILES=
for /r "%SRC_DIR%" %%f in (*.java) do (
    set FILES=!FILES! "%%f"
)
javac --module-path "%JAVA_FX_LIB%" --add-modules javafx.controls,javafx.fxml -d "%OUT_DIR%" !FILES!

if %ERRORLEVEL% neq 0 (
    echo.
    echo Compilation failed. Fix errors above.
    pause
    exit /b
)

echo.
echo ===== Running application =====
java --module-path "%JAVA_FX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%OUT_DIR%;%SRC_DIR%" %MAIN_CLASS%

echo.
pause
