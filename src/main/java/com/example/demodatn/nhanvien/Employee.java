package com.example.demodatn.nhanvien;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "employee_code", unique = true)
    private String employeeCode;

    @Column(name = "full_name", nullable = false)
    @org.hibernate.annotations.Nationalized
    private String fullName;

    @Column(unique = true)
    private String phone;

    private String email;

    private String address;

    private String position;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Boolean status = true;
}
