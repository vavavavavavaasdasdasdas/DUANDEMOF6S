curl.exe -s -c c_test.txt "http://localhost:8080/login" -o login.html
$csrf = (Get-Content login.html -Raw) -match 'name="_csrf"\s+value="([^"]+)"' | Out-Null
$csrf = $matches[1]
curl.exe -s -X POST -c c_test.txt -b c_test.txt -d "username=user&password=user123&_csrf=$csrf" "http://localhost:8080/login" | Out-Null
curl.exe -s -c c_test.txt -b c_test.txt "http://localhost:8080/" -o home.html
$csrf2 = (Get-Content home.html -Raw) -match 'name="_csrf"\s+content="([^"]+)"' | Out-Null
$csrf2 = $matches[1]

sqlcmd -S localhost -U sa -P "123456789" -C -d shopbangiay -Q "UPDATE product_variants SET quantity = 10 WHERE id = 2"
curl.exe -s -X POST -c c_test.txt -b c_test.txt -d "variantId=1&quantity=1&_csrf=$csrf2" "http://localhost:8080/cart/add-ajax" | Out-Null
curl.exe -s -X POST -c c_test.txt -b c_test.txt -d "variantId=2&quantity=1&_csrf=$csrf2" "http://localhost:8080/cart/add-ajax" | Out-Null

sqlcmd -S localhost -U sa -P "123456789" -C -d shopbangiay -Q "UPDATE product_variants SET quantity = 0 WHERE id = 2"

$res = curl.exe -s -X POST -c c_test.txt -b c_test.txt -d "receiverName=Test+User&phone=0123456789&email=test@test.com&shippingAddress=Hanoi&paymentMethod=COD&voucherCode=&_csrf=$csrf2" "http://localhost:8080/checkout/place" -o result_checkout_test.html -w '%{http_code}'
Write-Host "Checkout status:" $res

sqlcmd -S localhost -U sa -P "123456789" -C -d shopbangiay -Q "SELECT COUNT(*) AS total_orders FROM orders"
sqlcmd -S localhost -U sa -P "123456789" -C -d shopbangiay -Q "SELECT quantity FROM product_variants WHERE id = 1"
