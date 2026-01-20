$ErrorActionPreference = "Stop"

function Request-API {
    param (
        [string]$Method,
        [string]$Endpoint,
        [string]$Body = $null
    )
    $Url = "http://localhost:8080/api$Endpoint"
    try {
        $Params = @{
            Method = $Method
            Uri = $Url
            ContentType = "application/json"
        }
        if ($Body) {
            $Params.Body = $Body
        }
        $Response = Invoke-RestMethod @Params
        return $Response
    } catch {
        Write-Host "Error calling $Method $Endpoint : $_" -ForegroundColor Red
        return $null
    }
}

Write-Host "=== 1. 회원가입 테스트 ===" -ForegroundColor Cyan
$joinBody = '{"username":"testuser", "password":"password123", "name":"TestUser"}'
$joinRes = Request-API -Method Post -Endpoint "/members" -Body $joinBody
if ($joinRes) {
    Write-Host "회원가입 성공: $joinRes" -ForegroundColor Green
}

Write-Host "`n=== 2. 로그인 성공 테스트 ===" -ForegroundColor Cyan
$loginBody = '{"username":"testuser", "password":"password123"}'
$loginRes = Request-API -Method Post -Endpoint "/login" -Body $loginBody
if ($loginRes -and $loginRes.data -match "eyJ") {
    Write-Host "로그인 성공 (토큰 발급됨): $($loginRes.data.Substring(0, 20))..." -ForegroundColor Green
} else {
    Write-Host "로그인 실패 또는 토큰 없음: $loginRes" -ForegroundColor Red
}

Write-Host "`n=== 3. 로그인 실패 테스트 (비번 틀림) ===" -ForegroundColor Cyan
$failBody = '{"username":"testuser", "password":"wrongpassword"}'
$failRes = Request-API -Method Post -Endpoint "/login" -Body $failBody
if ($failRes -and $failRes.data -eq "로그인 실패") {
    Write-Host "로그인 실패 처리 확인됨: $($failRes.data)" -ForegroundColor Green
} else {
    Write-Host "로그인 실패 테스트 이상: $failRes" -ForegroundColor Red
}

Write-Host "`n=== 4. 전체 회원 조회 ===" -ForegroundColor Cyan
$listRes = Request-API -Method Get -Endpoint "/members"
if ($listRes) {
    Write-Host "조회 결과: $($listRes.data | Format-Table | Out-String)" -ForegroundColor Green
}

# ID 찾기
$targetId = ($listRes.data | Where-Object { $_.username -eq "testuser" }).id

if ($targetId) {
    Write-Host "`n=== 5. 회원 수정 (ID: $targetId) ===" -ForegroundColor Cyan
    $updateBody = '{"name":"UpdatedName", "password":"newpassword"}'
    $updateRes = Request-API -Method Put -Endpoint "/members/$targetId" -Body $updateBody
    
    if ($updateRes -and $updateRes.data.name -eq "UpdatedName") {
        Write-Host "수정 성공: $($updateRes.data.name)" -ForegroundColor Green
    }

    Write-Host "`n=== 6. 수정 후 로그인 테스트 ===" -ForegroundColor Cyan
    $newLoginBody = '{"username":"testuser", "password":"newpassword"}'
    $newLoginRes = Request-API -Method Post -Endpoint "/login" -Body $newLoginBody
     if ($newLoginRes -and $newLoginRes.data -match "eyJ") {
        Write-Host "수정된 비밀번호로 로그인 성공" -ForegroundColor Green
    } else {
        Write-Host "수정된 비밀번호로 로그인 실패" -ForegroundColor Red
    }

    Write-Host "`n=== 7. 회원 삭제 (ID: $targetId) ===" -ForegroundColor Cyan
    $delRes = Request-API -Method Delete -Endpoint "/members/$targetId"
    Write-Host "삭제 결과: $($delRes.data)" -ForegroundColor Green
} else {
    Write-Host "testuser를 찾을 수 없어 수정/삭제 테스트 건너뜀" -ForegroundColor Yellow
}
