package com.example.demodatn.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NhanHienThiTest {
    @Test
    void readsUtf8LabelsWithoutChangingPersistedCodes() {
        assertEquals("Chờ xử lý", NhanHienThi.label("PENDING"));
        assertEquals("Quản trị viên", NhanHienThi.label("ROLE_ADMIN"));
        assertEquals("Đã thanh toán", NhanHienThi.label("PAID"));
        assertEquals("Trực tuyến", NhanHienThi.label("ONLINE"));
        assertEquals("POS", NhanHienThi.label("POS"));
        assertEquals("UNKNOWN_CODE", NhanHienThi.label("UNKNOWN_CODE"));
    }
}
