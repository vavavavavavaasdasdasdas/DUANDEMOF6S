# Báo cáo nghiệm thu — Tài khoản Admin và hóa đơn chờ POS

Ngày 08/10/2026. Chỉ triển khai hai tính năng được phép. App nền: http://localhost:8081, profile `verify`, database `shop_verify`. Database đã bị DROP ở lần kiểm toán trước nên được tạo lại khi bắt đầu, sau đó giữ và dùng lại qua mọi restart. Không chạy test/migration/cập nhật dữ liệu trên `shopbangiay`.

## Kết quả

Account đã thay placeholder bằng trang thật dùng Admin layout và breadcrumb. POS hold lưu giỏ tạm trong database, không phải Invoice. Nghiệm thu server ghi nhận **46 kiểm tra PASS**, kèm bằng chứng thô bên dưới. Các kiểm tra UI cũng PASS. **Toàn bộ clean test chưa PASS:** 75 tests, 1 failure, 2 errors, 0 skipped; 16 test mới đều không có failure/error. Không khẳng định toàn bộ dự án đã được xác minh.

App chạy từ `target/classes` vừa compile bởi clean test; không nhận đây là JAR/package đã nghiệm thu.

## Bảng nghiệm thu server

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| account-access | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô account-access phía dưới | PASS |
| account-profile-sql | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô account-profile-sql phía dưới | PASS |
| account-duplicate-email | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô account-duplicate-email phía dưới | PASS |
| password-wrong-old | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-wrong-old phía dưới | PASS |
| password-weak | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-weak phía dưới | PASS |
| password-mismatch | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-mismatch phía dưới | PASS |
| password-same | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-same phía dưới | PASS |
| password-change-login | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-change-login phía dưới | PASS |
| account-no-employee | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô account-no-employee phía dưới | PASS |
| csrf | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô csrf phía dưới | PASS |
| hold-save-no-side-effects | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-save-no-side-effects phía dưới | PASS |
| hold-sixth-server-reject | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-sixth-server-reject phía dưới | PASS |
| hold-restore | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-restore phía dưới | PASS |
| hold-reprice-warning | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-reprice-warning phía dưới | PASS |
| hold-zero-stock-warning | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-zero-stock-warning phía dưới | PASS |
| hold-rollback | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-rollback phía dưới | PASS |
| hold-payment | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-payment phía dưới | PASS |
| hold-concurrent-exactly-once | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-concurrent-exactly-once phía dưới | PASS |
| hold-owner-isolation | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-owner-isolation phía dưới | PASS |
| hold-user-forbidden | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-user-forbidden phía dưới | PASS |
| hold-immediate-expiry | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-immediate-expiry phía dưới | PASS |
| regular-pos-regression | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô regular-pos-regression phía dưới | PASS |
| phase1-eight-security | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô phase1-eight-security phía dưới | PASS |
| hold-restart-persistence | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-restart-persistence phía dưới | PASS |
| hold-cleanup-job | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-cleanup-job phía dưới | PASS |
| hold-hours24-live | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-hours24-live phía dưới | PASS |
| hold-update-existing | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-update-existing phía dưới | PASS |
| hold-invalid-voucher-removed | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-invalid-voucher-removed phía dưới | PASS |
| account-unicode | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô account-unicode phía dưới | PASS |
| account-email-format | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô account-email-format phía dưới | PASS |
| account-phone-format | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô account-phone-format phía dưới | PASS |
| password-utf8-limit | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-utf8-limit phía dưới | PASS |
| password-login-raw | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-login-raw phía dưới | PASS |
| hold-customer-payment-note | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-customer-payment-note phía dưới | PASS |
| hold-parallel-limit | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-parallel-limit phía dưới | PASS |
| hold-hours24-immediate-expiry | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-hours24-immediate-expiry phía dưới | PASS |
| hold-expired-not-counted-live | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-expired-not-counted-live phía dưới | PASS |
| hold-hours24-cleanup-job | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-hours24-cleanup-job phía dưới | PASS |
| final-account-access | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô final-account-access phía dưới | PASS |
| password-change-sql | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô password-change-sql phía dưới | PASS |
| new-unit-tests | JUnit + Surefire XML | Bằng chứng thô new-unit-tests phía dưới | PASS |
| no-empty-template-expressions | Quét source / SHA256 | Bằng chứng thô no-empty-template-expressions phía dưới | PASS |
| account-placeholder-removed | Quét source / SHA256 | Bằng chứng thô account-placeholder-removed phía dưới | PASS |
| original-properties-unchanged | Quét source / SHA256 | Bằng chứng thô original-properties-unchanged phía dưới | PASS |
| hold-price-change-payment-blocked | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-price-change-payment-blocked phía dưới | PASS |
| hold-under30-warning | HTTP thật + SELECT độc lập / SQL guard DB_NAME | Bằng chứng thô hold-under30-warning phía dưới | PASS |
| Full clean test | mvnw clean test / profile verify | 75 / 1 failure / 2 errors / 0 skipped — BUILD FAILURE; lỗi cũ ngoài phạm vi | FAIL |

## Bảng nghiệm thu UI

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| Validation client Account | Chrome nhập email/SĐT sai, password ngắn/xác nhận khác | Lỗi hiển thị cạnh field; không submit | PASS |
| Lưu / mở hold | Chọn size/SL, voucher/note, lưu, mở khi giỏ có hàng | Giỏ trống; 3/5→4/5; modal lưu/bỏ/hủy; restore note/voucher; 420000−10000=410000 | PASS |
| Modal đủ 5 | Chọn hold khác khi cart có hàng và Chờ=5/5 | Save hidden, Hủy/Bỏ giỏ còn hoạt động | PASS |
| Loading / chống gửi lặp | Đọc trạng thái trong submit; concurrent checkout thật | disabled=true, aria-busy=true; hai checkout chỉ một thành công | PASS |
| Confirm xóa / focus | Dialog, Escape rồi xác nhận | Dialog có mã hold; trả focus; counter 5/5→4/5 | PASS |
| Sidebar thấp | 1280×480, cuộn tới cuối | overflow-y:auto; scrollHeight=1048; Account 356–394px; Logout 423–464px | PASS |
| Topbar Account/logout | DOM và HTTP | Link /admin/account; POST logout + CSRF; sau logout protected=302 /login | PASS |
| Responsive | Chrome 375/768/1280×480 | scrollWidth 360/753/1265; overflow=false cả 6 trường hợp, ảnh đã xem | PASS |
| JS console | pageerror/console-error + CLI console error | errors=[]; Errors:0 | PASS |
| Countdown dưới 30 phút | SQL đặt còn 20 phút; Chrome + screenshot | class text-warning-emphasis; còn 0 giờ 20 phút; đã khôi phục expiry | PASS |
| Design system | Đọc template và danh sách tệp sửa | Không tạo CSS riêng/CSS inline; dùng CSS và component hiện có | PASS |
| Premium strict static audit | audit_project.py --mode strict --no-write | Exit 1, 25 detector flags; chưa nhận là strict-clean | FAIL |

### Bằng chứng browser thô

```json
{"clientProfile":{"email":"Giá trị không đúng định dạng.","phone":"Giá trị không đúng định dạng.","url":"http://localhost:8081/admin/account"},"clientPassword":{"length":"Ít nhất 8 ký tự.","match":"Xác nhận mật khẩu không khớp."},"initialCount":"Chờ: 3/5","savedCount":"Chờ: 4/5","savedCode":"HC-2E848AA2","switchVisible":true,"restoredFocus":"HC-2E848AA2","restored":{"note":"Browser hold","voucher":"AHVERIFY","total":"410.000 đ","count":"Chờ: 5/5"},"sidebar":{"height":480,"scrollHeight":1048,"overflowY":"auto","account":{"top":356,"bottom":394},"logout":{"top":423,"bottom":464}},"errors":[]}

{
  "fullModal": {
    "visible": true,
    "saveHidden": true,
    "discardVisible": true,
    "message": "Đã đủ 5 hóa đơn chờ. Hãy thanh toán hoặc xóa bớt một hóa đơn."
  },
  "loading": {
    "disabled": true,
    "busy": "true"
  },
  "confirmation": {
    "visible": true,
    "title": "Xóa HC-7994186C?"
  },
  "focusRestored": true,
  "countBefore": "Chờ: 5/5",
  "countAfter": "Chờ: 4/5",
  "widths": [
    {
      "page": "/admin/pos",
      "width": 375,
      "scrollWidth": 360,
      "overflow": false
    },
    {
      "page": "/admin/pos",
      "width": 768,
      "scrollWidth": 753,
      "overflow": false
    },
    {
      "page": "/admin/pos",
      "width": 1280,
      "scrollWidth": 1265,
      "overflow": false
    },
    {
      "page": "/admin/account",
      "width": 375,
      "scrollWidth": 360,
      "overflow": false
    },
    {
      "page": "/admin/account",
      "width": 768,
      "scrollWidth": 753,
      "overflow": false
    },
    {
      "page": "/admin/account",
      "width": 1280,
      "scrollWidth": 1265,
      "overflow": false
    }
  ]
}
```

Static audit: [JSON](../verify/evidence/account-hold-premium-audit.json). Các flags gồm nút dùng data-* handlers do admin-catalog/admin-pos sở hữu (runtime đã bấm được), dialog business variant, textarea thừa hưởng resize:none/auto-grow chung; nhiều flags thuộc module cũ ngoài phạm vi. Không đổi nút confirm thành submit hoặc thêm CSS inline chỉ để vượt detector. Không tuyên bố strict audit sạch.

| Trang | 375px | 768px | 1280px |
|---|---|---|---|
| Account | [OK](../verify/evidence/browser/ah-account-375.png) | [OK](../verify/evidence/browser/ah-account-768.png) | [OK](../verify/evidence/browser/ah-account-1280.png) |
| POS | [OK](../verify/evidence/browser/ah-pos-375.png) | [OK](../verify/evidence/browser/ah-pos-768.png) | [OK](../verify/evidence/browser/ah-pos-1280.png) |

[Account desktop](../verify/evidence/browser/ah-account-desktop-final.png) · [POS desktop](../verify/evidence/browser/ah-pos-desktop-final.png) · [Sidebar thấp](../verify/evidence/browser/ah-sidebar-low-height.png) · [Cảnh báo dưới 30 phút](../verify/evidence/browser/ah-warning-under30.png).

## Quy tắc triển khai

- Clock bean dùng shop.timezone=Asia/Ho_Chi_Minh. END_OF_DAY hết hạn 00:00 ngày kế tiếp; HOURS_24 đúng 86400 giây. Cleanup mặc định PT5M; verify đã chạy PT2S/PT10S và restart về END_OF_DAY/PT5M.
- Lưu hold khóa User để kiểm tra tối đa 5 hold còn hạn theo tài khoản. List/open/payment lọc expires_at > now; chủ khác trả 404. Checkout khóa hold, gọi **OrderService.createPosOrder hiện có**, rồi xóa hold trong cùng transaction. Lỗi rollback giữ hold; không viết lại logic tạo đơn.
- Hold không lưu giá. PricingService tính lại khi mở. Session chỉ giữ bản giá đã hiển thị để nhận diện thay đổi; sau restart khi giá cũ không còn thì yêu cầu xác nhận giá hiện tại, không bịa giá cũ.
- Voucher load được kiểm tra bằng VoucherService.validateAndCalculate, lỗi thì bỏ và flash. Consume chỉ thuộc OrderService transaction. Lưu hold không consume voucher/stock.
- Account ưu tiên Employee, fallback User. Authentication được sửa để active standalone User thiếu Employee vẫn đăng nhập và vào Account; linked Employee/Customer inactive vẫn chặn. POS payment vẫn yêu cầu Employee active.
- created_at mới được lưu trên User; ngày tạo lịch sử không có dữ liệu thì hiển thị Chưa có dữ liệu. Họ tên admin seed cũ đã mất dấu trong varchar trước migration, không bịa sửa dữ liệu lịch sử; profile mới tiếng Việt đã SELECT đúng cả Employee và User.
- Migration `database/admin-account-pos-hold.sql` đã chạy **chỉ trên shop_verify**. Database chính chưa migrate; application.properties gốc vẫn schema-auto-update như trước. Muốn chạy bản mới trên database chính cần áp dụng migration theo quy trình triển khai riêng.

## File:dòng — bằng chứng source

```text
src/main/resources/templates/admin/pos.html:11:<aside class="admin-panel pos-basket" aria-label="Giỏ bán hàng tại quầy"><div class="panel-heading"><h2>Hóa đơn chờ</h2><span id="pos-hold-counter" th:text="'Chờ: '+${heldOrders.size()}+'/5'"></span></div>
src/main/resources/templates/admin/pos.html:15:<form th:action="@{/admin/pos/holds/{id}/open(id=${h.id})}" method="post" data-held-open novalidate>
src/main/java/com/example/demodatn/thongke/AdminPageController.java:23:    @GetMapping("/admin/account")
src/main/java/com/example/demodatn/thongke/AdminPageController.java:25:        populate(principal,model);return "admin/account";
src/main/java/com/example/demodatn/thongke/AdminPageController.java:27:    @PostMapping("/admin/account/profile")
src/main/java/com/example/demodatn/thongke/AdminPageController.java:31:        if(errors.hasErrors()){populate(principal,model);return "admin/account";}
src/main/java/com/example/demodatn/thongke/AdminPageController.java:32:        flash.addFlashAttribute("success","Đã cập nhật thông tin tài khoản.");return "redirect:/admin/account";
src/main/java/com/example/demodatn/thongke/AdminPageController.java:34:    @PostMapping("/admin/account/password")
src/main/java/com/example/demodatn/thongke/AdminPageController.java:38:        if(errors.hasErrors()) {populate(principal,model);return "admin/account";}
src/main/java/com/example/demodatn/thongke/AdminPageController.java:39:        flash.addFlashAttribute("success","Đổi mật khẩu thành công.");return "redirect:/admin/account";
src/main/java/com/example/demodatn/nhanvien/AdminAccountService.java:32:        if(users.otherAccountsWithEmail(email,user.getId())>0)
src/main/java/com/example/demodatn/nhanvien/AdminAccountService.java:51:        if(currentTooLong || !encoder.matches(form.getCurrentPassword(),user.getPassword()))
src/main/java/com/example/demodatn/nhanvien/AdminAccountService.java:56:        if(!newTooLong && encoder.matches(form.getNewPassword(),user.getPassword()))
src/main/java/com/example/demodatn/nhanvien/AdminAccountService.java:58:        if(!errors.hasErrors()) {user.setPassword(encoder.encode(form.getNewPassword()));users.save(user);}
src/main/java/com/example/demodatn/nhanvien/UserRepository.java:22:    long otherAccountsWithEmail(@org.springframework.data.repository.query.Param("email") String email,
src/main/resources/templates/fragments/admin-topbar.html:5:    <div class="d-flex align-items-center gap-2 flex-wrap"><a class="admin-user" th:href="@{/admin/account}" aria-label="Tài khoản"><span class="admin-avatar" aria-hidden="true">A</span><span th:text="${#authentication.name}">Admin</span><span class="admin-role">Tài khoản</span></a><form th:action="@{/logout}" method="post" novalidate><button class="btn btn-sm btn-outline-secondary" type="submit">Đăng xuất</button></form></div>
src/main/resources/templates/admin/account.html:12:<form th:action="@{/admin/account/profile}" method="post" th:object="${profileForm}" data-admin-form novalidate>
src/main/resources/templates/admin/account.html:17:<div class="col-lg-6"><div class="admin-panel"><div class="panel-heading"><h2>Đổi mật khẩu</h2></div><form th:action="@{/admin/account/password}" th:object="${passwordForm}" method="post" data-admin-form novalidate>
src/main/resources/templates/admin/account.html:18:<div class="sales-field"><label for="currentPassword">Mật khẩu hiện tại</label><input id="currentPassword" name="currentPassword" type="password" class="form-control" required maxlength="72" data-byte-limit="72" autocomplete="current-password" aria-describedby="currentPassword-error"><p id="currentPassword-error" class="field-error"><span th:errors="*{currentPassword}"></span></p></div>
src/main/resources/templates/admin/account.html:19:<div class="sales-field"><label for="newPassword">Mật khẩu mới</label><input id="newPassword" name="newPassword" type="password" class="form-control" required minlength="8" maxlength="72" data-byte-limit="72" autocomplete="new-password" aria-describedby="newPassword-error"><p id="newPassword-error" class="field-error"><span th:errors="*{newPassword}"></span></p></div>
src/main/resources/templates/admin/account.html:20:<div class="sales-field"><label for="confirmPassword">Xác nhận mật khẩu mới</label><input id="confirmPassword" name="confirmPassword" type="password" class="form-control" required maxlength="72" data-byte-limit="72" autocomplete="new-password" data-match="newPassword" aria-describedby="confirmPassword-error"><p id="confirmPassword-error" class="field-error"><span th:errors="*{confirmPassword}"></span></p></div>
src/main/java/com/example/demodatn/config/PosHoldConfig.java:8:import org.springframework.scheduling.annotation.EnableScheduling;
src/main/java/com/example/demodatn/config/PosHoldConfig.java:10:@Configuration @EnableScheduling
src/main/java/com/example/demodatn/config/PosHoldConfig.java:13:    public Clock shopClock(@Value("${shop.timezone:Asia/Ho_Chi_Minh}") String zone) {
src/main/java/com/example/demodatn/banhang/PosHeldOrderRepository.java:11:    long countByStaffUser_IdAndExpiresAtAfter(Integer staffId,LocalDateTime now);
src/main/java/com/example/demodatn/banhang/PosHeldOrderRepository.java:14:    @Query("select h from PosHeldOrder h where h.id=:id and h.staffUser.username=:username and h.expiresAt>:now")
src/main/java/com/example/demodatn/banhang/PosHeldOrderRepository.java:15:    Optional<PosHeldOrder> lockActive(@Param("id") Long id,@Param("username") String username,@Param("now") LocalDateTime now);
src/main/java/com/example/demodatn/banhang/PosHoldService.java:19:    public static final String LIMIT_MESSAGE="Đã đủ 5 hóa đơn chờ. Hãy thanh toán hoặc xóa bớt một hóa đơn.";
src/main/java/com/example/demodatn/banhang/PosHoldService.java:28:    @Value("${shop.pos.hold.expiry-mode:END_OF_DAY}") private String expiryMode="END_OF_DAY";
src/main/java/com/example/demodatn/banhang/PosHoldService.java:52:        return holds.lockActive(id,username,now()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Hóa đơn chờ không tồn tại hoặc đã hết hạn."));
src/main/java/com/example/demodatn/banhang/PosHoldService.java:65:            if(holds.countByStaffUser_IdAndExpiresAtAfter(user.getId(),now())>=5)throw new IllegalArgumentException(LIMIT_MESSAGE);
src/main/java/com/example/demodatn/banhang/PosHoldService.java:102:    public void delete(String username,Long id) {holds.delete(locked(username,id));}
src/main/java/com/example/demodatn/banhang/PosHoldService.java:114:        var order=orders.createPosOrder(cart,customer,employee,request.getVoucherCode(),request.getPaymentMethod(),"Tại quầy",request.getCashReceived());
src/main/java/com/example/demodatn/banhang/PosHoldService.java:115:        holds.delete(hold);holds.flush();
src/main/java/com/example/demodatn/banhang/PosHoldService.java:121:        var expired=holds.findByExpiresAtLessThanEqual(now());holds.deleteAll(expired);holds.flush();return expired.size();
src/main/java/com/example/demodatn/banhang/PosHoldCleanup.java:10:    @Scheduled(fixedDelayString="${shop.pos.hold.cleanup-interval:PT5M}")
```

## SELECT tự viết và script SQL nghiệm thu

### ah_final_profile_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT u.username,u.full_name user_name,u.email user_email,u.phone user_phone,e.full_name employee_name,e.email employee_email,e.phone employee_phone,u.created_at FROM users u LEFT JOIN employees e ON e.user_id=u.id WHERE u.username='testadmin_verify' FOR JSON PATH;
```

### ah_final_password_before

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT username,LEFT(password,4) prefix,LEN(password) hash_length,CONVERT(varchar(64),HASHBYTES('SHA2_256',password),2) fingerprint FROM users WHERE username='testadmin_verify' FOR JSON PATH;
```

### ah_expired_not_counted_live

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT h.id,h.expires_at,CASE WHEN h.expires_at>SYSDATETIME() THEN 1 ELSE 0 END active FROM pos_held_orders h JOIN users u ON u.id=h.staff_user_id WHERE u.username='staffb_verify' ORDER BY h.id FOR JSON PATH;
```

### ah_rollback_hold

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT id FROM pos_held_orders WHERE id=9 FOR JSON PATH;
```

### ah_rollback_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_pay_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_concurrent_before

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_concurrent_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_rollback_before

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_save_before

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_save_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_hours24_cleanup_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT id FROM pos_held_orders WHERE id=24 FOR JSON PATH;
```

### ah_pay_order

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT TOP 1 o.id,o.order_type,o.total_amount,o.discount_amount,o.final_amount,o.employee_id,COUNT(d.id) details,(SELECT COUNT(*) FROM invoices i WHERE i.order_id=o.id) invoices FROM orders o JOIN order_details d ON d.order_id=o.id WHERE o.order_type='POS' GROUP BY o.id,o.order_type,o.total_amount,o.discount_amount,o.final_amount,o.employee_id ORDER BY o.id DESC FOR JSON PATH;
```

### ah_final_duplicate_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT u.username,u.full_name user_name,u.email user_email,u.phone user_phone,e.full_name employee_name,e.email employee_email,e.phone employee_phone,u.created_at FROM users u LEFT JOIN employees e ON e.user_id=u.id WHERE u.username='testadmin_verify' FOR JSON PATH;
```

### ah_final_password_after

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT username,LEFT(password,4) prefix,LEN(password) hash_length,CONVERT(varchar(64),HASHBYTES('SHA2_256',password),2) fingerprint FROM users WHERE username='testadmin_verify' FOR JSON PATH;
```

### ah_after_cleanup_expired

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT id FROM pos_held_orders WHERE id=11 FOR JSON PATH;
```

### ah_hours24_expired_before_job

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT id,expires_at FROM pos_held_orders WHERE id=24 FOR JSON PATH;
```

### ah_pay_before

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT (SELECT COUNT(*) FROM orders) orders,(SELECT COUNT(*) FROM order_details) details,(SELECT COUNT(*) FROM invoices) invoices,(SELECT COUNT(*) FROM inventory_logs) logs,(SELECT used_count FROM vouchers WHERE code='AHVERIFY') voucher_used,(SELECT id,quantity,price FROM product_variants WHERE product_id=(SELECT id FROM products WHERE code='AHVERIFY') FOR JSON PATH) stock FOR JSON PATH;
```

### ah_hours24_expiry

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT id,created_at,expires_at,DATEDIFF_BIG(second,created_at,expires_at) elapsed_seconds FROM pos_held_orders WHERE id=14 FOR JSON PATH;
```

### ah_expired_before_cleanup

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT id,expires_at FROM pos_held_orders WHERE id=11 FOR JSON PATH;
```

### ah_final_held_rows

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT u.username,h.id,h.code,h.created_at,h.expires_at,(SELECT COUNT(*) FROM pos_held_items i WHERE i.held_id=h.id) item_rows FROM pos_held_orders h JOIN users u ON u.id=h.staff_user_id ORDER BY u.username,h.id FOR JSON PATH;
```

### ah_final_profile_before

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT u.username,u.full_name user_name,u.email user_email,u.phone user_phone,e.full_name employee_name,e.email employee_email,e.phone employee_phone,u.created_at FROM users u LEFT JOIN employees e ON e.user_id=u.id WHERE u.username='testadmin_verify' FOR JSON PATH;
```

### ah_hours24_before_expiry

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT id,created_at,expires_at,DATEDIFF_BIG(second,created_at,expires_at) elapsed_seconds FROM pos_held_orders WHERE id=24 FOR JSON PATH;
```

### ah_saved_hold

```sql
SET NOCOUNT ON;
IF DB_NAME() <> 'shop_verify' THROW 51000, 'Wrong database', 1;
SELECT h.id,h.code,h.customer_label,h.voucher_code,h.note,h.created_at,h.expires_at,COUNT(i.id) item_rows FROM pos_held_orders h JOIN pos_held_items i ON i.held_id=h.id WHERE h.id=9 GROUP BY h.id,h.code,h.customer_label,h.voucher_code,h.note,h.created_at,h.expires_at FOR JSON PATH;
```

## Output thô từng kiểm tra

### account-access — PASS

```json
{
  "http": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454358158944700_ah_admin_GET__admin_account.html"
  },
  "sql": [
    {
      "full_name": "Qu?n tr? h? th?ng"
    }
  ]
}
```

### account-profile-sql — PASS

```json
{
  "before": [
    {
      "username": "testadmin_verify",
      "user_name": "Nhân viên kiểm thử",
      "user_email": "accountupdated@verify.test",
      "user_phone": "0908111011",
      "employee_name": "Nhân viên kiểm thử",
      "employee_email": "accountupdated@verify.test",
      "employee_phone": "0908111011",
      "created_at": "2026-10-08T16:59:06.7184934"
    }
  ],
  "after": [
    {
      "username": "testadmin_verify",
      "user_name": "Nhân viên QA cập nhật",
      "user_email": "account-final@verify.test",
      "user_phone": "0908111044",
      "employee_name": "Nhân viên QA cập nhật",
      "employee_email": "account-final@verify.test",
      "employee_phone": "0908111044",
      "created_at": "2026-10-08T16:59:06.7184934"
    }
  ],
  "http": {
    "status": 302,
    "location": "http://localhost:8081/admin/account",
    "bodyFile": "verify\\evidence\\ah_1791455283750398700_ah_staff_POST__admin_account_profile.html"
  },
  "role": [
    {
      "username": "testadmin_verify",
      "role": "ROLE_ADMIN"
    }
  ]
}
```

### account-duplicate-email — PASS

```json
{
  "http": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455284352738600_ah_staff_POST__admin_account_profile.html"
  },
  "sqlAfter": [
    {
      "username": "testadmin_verify",
      "user_name": "Nhân viên QA cập nhật",
      "user_email": "account-final@verify.test",
      "user_phone": "0908111044",
      "employee_name": "Nhân viên QA cập nhật",
      "employee_email": "account-final@verify.test",
      "employee_phone": "0908111044",
      "created_at": "2026-10-08T16:59:06.7184934"
    }
  ]
}
```

### password-wrong-old — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454220802088700_ah_staff_POST__admin_account_password.html"
}
```

### password-weak — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454221001343000_ah_staff_POST__admin_account_password.html"
}
```

### password-mismatch — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454221104857300_ah_staff_POST__admin_account_password.html"
}
```

### password-same — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454221311142600_ah_staff_POST__admin_account_password.html"
}
```

### password-change-login — PASS

```json
{
  "change": {
    "status": 302,
    "location": "http://localhost:8081/admin/account",
    "bodyFile": "verify\\evidence\\ah_1791454221767144800_ah_staff_POST__admin_account_password.html"
  },
  "stillLoggedIn": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454222042585200_ah_staff_GET__admin_account.html"
  },
  "old": {
    "status": 302,
    "location": "http://localhost:8081/login?error",
    "bodyFile": "verify\\evidence\\ah_staff_login.html"
  },
  "new": {
    "status": 302,
    "location": "http://localhost:8081/admin",
    "bodyFile": "verify\\evidence\\ah_staff_login.html"
  },
  "adminUnchanged": {
    "status": 302,
    "location": "http://localhost:8081/admin",
    "bodyFile": "verify\\evidence\\ah_admin2_login.html"
  },
  "sqlBefore": [
    {
      "username": "admin",
      "prefix": "$2a$",
      "hash_length": 60
    },
    {
      "username": "testadmin_verify",
      "prefix": "$2a$",
      "hash_length": 60
    }
  ],
  "sqlAfter": [
    {
      "username": "admin",
      "prefix": "$2a$",
      "hash_length": 60
    },
    {
      "username": "testadmin_verify",
      "prefix": "$2a$",
      "hash_length": 60
    }
  ]
}
```

### account-no-employee — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454358048586800_ah_noemployee_GET__admin_account.html"
}
```

### csrf — PASS

```json
[
  {
    "url": "/admin/account/profile",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454223206723400_ah_staff_POST__admin_account_profile.html"
  },
  {
    "url": "/admin/account/password",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454223253422400_ah_staff_POST__admin_account_password.html"
  },
  {
    "url": "/admin/pos/holds",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454223278481500_ah_staff_POST__admin_pos_holds.html"
  },
  {
    "url": "/admin/pos/new",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454223323035500_ah_staff_POST__admin_pos_new.html"
  },
  {
    "url": "/admin/pos/holds/1/open",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454223350121900_ah_staff_POST__admin_pos_holds_1_open.html"
  },
  {
    "url": "/admin/pos/holds/1/delete",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454223386068200_ah_staff_POST__admin_pos_holds_1_delete.html"
  },
  {
    "url": "/admin/pos/checkout",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454223413092000_ah_staff_POST__admin_pos_checkout.html"
  }
]
```

### hold-save-no-side-effects — PASS

```json
{
  "http": {
    "status": 302,
    "location": "http://localhost:8081/admin/pos",
    "bodyFile": "verify\\evidence\\ah_1791454224169373800_ah_staff_POST__admin_pos_holds.html"
  },
  "before": {
    "orders": 3,
    "details": 4,
    "invoices": 3,
    "logs": 4,
    "voucher_used": 2,
    "stock": [
      {
        "id": 35,
        "quantity": 20,
        "price": 200000
      },
      {
        "id": 36,
        "quantity": 20,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 3,
    "details": 4,
    "invoices": 3,
    "logs": 4,
    "voucher_used": 2,
    "stock": [
      {
        "id": 35,
        "quantity": 20,
        "price": 200000
      },
      {
        "id": 36,
        "quantity": 20,
        "price": 200000
      }
    ]
  },
  "held": [
    {
      "id": 9,
      "code": "HC-7F60CC46",
      "customer_label": "Khách lẻ",
      "voucher_code": "AHVERIFY",
      "note": "Two lines",
      "created_at": "2026-10-08T17:10:24.2566365",
      "expires_at": "2026-10-09T00:00:00",
      "item_rows": 2
    }
  ]
}
```

### hold-sixth-server-reject — PASS

```json
{
  "responses": [
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091011641400_ah_limit_0_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091012647100_ah_limit_1_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091012647100_ah_limit_2_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091012647100_ah_limit_3_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091013621900_ah_limit_4_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091013621900_ah_limit_5_POST__admin_pos_holds.html"
    }
  ],
  "rejections": [
    {
      "status": 200,
      "location": null,
      "bodyFile": "verify\\evidence\\ah_1791455091124092900_ah_limit_0_GET__admin_pos.html"
    }
  ],
  "count": 5,
  "before": {
    "orders": 6,
    "details": 8,
    "invoices": 6,
    "logs": 8,
    "voucher_used": 4,
    "stock": [
      {
        "id": 35,
        "quantity": 17,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 6,
    "details": 8,
    "invoices": 6,
    "logs": 8,
    "voucher_used": 4,
    "stock": [
      {
        "id": 35,
        "quantity": 17,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  }
}
```

### hold-restore — PASS

```json
{
  "status": 302,
  "location": "http://localhost:8081/admin/pos",
  "bodyFile": "verify\\evidence\\ah_1791454227537664900_ah_staff_POST__admin_pos_holds_9_open.html"
}
```

### hold-reprice-warning — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454228184274300_ah_staff_GET__admin_pos.html"
}
```

### hold-zero-stock-warning — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454228766200000_ah_staff_GET__admin_pos.html"
}
```

### hold-rollback — PASS

```json
{
  "http": {
    "status": 302,
    "location": "http://localhost:8081/admin/pos",
    "bodyFile": "verify\\evidence\\ah_1791454229221674400_ah_staff_POST__admin_pos_checkout.html"
  },
  "before": {
    "orders": 3,
    "details": 4,
    "invoices": 3,
    "logs": 4,
    "voucher_used": 2,
    "stock": [
      {
        "id": 35,
        "quantity": 20,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 0,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 3,
    "details": 4,
    "invoices": 3,
    "logs": 4,
    "voucher_used": 2,
    "stock": [
      {
        "id": 35,
        "quantity": 20,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 0,
        "price": 200000
      }
    ]
  },
  "hold": [
    {
      "id": 9
    }
  ]
}
```

### hold-payment — PASS

```json
{
  "http": {
    "status": 302,
    "location": "http://localhost:8081/admin/pos/success/58",
    "bodyFile": "verify\\evidence\\ah_1791454230354931700_ah_staff_POST__admin_pos_checkout.html"
  },
  "before": {
    "orders": 3,
    "details": 4,
    "invoices": 3,
    "logs": 4,
    "voucher_used": 2,
    "stock": [
      {
        "id": 35,
        "quantity": 20,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 20,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 4,
    "details": 6,
    "invoices": 4,
    "logs": 6,
    "voucher_used": 3,
    "stock": [
      {
        "id": 35,
        "quantity": 19,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  },
  "sale": [
    {
      "id": 58,
      "order_type": "POS",
      "total_amount": 410000,
      "discount_amount": 10000,
      "final_amount": 400000,
      "employee_id": 21,
      "details": 2,
      "invoices": 1
    }
  ],
  "manual": "210000+200000-10000=400000"
}
```

### hold-concurrent-exactly-once — PASS

```json
{
  "responses": [
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos/success/59",
      "bodyFile": "verify\\evidence\\ah_1791454232254912700_ah_concurrent1_POST__admin_pos_checkout.html"
    },
    {
      "status": 404,
      "location": null,
      "bodyFile": "verify\\evidence\\ah_1791454232255900900_ah_concurrent2_POST__admin_pos_checkout.html"
    }
  ],
  "before": {
    "orders": 4,
    "details": 6,
    "invoices": 4,
    "logs": 6,
    "voucher_used": 3,
    "stock": [
      {
        "id": 35,
        "quantity": 19,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 5,
    "details": 7,
    "invoices": 5,
    "logs": 7,
    "voucher_used": 3,
    "stock": [
      {
        "id": 35,
        "quantity": 18,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  }
}
```

### hold-owner-isolation — PASS

```json
[
  {
    "action": "open",
    "status": 404,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454232782933200_ah_b_POST__admin_pos_holds_11_open.html"
  },
  {
    "action": "delete",
    "status": 404,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454232980330800_ah_b_POST__admin_pos_holds_11_delete.html"
  }
]
```

### hold-user-forbidden — PASS

```json
[
  {
    "url": "/admin/pos",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454233184346800_ah_user_GET__admin_pos.html"
  },
  {
    "url": "/admin/pos/holds",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454233239158600_ah_user_POST__admin_pos_holds.html"
  },
  {
    "url": "/admin/pos/holds/11/open",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454233326259000_ah_user_POST__admin_pos_holds_11_open.html"
  },
  {
    "url": "/admin/pos/holds/11/delete",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454233400854200_ah_user_POST__admin_pos_holds_11_delete.html"
  },
  {
    "url": "/admin/pos/holds/review",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454233477555500_ah_user_POST__admin_pos_holds_review.html"
  }
]
```

### hold-immediate-expiry — PASS

```json
{
  "pay": {
    "status": 404,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454234426341700_ah_staff_POST__admin_pos_checkout.html"
  },
  "open": {
    "status": 404,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454234528866600_ah_staff_POST__admin_pos_holds_11_open.html"
  },
  "rowStillBeforeJob": [
    {
      "id": 11,
      "expires_at": "2026-10-08T17:09:34.4203275"
    }
  ],
  "list": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454234612486800_ah_staff_GET__admin_pos.html"
  }
}
```

### regular-pos-regression — PASS

```json
{
  "http": {
    "status": 302,
    "location": "http://localhost:8081/admin/pos/success/60",
    "bodyFile": "verify\\evidence\\ah_1791454235305492700_ah_staff_POST__admin_pos_checkout.html"
  },
  "before": {
    "orders": 5,
    "details": 7,
    "invoices": 5,
    "logs": 7,
    "voucher_used": 3,
    "stock": [
      {
        "id": 35,
        "quantity": 18,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 6,
    "details": 8,
    "invoices": 6,
    "logs": 8,
    "voucher_used": 4,
    "stock": [
      {
        "id": 35,
        "quantity": 17,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  }
}
```

### phase1-eight-security — PASS

```json
[
  {
    "case": "P1.1",
    "pass": true,
    "http": {
      "status": 302,
      "location": "http://localhost:8081/login",
      "bodyFile": "verify\\evidence\\ah_1791455285914905300_ah_anon_GET__admin.html"
    }
  },
  {
    "case": "P1.2",
    "pass": true,
    "http": {
      "status": 302,
      "location": "http://localhost:8081/login?error",
      "bodyFile": "verify\\evidence\\ah_1791455285948816900_ah_wrong_POST__login.html"
    }
  },
  {
    "case": "P1.3",
    "pass": true,
    "http": [
      {
        "status": 302,
        "location": "http://localhost:8081/admin",
        "bodyFile": "verify\\evidence\\ah_1791455286130100600_ah_admin_POST__login.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455286242606800_ah_admin_GET__admin.html"
      }
    ]
  },
  {
    "case": "P1.4",
    "pass": true,
    "http": [
      {
        "status": 302,
        "location": "http://localhost:8081/",
        "bodyFile": "verify\\evidence\\ah_1791455286367310700_ah_user_POST__login.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455286486779400_ah_user_GET__.html"
      }
    ]
  },
  {
    "case": "P1.5",
    "pass": true,
    "http": [
      {
        "status": 403,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455286649311900_ah_user_GET__admin.html"
      },
      {
        "status": 403,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455286677311400_ah_user_GET__admin_products.html"
      }
    ]
  },
  {
    "case": "P1.6",
    "pass": true,
    "http": [
      {
        "status": 302,
        "location": "http://localhost:8081/admin/products-crud",
        "bodyFile": "verify\\evidence\\ah_1791455286724994300_ah_admin_GET__admin_products.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455286749993700_ah_admin_GET__admin_products_crud.html"
      }
    ]
  },
  {
    "case": "P1.7",
    "pass": true,
    "http": [
      {
        "status": 302,
        "location": "http://localhost:8081/login?logout",
        "bodyFile": "verify\\evidence\\ah_1791455286814997700_ah_admin_POST__logout.html"
      },
      {
        "status": 302,
        "location": "http://localhost:8081/login",
        "bodyFile": "verify\\evidence\\ah_1791455286880722700_ah_admin_GET__admin.html"
      }
    ]
  },
  {
    "case": "P1.8",
    "pass": true,
    "http": [
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455286903727800_ah_anon_GET__.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455287049440500_ah_anon_GET__products.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455287131477600_ah_anon_GET__login.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455287171476800_ah_anon_GET__register.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455287214480300_ah_anon_GET__css_design_system_css.html"
      },
      {
        "status": 200,
        "location": null,
        "bodyFile": "verify\\evidence\\ah_1791455287244987600_ah_anon_GET__js_quick_add_js.html"
      }
    ]
  }
]
```

### hold-restart-persistence — PASS

```json
[
  {
    "id": 12,
    "code": "HC-CB0F66A4",
    "created_at": "2026-10-08T17:10:25.9901424",
    "expires_at": "2026-10-09T00:00:00"
  },
  {
    "id": 13,
    "code": "HC-61E882FD",
    "created_at": "2026-10-08T17:10:26.5248211",
    "expires_at": "2026-10-09T00:00:00"
  }
]
```

### hold-cleanup-job — PASS

```json
{
  "beforeRow": 11,
  "afterRows": [],
  "appConfig": "HOURS_24 / PT2S"
}
```

### hold-hours24-live — PASS

```json
[
  {
    "id": 14,
    "created_at": "2026-10-08T17:12:41.2890465",
    "expires_at": "2026-10-09T17:12:41.2890465",
    "elapsed_seconds": 86400
  }
]
```

### hold-update-existing — PASS

```json
[
  {
    "held_id": 14,
    "variant_id": 35,
    "quantity": 2
  }
]
```

### hold-invalid-voucher-removed — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454363114393400_ah_staff_GET__admin_pos.html"
}
```

### account-unicode — PASS

```json
{
  "before": [
    {
      "username": "testadmin_verify",
      "user_name": "QA Account Updated",
      "user_email": "accountupdated@verify.test",
      "user_phone": "0908111011",
      "employee_name": "QA Account Updated",
      "employee_email": "accountupdated@verify.test",
      "employee_phone": "0908111011",
      "created_at": "2026-10-08T16:59:06.7184934"
    }
  ],
  "after": [
    {
      "username": "testadmin_verify",
      "user_name": "Nhân viên kiểm thử",
      "user_email": "accountupdated@verify.test",
      "user_phone": "0908111011",
      "employee_name": "Nhân viên kiểm thử",
      "employee_email": "accountupdated@verify.test",
      "employee_phone": "0908111011",
      "created_at": "2026-10-08T16:59:06.7184934"
    }
  ],
  "http": {
    "status": 302,
    "location": "http://localhost:8081/admin/account",
    "bodyFile": "verify\\evidence\\ah_1791454363518638100_ah_staff_POST__admin_account_profile.html"
  }
}
```

### account-email-format — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454364127534200_ah_staff_POST__admin_account_profile.html"
}
```

### account-phone-format — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454364228037500_ah_staff_POST__admin_account_profile.html"
}
```

### password-utf8-limit — PASS

```json
{
  "status": 200,
  "location": null,
  "bodyFile": "verify\\evidence\\ah_1791454364342074100_ah_staff_POST__admin_account_password.html"
}
```

### password-login-raw — PASS

```json
{
  "old": {
    "status": 302,
    "location": "http://localhost:8081/login?error",
    "bodyFile": "verify\\evidence\\ah_1791454364506033800_ah_oldpw_POST__login.html"
  },
  "new": {
    "status": 302,
    "location": "http://localhost:8081/admin",
    "bodyFile": "verify\\evidence\\ah_1791454364700234400_ah_newpw_POST__login.html"
  }
}
```

### hold-customer-payment-note — PASS

```json
{
  "sql": [
    {
      "id": 16,
      "customer_id": 7,
      "customer_label": "QA Held Customer",
      "payment_method": "TRANSFER",
      "note": "Saved customer note",
      "voucher_code": "AHVERIFY"
    }
  ],
  "http": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791454366247579000_ah_staff_GET__admin_pos.html"
  }
}
```

### hold-parallel-limit — PASS

```json
{
  "responses": [
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091011641400_ah_limit_0_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091012647100_ah_limit_1_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091012647100_ah_limit_2_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091012647100_ah_limit_3_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091013621900_ah_limit_4_POST__admin_pos_holds.html"
    },
    {
      "status": 302,
      "location": "http://localhost:8081/admin/pos",
      "bodyFile": "verify\\evidence\\ah_1791455091013621900_ah_limit_5_POST__admin_pos_holds.html"
    }
  ],
  "rejections": [
    {
      "status": 200,
      "location": null,
      "bodyFile": "verify\\evidence\\ah_1791455091124092900_ah_limit_0_GET__admin_pos.html"
    }
  ],
  "count": 5,
  "before": {
    "orders": 6,
    "details": 8,
    "invoices": 6,
    "logs": 8,
    "voucher_used": 4,
    "stock": [
      {
        "id": 35,
        "quantity": 17,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 6,
    "details": 8,
    "invoices": 6,
    "logs": 8,
    "voucher_used": 4,
    "stock": [
      {
        "id": 35,
        "quantity": 17,
        "price": 210000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  }
}
```

### hold-hours24-immediate-expiry — PASS

```json
{
  "expiry": [
    {
      "id": 24,
      "created_at": "2026-10-08T17:24:51.0891045",
      "expires_at": "2026-10-09T17:24:51.0891045",
      "elapsed_seconds": 86400
    }
  ],
  "payment": {
    "status": 404,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455092700174600_ah_limit_1_POST__admin_pos_checkout.html"
  },
  "open": {
    "status": 404,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455092799237600_ah_limit_1_POST__admin_pos_holds_24_open.html"
  },
  "stillBeforeJob": [
    {
      "id": 24,
      "expires_at": "2026-10-08T17:23:52.6931767"
    }
  ],
  "page": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455092880793900_ah_limit_1_GET__admin_pos.html"
  }
}
```

### hold-expired-not-counted-live — PASS

```json
{
  "response": {
    "status": 302,
    "location": "http://localhost:8081/admin/pos",
    "bodyFile": "verify\\evidence\\ah_1791455093289202900_ah_limit_0_POST__admin_pos_holds.html"
  },
  "sql": [
    {
      "id": 24,
      "expires_at": "2026-10-08T17:23:52.6931767",
      "active": 0
    },
    {
      "id": 25,
      "expires_at": "2026-10-09T17:24:51.0930945",
      "active": 1
    },
    {
      "id": 26,
      "expires_at": "2026-10-09T17:24:51.1030944",
      "active": 1
    },
    {
      "id": 27,
      "expires_at": "2026-10-09T17:24:51.1080937",
      "active": 1
    },
    {
      "id": 28,
      "expires_at": "2026-10-09T17:24:51.1110934",
      "active": 1
    },
    {
      "id": 29,
      "expires_at": "2026-10-09T17:24:53.3999355",
      "active": 1
    }
  ]
}
```

### hold-hours24-cleanup-job — PASS

```json
{
  "expiredId": 24,
  "sqlAfter": []
}
```

### final-account-access — PASS

```json
[
  {
    "role": "admin",
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455283356834700_ah_admin_GET__admin_account.html",
    "expected": 200
  },
  {
    "role": "testadmin_verify",
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455283441347500_ah_staff_GET__admin_account.html",
    "expected": 200
  },
  {
    "role": "user",
    "status": 403,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455283506358800_ah_user_GET__admin_account.html",
    "expected": 403
  },
  {
    "role": "anon",
    "status": 302,
    "location": "http://localhost:8081/login",
    "bodyFile": "verify\\evidence\\ah_1791455283554364100_ah_anon_GET__admin_account.html",
    "expected": 302
  }
]
```

### password-change-sql — PASS

```json
{
  "sqlBefore": [
    {
      "username": "testadmin_verify",
      "prefix": "$2a$",
      "hash_length": 60,
      "fingerprint": "8B443EDF5547E4A9557DEC0E39E84466F124753C145B3562ED97FCEADE2C932D"
    }
  ],
  "sqlAfter": [
    {
      "username": "testadmin_verify",
      "prefix": "$2a$",
      "hash_length": 60,
      "fingerprint": "7360328D44805C9DAF4E515DFE5E140DF58739959B0727BAFF122F13E2F14353"
    }
  ],
  "change": {
    "status": 302,
    "location": "http://localhost:8081/admin/account",
    "bodyFile": "verify\\evidence\\ah_1791455284882578300_ah_staff_POST__admin_account_password.html"
  },
  "stillLoggedIn": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455285207577900_ah_staff_GET__admin_account.html"
  },
  "oldLogin": {
    "status": 302,
    "location": "http://localhost:8081/login?error",
    "bodyFile": "verify\\evidence\\ah_1791455285357344300_ah_staff_POST__login.html"
  },
  "newLogin": {
    "status": 302,
    "location": "http://localhost:8081/admin",
    "bodyFile": "verify\\evidence\\ah_1791455285538381200_ah_staff_POST__login.html"
  }
}
```

### new-unit-tests — PASS

```json
[
  {
    "name": "com.example.demodatn.banhang.PosHoldServiceTest",
    "tests": "9",
    "failures": "0",
    "errors": "0",
    "skipped": "0"
  },
  {
    "name": "com.example.demodatn.nhanvien.AdminAccountServiceTest",
    "tests": "6",
    "failures": "0",
    "errors": "0",
    "skipped": "0"
  },
  {
    "name": "com.example.demodatn.security.StandaloneAccountTest",
    "tests": "1",
    "failures": "0",
    "errors": "0",
    "skipped": "0"
  }
]
```

### no-empty-template-expressions — PASS

```json
[]
```

### account-placeholder-removed — PASS

```json
[
  {
    "file": "src\\main\\resources\\templates\\admin\\placeholder.html",
    "line": 3,
    "text": "<span class=\"eyebrow\">MODULE ĐANG PHÁT TRIỂN</span><h1 th:text=\"${adminTitle}\"></h1>"
  },
  {
    "file": "src\\main\\resources\\templates\\admin\\placeholder.html",
    "line": 4,
    "text": "<p th:text=\"'Sẽ hoàn thiện ở phase ' + ${phase}\"></p>"
  },
  {
    "file": "src\\main\\resources\\templates\\admin\\placeholder.html",
    "line": 5,
    "text": "<p class=\"text-muted\">Chức năng này chưa được triển khai.</p>"
  },
  {
    "file": "src\\test\\java\\com\\example\\demodatn\\banhang\\PosInvoiceIntegrationTest.java",
    "line": 108,
    "text": "                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(\"Sẽ hoàn thiện\"))));"
  }
]
```

### original-properties-unchanged — PASS

```json
{
  "sha256": "10CDAC630222A7CC7E729FD0AAB18E58A5EDA8D2950C953FFE5D7620AC2414E1",
  "note": "File already had uncommitted changes before this task; this task did not modify it."
}
```

### hold-price-change-payment-blocked — PASS

```json
{
  "before": {
    "orders": 6,
    "details": 8,
    "invoices": 6,
    "logs": 8,
    "voucher_used": 4,
    "stock": [
      {
        "id": 35,
        "quantity": 17,
        "price": 218000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  },
  "after": {
    "orders": 6,
    "details": 8,
    "invoices": 6,
    "logs": 8,
    "voucher_used": 4,
    "stock": [
      {
        "id": 35,
        "quantity": 17,
        "price": 218000
      },
      {
        "id": 36,
        "quantity": 19,
        "price": 200000
      }
    ]
  },
  "http": {
    "status": 302,
    "location": "http://localhost:8081/admin/pos",
    "bodyFile": "verify\\evidence\\ah_1791455567077422600_ah_priceguard_POST__admin_pos_checkout.html"
  },
  "error": {
    "status": 200,
    "location": null,
    "bodyFile": "verify\\evidence\\ah_1791455567147911100_ah_priceguard_GET__admin_pos.html"
  }
}
```

### hold-under30-warning — PASS

```json
{
  "warning": [
    "còn 0 giờ 20 phút"
  ]
}
```

## Build / vòng lặp sửa

`.\verify\account-hold-run.ps1 test` thiết lập datasource=shop_verify và profile=verify rồi gọi `.\mvnw.cmd clean test`. [Log đầy đủ](../verify/evidence/account-hold-test.log).

```text
2026-10-08T17:22:19.264+07:00  INFO 61236 --- [           main] o.s.t.web.servlet.TestDispatcherServlet  : Completed initialization in 0 ms
2026-10-08T17:22:19.333+07:00  INFO 61236 --- [           main] o.s.b.t.m.w.SpringBootMockServletContext : Initializing Spring TestDispatcherServlet ''
2026-10-08T17:22:19.333+07:00  INFO 61236 --- [           main] o.s.t.web.servlet.TestDispatcherServlet  : Initializing Servlet ''
2026-10-08T17:22:19.334+07:00  INFO 61236 --- [           main] o.s.t.web.servlet.TestDispatcherServlet  : Completed initialization in 1 ms
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.098 s -- in com.example.demodatn.thongke.StatisticsIntegrationTest
[INFO] 
[INFO] Results:
[INFO] 
[ERROR] Failures: 
[ERROR]   PeopleIntegrationTest.employeeInsertFailureRollsBackTheAlreadyInsertedUser:117 Expected org.springframework.dao.DataAccessException to be thrown, but nothing was thrown.
[ERROR] Errors: 
[ERROR]   UserUiIntegrationTest.historyAndOwnedDetailUseSharedLayoutAndResponsiveTables:23 � EmptyResultDataAccess Incorrect result size: expected 1, actual 0
[ERROR]   UserUiIntegrationTest.orderDetailAndSuccessRejectOtherCustomersAndMissingIds:30 � EmptyResultDataAccess Incorrect result size: expected 1, actual 0
[INFO] 
[ERROR] Tests run: 75, Failures: 1, Errors: 2, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  22.244 s
[INFO] Finished at: 2026-10-08T17:22:19+07:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:3.5.6:test (default-test) on project DemoDATN: There are test failures.
[ERROR] 
[ERROR] See C:\DuAnTNDEMO\DemoDATN\target\surefire-reports for the individual test results.
[ERROR] See dump files (if any exist) [date].dump, [date]-jvmRun[N].dump and [date].dumpstream.
[ERROR] -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
```

1. 13 test mới PASS; full clean test 72/1 failure/5 errors cũ. HTTP phát hiện standalone Admin bị quy tắc Employee chặn: sửa fallback và thêm regression test.
2. Bổ sung Unicode: restart trên varchar cũ gặp driver conversion from varchar to NCHAR unsupported. Thêm ALTER COLUMN vào migration, chạy chỉ trên shop_verify; login/profile/cleanup chạy lại PASS.
3. th:errors bỏ element khi không có lỗi làm client mất error slot: giữ p và để th:errors trên span con; browser validation chạy lại PASS.
4. Bổ sung guard 72 byte UTF-8 cho BCrypt, loading của nút ngoài form, tự preview voucher sau load và countdown bỏ chip hết hạn. Test/HTTP chạy lại PASS.
5. Probe giữ request làm navigation chờ và timeout: không nhận PASS; thay bằng đo tại submit, loading PASS. Ảnh mobile đầu chụp giữa transition sidebar: chụp lại với animations disabled và xem lại ảnh ổn định.
6. Probe limit tìm chuỗi trong toàn HTML nhầm text modal ẩn: đổi oracle đọc alert-danger; chạy lại 6 session: đúng 5 hold và 1 từ chối.
7. Clean test cuối 75/1 failure/2 errors. 16 test mới: 9 hold + 6 account + 1 standalone security đều PASS. Các Discount test thiếu fixture cũ chạy được sau khi nghiệm thu tạo Customer thật trong verify, không sửa code/test Discount. Còn PeopleIntegrationTest.employeeInsertFailureRollsBackTheAlreadyInsertedUser (giả định DB giới hạn khác thực tế) và 2 UserUiIntegrationTest thiếu fixture Order ONLINE/Order của khách khác. Không sửa ngoài phạm vi.

## Placeholder còn lại — chỉ liệt kê

| File:dòng | Nội dung | Tình trạng |
|---|---|---|
| src/main/resources/templates/admin/placeholder.html:3 | MODULE ĐANG PHÁT TRIỂN | Template cũ mồ côi, không còn controller return |
| src/main/resources/templates/admin/placeholder.html:4 | Sẽ hoàn thiện ở phase + biến phase | Không còn /admin/account đưa phase=6 |
| src/main/resources/templates/admin/placeholder.html:5 | Chức năng này chưa được triển khai | Như trên, giữ nguyên ngoài phạm vi |
| src/test/java/com/example/demodatn/banhang/PosInvoiceIntegrationTest.java:108 | Assertion phủ định chuỗi Sẽ hoàn thiện | Không phải placeholder module |

Quét src java/html/js/css không có thêm TODO/FIXME/coming soon/not implemented của module hoạt động. Báo cáo cũ có nhắc placeholder lịch sử và được giữ nguyên, không sửa chúng thành khẳng định hiện trạng.

## NOT TESTED / giới hạn

| Mục | Lý do |
|---|---|
| Migration/test trên shopbangiay | Người dùng chỉ cho test shop_verify |
| Package/JAR/E2E toàn hệ thống | Ngoài bổ sung này; clean test còn FAIL vì test cũ |
| Clone sạch chỉ từ commit feature | Workspace chứa nhiều thay đổi phase trước chưa commit; không khẳng định feature commit thay toàn bộ lịch sử đó |
| Các module/site khác | Không chạy lại kiểm toán toàn site và không sửa ngoài phạm vi |

## Git / dữ liệu giữ lại

Commit source: **171014b976254baa8fac201f7e4d43487050757e** — `Implement admin account and persistent POS held carts`.

| Hạng mục | Cách kiểm tra | Kết quả thực tế | PASS/FAIL/NOT TESTED |
|---|---|---|---|
| Commit code trong phạm vi | git log; git diff --cached --check trước commit | 171014b, 27 tệp; diff check không lỗi | PASS |

Các tệp trong commit source:

```text
DESIGN.md
UX-CONTRACT.md
database/admin-account-pos-hold.sql
src/main/java/com/example/demodatn/banhang/AdminPosController.java
src/main/java/com/example/demodatn/banhang/PosHeldItem.java
src/main/java/com/example/demodatn/banhang/PosHeldOrder.java
src/main/java/com/example/demodatn/banhang/PosHeldOrderRepository.java
src/main/java/com/example/demodatn/banhang/PosHoldCleanup.java
src/main/java/com/example/demodatn/banhang/PosHoldService.java
src/main/java/com/example/demodatn/banhang/dto/PosCheckoutRequest.java
src/main/java/com/example/demodatn/config/PosHoldConfig.java
src/main/java/com/example/demodatn/nhanvien/AccountPasswordForm.java
src/main/java/com/example/demodatn/nhanvien/AccountProfileForm.java
src/main/java/com/example/demodatn/nhanvien/AdminAccountService.java
src/main/java/com/example/demodatn/nhanvien/Employee.java
src/main/java/com/example/demodatn/nhanvien/User.java
src/main/java/com/example/demodatn/nhanvien/UserRepository.java
src/main/java/com/example/demodatn/security/UserDetailsServiceImpl.java
src/main/java/com/example/demodatn/thongke/AdminPageController.java
src/main/resources/static/js/admin-catalog.js
src/main/resources/static/js/admin-pos.js
src/main/resources/templates/admin/account.html
src/main/resources/templates/admin/pos.html
src/main/resources/templates/fragments/admin-topbar.html
src/test/java/com/example/demodatn/banhang/PosHoldServiceTest.java
src/test/java/com/example/demodatn/nhanvien/AdminAccountServiceTest.java
src/test/java/com/example/demodatn/security/StandaloneAccountTest.java
```

Báo cáo bổ sung: `docs/ADMIN_ACCOUNT_POS_HOLD_REPORT.md`. Các script QA tạo cục bộ (không commit dữ liệu kiểm thử):

```text
verify/account-hold-run.ps1
verify/account-hold-check.py
verify/account-hold-followup.py
verify/account-hold-migrate.py
verify/account-hold-debug.py
verify/account-hold-prepare-retest.py
verify/account-hold-browser.js
verify/account-hold-browser-final.js
verify/account-hold-concurrency-expiry.py
verify/account-hold-final-check.py
verify/account-hold-price-guard.py
verify/account-hold-warning-restore.py
```

Chỉ stage source/template/JS/migration/tests và tài liệu trong phạm vi. Không stage application.properties/application-verify.properties, cookies, ảnh hay HTML có CSRF/session. Nhiều tệp phase trước đã dirty/untracked từ trước; các thay đổi ngoài phạm vi được giữ. Các tệp chạm trong commit được ghi theo trạng thái workspace hiện tại, vì một số tệp trước đó chưa được Git theo dõi; không nhận commit là snapshot toàn dự án.

application.properties không được nhiệm vụ này sửa: SHA256 trước/sau `10CDAC630222A7CC7E729FD0AAB18E58A5EDA8D2950C953FFE5D7620AC2414E1`. git status vẫn M do thay đổi trước nhiệm vụ, không nói file sạch so với HEAD.

**Giữ shop_verify** với fixture và hold hợp lệ; app nền cổng 8081, END_OF_DAY/PT5M. Không đổi mật khẩu admin thật. Password tests chỉ dùng testadmin_verify trên shop_verify. Browser test đã đóng.

Manifest 1262 tệp bằng chứng cục bộ: [account-hold-evidence-manifest.txt](../verify/evidence/account-hold-evidence-manifest.txt). Có cookies/HTML/token trong thư mục verify, không đưa vào commit.

**DỪNG sau báo cáo, không thực hiện module khác.**

