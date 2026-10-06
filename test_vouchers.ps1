curl.exe -s -c c_test.txt "http://localhost:8080/login" -o login.html
$csrf = (Get-Content login.html -Raw) -match 'name="_csrf"\s+value="([^"]+)"' | Out-Null
$csrf = $matches[1]
curl.exe -s -X POST -c c_test.txt -b c_test.txt -d "username=user&password=user123&_csrf=$csrf" "http://localhost:8080/login" | Out-Null
curl.exe -s -c c_test.txt -b c_test.txt "http://localhost:8080/" -o home.html
$csrf2 = (Get-Content home.html -Raw) -match 'name="_csrf"\s+content="([^"]+)"' | Out-Null
$csrf2 = $matches[1]

# Make sure cart is not empty
curl.exe -s -X POST -c c_test.txt -b c_test.txt -d "variantId=1&quantity=1&_csrf=$csrf2" "http://localhost:8080/cart/add-ajax" | Out-Null

$vouchers = @("GIAM10K", "GIAM10PT", "HETHAN", "CHUATOINGAY", "HETLUOT", "RIENGTU", "FAKECODE")
foreach ($v in $vouchers) {
    Write-Host "Voucher $v :" $(curl.exe -s -X POST -c c_test.txt -b c_test.txt -d "code=$v&_csrf=$csrf2" "http://localhost:8080/checkout/validate-voucher-ajax")
}
