$ErrorActionPreference = "Continue" # Don't stop on error, show the error

function Request-API {
    param (
        [string]$Method,
        [string]$Endpoint,
        [string]$Body = $null
    )
    $Url = "http://localhost:8080/api$Endpoint"
    try {
        $Params = @{
            Method      = $Method
            Uri         = $Url
            ContentType = "application/json"
        }
        if ($Body) {
            $Params.Body = $Body
        }
        $Response = Invoke-RestMethod @Params
        return $Response
    }
    catch {
        Write-Host "API CALL FAILED: $Method $Endpoint" -ForegroundColor Red
        Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        # Read error stream if possible
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $msg = $reader.ReadToEnd()
        Write-Host "Error Body: $msg" -ForegroundColor Red
        return $null
    }
}

Write-Host "=== 1. Test Join ===" -ForegroundColor Cyan
$joinBody = '{"username":"testuser2", "password":"password123", "name":"TestUser2"}'
$joinRes = Request-API -Method Post -Endpoint "/members" -Body $joinBody
if ($joinRes) {
    Write-Host "Join Success: $joinRes" -ForegroundColor Green
}

Write-Host "`n=== 2. Test Login (Success Case) ===" -ForegroundColor Cyan
$loginBody = '{"username":"testuser2", "password":"password123"}'
$loginRes = Request-API -Method Post -Endpoint "/login" -Body $loginBody
if ($loginRes -and $loginRes.data -match "eyJ") {
    Write-Host "Login Success! Token: $($loginRes.data.Substring(0, 20))..." -ForegroundColor Green
}
else {
    Write-Host "Login Failed or No Token: $loginRes" -ForegroundColor Red
}

Write-Host "`n=== 3. Test Login (Fail Case) ===" -ForegroundColor Cyan
$failBody = '{"username":"testuser2", "password":"wrong"}'
$failRes = Request-API -Method Post -Endpoint "/login" -Body $failBody
if ($failRes -and ($failRes.data -eq "로그인 실패" -or $failRes.data -eq "Login Failed")) {
    Write-Host "Login Fail Handled Correctly: $($failRes.data)" -ForegroundColor Green
}
else {
    Write-Host "Login Fail Unexpected: $failRes" -ForegroundColor Red
}

Write-Host "`n=== 4. Get All Members ===" -ForegroundColor Cyan
$listRes = Request-API -Method Get -Endpoint "/members"
if ($listRes) {
    Write-Host "Members Found: $($listRes.data.Count)" -ForegroundColor Green
    $listRes.data | Format-Table | Out-String | Write-Host
}

# Find ID
$targetUser = $listRes.data | Where-Object { $_.username -eq "testuser2" }
if ($targetUser) {
    $targetId = $targetUser.id
    Write-Host "`n=== 5. Update Member (ID: $targetId) ===" -ForegroundColor Cyan
    $updateBody = '{"name":"UpdatedName", "password":"newpassword"}'
    $updateRes = Request-API -Method Put -Endpoint "/members/$targetId" -Body $updateBody
    
    if ($updateRes -and $updateRes.data.name -eq "UpdatedName") {
        Write-Host "Update Success: $($updateRes.data.name)" -ForegroundColor Green
    }

    Write-Host "`n=== 6. Login with NEW password ===" -ForegroundColor Cyan
    $newLoginBody = '{"username":"testuser2", "password":"newpassword"}'
    $newLoginRes = Request-API -Method Post -Endpoint "/login" -Body $newLoginBody
    if ($newLoginRes -and $newLoginRes.data -match "eyJ") {
        Write-Host "Login with New Password Success" -ForegroundColor Green
    }
    else {
        Write-Host "Login with New Password Failed: $newLoginRes" -ForegroundColor Red
    }

    Write-Host "`n=== 7. Delete Member (ID: $targetId) ===" -ForegroundColor Cyan
    $delRes = Request-API -Method Delete -Endpoint "/members/$targetId"
    Write-Host "Delete Result: $($delRes.data)" -ForegroundColor Green
}
else {
    Write-Host "testuser2 not found, skipping update/delete" -ForegroundColor Yellow
}
