package com.example.demodatn.entity.nhanvien;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private Boolean status = true;

    @Column(name = "full_name")
    @org.hibernate.annotations.Nationalized
    private String fullName;
    @org.hibernate.annotations.Nationalized private String email;
    @org.hibernate.annotations.Nationalized private String phone;
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}
