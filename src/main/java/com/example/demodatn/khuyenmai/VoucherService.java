package com.example.demodatn.khuyenmai;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.khuyenmai.Voucher;
import com.example.demodatn.khuyenmai.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;
    
    public Voucher getByCode(String code) {
        // filter manually if findByCode doesn't exist yet
        return voucherRepository.findAll().stream()
                .filter(v -> v.getCode().equalsIgnoreCase(code) && v.getStatus())
                .findFirst()
                .orElse(null);
    }
    
    public List<Voucher> getAllActive() {
        return voucherRepository.findAll().stream()
                .filter(Voucher::getStatus)
                .toList();
    }
}
