(() => {
    window.dich = key => window.nhanHienThi[key] ?? key;
    window.nhanTrangThai = code => window.nhanHienThi['enum.' + code] ?? code;
})();
