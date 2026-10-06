/*
 * Them nhanh vao gio (dung chung cho moi trang co product-card).
 * Quy tac: badge LUON hien thi dung so totalQuantity server tra ve (JSON),
 * KHONG cong don phia client (tranh "2" + 1 = "21").
 * Server tu choi (het hang / vuot ton kho) -> khong doi badge, hien thong bao loi.
 */
(function () {
    function csrf() {
        var t = document.querySelector('meta[name="_csrf"]');
        return t ? t.getAttribute('content') : null;
    }

    function showMsg(form, text, ok) {
        var box = form.querySelector('.quick-add-msg');
        if (!box) { alert(text); return; }
        box.textContent = text;
        box.classList.remove('d-none', 'text-danger', 'text-success');
        box.classList.add(ok ? 'text-success' : 'text-danger');
    }

    function setBadge(total) {
        var n = Number(total);
        if (!Number.isFinite(n)) return;
        document.querySelectorAll('.cart-badge').forEach(function (b) { b.textContent = String(n); });
    }

    document.addEventListener('submit', function (e) {
        var form = e.target;
        if (!form.classList || !form.classList.contains('quick-add-form')) return;
        e.preventDefault();

        var checked = form.querySelector('input[name="variantId"]:checked');
        if (!checked) { showMsg(form, 'Vui lòng chọn size.', false); return; }

        var body = new URLSearchParams();
        body.append('variantId', checked.value);
        body.append('quantity', '1');
        var token = csrf();
        if (token) body.append('_csrf', token);

        fetch('/cart/add-ajax', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'Accept': 'application/json' },
            body: body.toString()
        })
            .then(function (r) { return r.json().then(function (d) { return { ok: r.ok, data: d }; }); })
            .then(function (res) {
                if (res.ok && typeof res.data.totalQuantity === 'number') {
                    setBadge(res.data.totalQuantity);
                    showMsg(form, res.data.message || 'Đã thêm vào giỏ', true);
                } else {
                    showMsg(form, (res.data && res.data.error) || 'Không thể thêm vào giỏ', false);
                }
            })
            .catch(function () { showMsg(form, 'Lỗi kết nối, vui lòng thử lại.', false); });
    });
})();
