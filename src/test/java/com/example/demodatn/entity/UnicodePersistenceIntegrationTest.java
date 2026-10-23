package com.example.demodatn.entity;

import com.example.demodatn.entity.khachhang.Customer;
import com.example.demodatn.entity.sanpham.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class UnicodePersistenceIntegrationTest {
    @Autowired
    private EntityManager entityManager;

    @Test
    void vietnameseNamesDescriptionsAndAddressesSurviveDatabaseRoundTrip() {
        Customer customer = new Customer();
        customer.setFullName("Nguyễn Thị Ánh");
        customer.setAddress("Đường Nguyễn Huệ, phường Bến Nghé, Hồ Chí Minh");
        entityManager.persist(customer);

        Product product = new Product();
        product.setName("Giày dệt Đà Nẵng");
        product.setDescription("Bền, nhẹ, êm ái, chống trơn trượt");
        entityManager.persist(product);
        entityManager.flush();
        entityManager.clear();

        Customer storedCustomer = entityManager.find(Customer.class, customer.getId());
        Product storedProduct = entityManager.find(Product.class, product.getId());
        assertEquals("Nguyễn Thị Ánh", storedCustomer.getFullName());
        assertEquals("Đường Nguyễn Huệ, phường Bến Nghé, Hồ Chí Minh", storedCustomer.getAddress());
        assertEquals("Giày dệt Đà Nẵng", storedProduct.getName());
        assertEquals("Bền, nhẹ, êm ái, chống trơn trượt", storedProduct.getDescription());
    }
}
