curl.exe -s -c c_user1.txt "http://localhost:8080/login" -o login1.html
$csrf1 = (Get-Content login1.html -Raw) -match 'name="_csrf"\s+value="([^"]+)"' | Out-Null
$csrf1 = $matches[1]
curl.exe -s -X POST -c c_user1.txt -b c_user1.txt -d "username=user&password=user123&_csrf=$csrf1" "http://localhost:8080/login" | Out-Null

curl.exe -s -c c_user2.txt "http://localhost:8080/login" -o login2.html
$csrf2 = (Get-Content login2.html -Raw) -match 'name="_csrf"\s+value="([^"]+)"' | Out-Null
$csrf2 = $matches[1]
curl.exe -s -X POST -c c_user2.txt -b c_user2.txt -d "username=testuser11&password=test123&_csrf=$csrf2" "http://localhost:8080/login" | Out-Null

# User 1 views order 3 (which was created by testuser11?) No, order 1 was admin? Order 2 was user. Order 3 was user.
$res1 = curl.exe -s -b c_user1.txt "http://localhost:8080/orders/success/2" -w '%{http_code}' -o out1.html
$res2 = curl.exe -s -b c_user2.txt "http://localhost:8080/orders/success/2" -w '%{http_code}' -o out2.html
Write-Host "User 1 views Order 2:" $res1
Write-Host "User 2 views Order 2:" $res2
