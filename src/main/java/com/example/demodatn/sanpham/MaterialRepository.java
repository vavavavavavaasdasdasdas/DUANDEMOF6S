package com.example.demodatn.sanpham;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
    boolean existsByName(String name);
    List<Material> findByStatusTrueOrderByNameAscIdAsc();
}
