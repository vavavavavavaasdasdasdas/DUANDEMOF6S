$html = Invoke-WebRequest "http://localhost:8080/register" -SessionVariable sess -UseBasicParsing
$csrf = $html.InputFields | Where-Object { $_.name -eq '_csrf' } | Select-Object -ExpandProperty value
try {
    Invoke-WebRequest -Uri "http://localhost:8080/register" -Method Post -WebSession $sess -Body @{
        username="testuser11"
        fullName="Test User"
        email="test11@test.com"
        phone="0987654311"
        password="test123"
        confirmPassword="test123"
        _csrf=$csrf
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Write-Host "Register status: 200"
} catch {
    Write-Host "Register status:" $_.Exception.Response.StatusCode.value__
}

try {
    Invoke-WebRequest -Uri "http://localhost:8080/login" -Method Post -WebSession $sess -Body @{
        username="testuser11"
        password="test123"
        _csrf=$csrf
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Write-Host "Login status: 200"
} catch {
    Write-Host "Login status:" $_.Exception.Response.StatusCode.value__
}

$html3 = Invoke-WebRequest "http://localhost:8080/profile" -WebSession $sess -UseBasicParsing
$csrf3 = $html3.InputFields | Where-Object { $_.name -eq '_csrf' } | Select-Object -ExpandProperty value | Select-Object -First 1

try {
    Invoke-WebRequest -Uri "http://localhost:8080/profile/change-password" -Method Post -WebSession $sess -Body @{
        oldPassword="wrongpassword"
        newPassword="newpassword123"
        confirmPassword="newpassword123"
        _csrf=$csrf3
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Write-Host "Change password wrong old status: 200"
} catch {
    Write-Host "Change password wrong old status:" $_.Exception.Response.StatusCode.value__
}

try {
    Invoke-WebRequest -Uri "http://localhost:8080/profile/change-password" -Method Post -WebSession $sess -Body @{
        oldPassword="test123"
        newPassword="newpassword123"
        confirmPassword="newpassword123"
        _csrf=$csrf3
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Write-Host "Change password status: 200"
} catch {
    Write-Host "Change password status:" $_.Exception.Response.StatusCode.value__
}
