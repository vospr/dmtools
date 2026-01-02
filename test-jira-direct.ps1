# Test Jira from dmtools directory (where dmtools.env is located)
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "JIRA API TEST (FROM DMTOOLS DIR)" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Working directory: $(Get-Location)" -ForegroundColor Gray
Write-Host "dmtools.env exists: $(Test-Path 'dmtools.env')" -ForegroundColor Gray
Write-Host ""

Write-Host "Testing Jira API (may take 10-20 seconds)..." -ForegroundColor Yellow
$result = java -jar "C:\Users\AndreyPopov\.dmtools\dmtools.jar" mcp jira_get_my_profile 2>&1 | Out-String

# Check result
if ($result -match '"accountId"' -and $result -match '"emailAddress"') {
    Write-Host "`n========================================" -ForegroundColor Green
    Write-Host "SUCCESS! Jira API Connected" -ForegroundColor Green
    Write-Host "========================================`n" -ForegroundColor Green
    
    if ($result -match '"displayName"\s*:\s*"([^"]+)"') {
        Write-Host "Name: $($matches[1])" -ForegroundColor White
    }
    if ($result -match '"emailAddress"\s*:\s*"([^"]+)"') {
        Write-Host "Email: $($matches[1])" -ForegroundColor White
    }
    if ($result -match '"accountId"\s*:\s*"([^"]+)"') {
        $accountId = $matches[1]
        Write-Host "Account ID: $accountId" -ForegroundColor White
        $accountId | Out-File "c:\Users\AndreyPopov\Documents\EPAM\AWS\GenAI Architect\ai-teammate\YOUR_JIRA_ACCOUNT_ID.txt" -Encoding UTF8
    }
    
    $result | Out-File "jira-profile-success.json" -Encoding UTF8
    Write-Host "`nFull response saved to: jira-profile-success.json" -ForegroundColor Gray
    
} elseif ($result -match '401') {
    Write-Host "`n========================================" -ForegroundColor Red
    Write-Host "FAILED: 401 Authentication Error" -ForegroundColor Red
    Write-Host "========================================`n" -ForegroundColor Red
    Write-Host ""
    Write-Host "The Jira API token is expired or invalid." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "ACTION REQUIRED: Generate a new API token" -ForegroundColor Cyan
    Write-Host "1. Go to: https://id.atlassian.com/manage-profile/security/api-tokens" -ForegroundColor White
    Write-Host "2. Click 'Create API token'" -ForegroundColor White
    Write-Host "3. Give it a name (e.g., 'dmtools-ai-teammate')" -ForegroundColor White
    Write-Host "4. Copy the new token" -ForegroundColor White
    Write-Host "5. Update JIRA_API_TOKEN in: c:\Users\AndreyPopov\dmtools\dmtools.env" -ForegroundColor White
    Write-Host ""
    Write-Host "Current token starts with: $(if($env:JIRA_API_TOKEN){$env:JIRA_API_TOKEN.Substring(0,20)}else{'NOT SET'})..." -ForegroundColor Gray
    
} else {
    Write-Host "`n========================================" -ForegroundColor Yellow
    Write-Host "Unknown Result" -ForegroundColor Yellow
    Write-Host "========================================`n" -ForegroundColor Yellow
    Write-Host $result
}

Write-Host "`nPress any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

