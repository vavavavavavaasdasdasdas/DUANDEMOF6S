curl.exe -s -c c_test.txt -b c_test.txt -L -X POST -d "receiverName=Test+User&phone=0123456789&email=test@test.com&shippingAddress=Hanoi&paymentMethod=COD&voucherCode=&_csrf=$csrf2" "http://localhost:8080/checkout/place" -o checkout_after.html
Select-String -Path checkout_after.html -Pattern "không đủ tồn kho" -Context 0,0
