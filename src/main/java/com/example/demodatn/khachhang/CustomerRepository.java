package com.example.demodatn.khachhang;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.khachhang.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    java.util.Optional<Customer> findByEmail(String email);
    java.util.Optional<Customer> findByPhone(String phone);
    java.util.Optional<Customer> findByUser_Username(String username);
}
