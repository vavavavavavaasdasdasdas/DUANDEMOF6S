package com.example.demodatn.khachhang;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_code", unique = true)
    private String customerCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true)
    private String phone;

    private String email;

    private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean status = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.example.demodatn.nhanvien.User user;
}

