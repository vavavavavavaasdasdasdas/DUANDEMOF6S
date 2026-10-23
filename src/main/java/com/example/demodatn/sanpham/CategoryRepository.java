package com.example.demodatn.sanpham;
import com.example.demodatn.entity.sanpham.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
}
