$html = Invoke-WebRequest "http://localhost:8080/register" -SessionVariable sess -UseBasicParsing
$csrf = $html.InputFields | Where-Object { $_.name -eq '_csrf' } | Select-Object -ExpandProperty value
try {
    Invoke-WebRequest -Uri "http://localhost:8080/register" -Method Post -WebSession $sess -Body @{
        username="testuser12"
        fullName="Test User"
        email="test12@test.com"
        phone="0987654312"
        password="test123"
        confirmPassword="test123"
        _csrf=$csrf
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Add-Content res.txt "Register status: 200"
} catch {
    Add-Content res.txt "Register status: "
}

try {
    Invoke-WebRequest -Uri "http://localhost:8080/login" -Method Post -WebSession $sess -Body @{
        username="testuser12"
        password="test123"
        _csrf=$csrf
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Add-Content res.txt "Login status: 200"
} catch {
    Add-Content res.txt "Login status: "
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
    Add-Content res.txt "Change password wrong old status: 200"
} catch {
    Add-Content res.txt "Change password wrong old status: "
}

try {
    Invoke-WebRequest -Uri "http://localhost:8080/profile/change-password" -Method Post -WebSession $sess -Body @{
        oldPassword="test123"
        newPassword="newpassword123"
        confirmPassword="newpassword123"
        _csrf=$csrf3
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Add-Content res.txt "Change password status: 200"
} catch {
    Add-Content res.txt "Change password status: "
}

try {
    Invoke-WebRequest -Uri "http://localhost:8080/register" -Method Post -WebSession $sess -Body @{
        username="testuser13"
        fullName="Test User"
        email="test12@test.com"
        phone="0987654313"
        password="test123"
        confirmPassword="test123"
        _csrf=$csrf3
    } -MaximumRedirection 0 -ErrorAction Stop | Out-Null
    Add-Content res.txt "Duplicate email status: 200"
} catch {
    Add-Content res.txt "Duplicate email status: "
}

