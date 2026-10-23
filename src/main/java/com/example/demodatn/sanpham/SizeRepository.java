package com.example.demodatn.sanpham;
import com.example.demodatn.entity.sanpham.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Integer> {
    boolean existsBySizeValueIgnoreCaseAndIdNot(String value, Integer id);
}
