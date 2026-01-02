@echo off
echo ========================================
echo Rebuilding DMTools with API Fix
echo ========================================
echo.
echo This will take 1-2 minutes...
echo.

cd /d "c:\Users\AndreyPopov\dmtools"
call gradlew.bat clean shadowJar

echo.
echo ========================================
echo Build Complete
echo ========================================
echo.
echo Installing updated JAR...
copy /Y "build\libs\dmtools-v1.7.102-all.jar" "C:\Users\AndreyPopov\.dmtools\dmtools.jar"

echo.
echo Done! Now test with: test-with-api-v3.ps1
pause

