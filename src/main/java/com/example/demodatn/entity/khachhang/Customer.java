package com.example.demodatn.entity.khachhang;
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
    @org.hibernate.annotations.Nationalized private String fullName;

    @Column(unique = true)
    @org.hibernate.annotations.Nationalized private String phone;

    @org.hibernate.annotations.Nationalized private String email;

    @org.hibernate.annotations.Nationalized private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean status = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.example.demodatn.entity.nhanvien.User user;
}

