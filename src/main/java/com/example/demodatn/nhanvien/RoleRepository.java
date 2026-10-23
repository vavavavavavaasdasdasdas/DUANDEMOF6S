package com.example.demodatn.nhanvien;
import com.example.demodatn.entity.nhanvien.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
