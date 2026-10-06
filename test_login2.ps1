$html = Invoke-WebRequest -Uri "http://localhost:8080/login" -UseBasicParsing -SessionVariable session
$csrf = $html.InputFields | Where-Object { $_.name -eq '_csrf' } | Select-Object -ExpandProperty value

# Admin Login
$adminLogin = Invoke-WebRequest -Uri "http://localhost:8080/login" -Method Post -Body @{ username="admin"; password="admin123"; _csrf=$csrf } -WebSession $session -MaximumRedirection 0 -ErrorAction SilentlyContinue
# User Login
$html2 = Invoke-WebRequest -Uri "http://localhost:8080/login" -UseBasicParsing -SessionVariable sessionUser
$csrfUser = $html2.InputFields | Where-Object { $_.name -eq '_csrf' } | Select-Object -ExpandProperty value
$userLogin = Invoke-WebRequest -Uri "http://localhost:8080/login" -Method Post -Body @{ username="user"; password="user123"; _csrf=$csrfUser } -WebSession $sessionUser -MaximumRedirection 0 -ErrorAction SilentlyContinue

Write-Host "--- ANON ---"
Write-Host "/ -> " $(Invoke-WebRequest -Uri "http://localhost:8080/" -UseBasicParsing).StatusCode
Write-Host "/admin -> " $(try{ (Invoke-WebRequest -Uri "http://localhost:8080/admin" -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop).StatusCode }catch{ $_.Exception.Response.StatusCode.value__ })

Write-Host "--- USER ---"
Write-Host "/ -> " $(Invoke-WebRequest -Uri "http://localhost:8080/" -WebSession $sessionUser -UseBasicParsing).StatusCode
Write-Host "/admin -> " $(try{ (Invoke-WebRequest -Uri "http://localhost:8080/admin" -WebSession $sessionUser -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop).StatusCode }catch{ $_.Exception.Response.StatusCode.value__ })

Write-Host "--- ADMIN ---"
Write-Host "/ -> " $(Invoke-WebRequest -Uri "http://localhost:8080/" -WebSession $session -UseBasicParsing).StatusCode
Write-Host "/admin -> " $(try{ (Invoke-WebRequest -Uri "http://localhost:8080/admin" -WebSession $session -UseBasicParsing -MaximumRedirection 0 -ErrorAction Stop).StatusCode }catch{ $_.Exception.Response.StatusCode.value__ })
