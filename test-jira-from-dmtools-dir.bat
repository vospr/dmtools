@echo off
echo ========================================
echo TESTING JIRA FROM DMTOOLS DIRECTORY
echo ========================================
echo.
echo Current directory: %CD%
echo dmtools.env exists: 
if exist "dmtools.env" (echo YES) else (echo NO)
echo.
echo Running jira_get_my_profile...
echo.
java -jar "C:\Users\AndreyPopov\.dmtools\dmtools.jar" mcp jira_get_my_profile
echo.
echo ========================================
pause

