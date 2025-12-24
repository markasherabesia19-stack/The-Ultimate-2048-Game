@echo off
echo Building JAR file...
echo.

REM Clean old build
if exist build rmdir /s /q build
if exist Ultimate2048Game.jar del Ultimate2048Game.jar
mkdir build

REM Compile - using correct path: src\game2048\
echo Compiling Java files...
javac -d build src\game2048\Board.java src\game2048\CustomDialog.java src\game2048\Expectimax.java src\game2048\Game.java src\game2048\GameplayScreen.java src\game2048\Instructions.java src\game2048\Leaderboard.java src\game2048\LeaderboardManager.java src\game2048\Main.java src\game2048\MusicPlayer.java src\game2048\NameInputPanel.java src\game2048\SplashScreen.java src\game2048\Suggestion.java src\game2048\Tile.java

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo Compilation FAILED! Check errors above.
    echo ========================================
    pause
    exit /b 1
)

echo Compilation SUCCESS!

REM Copy resources
echo Copying resources...
xcopy /E /I /Y components build\components

REM Create JAR
echo Creating JAR...
cd build
jar cfm ..\Ultimate2048Game.jar ..\MANIFEST.MF game2048\*.class components\
cd ..

if exist Ultimate2048Game.jar (
    echo.
    echo ========================================
    echo SUCCESS! Ultimate2048Game.jar created!
    echo ========================================
    echo.
    echo To test your game, run:
    echo java -jar Ultimate2048Game.jar
    echo.
) else (
    echo.
    echo ========================================
    echo JAR creation FAILED!
    echo ========================================
)

pause