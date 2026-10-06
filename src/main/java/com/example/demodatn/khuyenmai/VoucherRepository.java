package com.example.demodatn.khuyenmai;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.khuyenmai.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {}
