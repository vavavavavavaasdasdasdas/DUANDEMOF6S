package com.example.demodatn.config;

import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.khuyenmai.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionDetailRepository promotionDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Roles
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            Role r = new Role();
            r.setName("ROLE_ADMIN");
            adminRole = roleRepository.save(r);
        }
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            Role r = new Role();
            r.setName("ROLE_USER");
            userRole = roleRepository.save(r);
        }

        // 2. Users
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            admin.setStatus(true);
            userRepository.save(admin);
            Employee employee=new Employee();
            employee.setEmployeeCode("NV-SYSTEM");employee.setFullName("Quản trị hệ thống");
            employee.setUser(admin);employee.setStatus(true);
            employeeRepository.save(employee);
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(userRole);
            user.setStatus(true);
            userRepository.save(user);
        }

        // 3. Categories
        Category catNike = createCategoryIfNotFound(1, "Nike");
        Category catAdidas = createCategoryIfNotFound(2, "Adidas");
        Category catConverse = createCategoryIfNotFound(3, "Converse");

        // 4. Sizes
        Size size39 = createSizeIfNotFound(4, "39");
        Size size40 = createSizeIfNotFound(5, "40");
        Size size41 = createSizeIfNotFound(6, "41");

        // 5. Products & Variants
        if (productRepository.count() == 0) {
            Product p1 = createProduct("SP001", "Nike Air Force 1", "GiÃƒÂ y thÃ¡Â»Æ’ thao Nike Air Force 1 trÃ¡ÂºÂ¯ng", catNike, "https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fb3c00f/air-force-1-07-shoes-WrLlWX.png");
            createVariant(p1, size39, 50, new BigDecimal("2500000.00"));
            createVariant(p1, size40, 27, new BigDecimal("2500000.00"));

            Product p2 = createProduct("SP002", "Adidas Ultraboost", "GiÃƒÂ y chÃ¡ÂºÂ¡y bÃ¡Â»â„¢ Adidas Ultraboost Ã„â€˜en", catAdidas, "https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/09c5ea6df1bd473e8b02acdf011614cb_9366/Giay_Ultraboost_21_Den_FY0306_01_standard.jpg");
            createVariant(p2, size40, 20, new BigDecimal("3500000.00"));
            createVariant(p2, size41, 15, new BigDecimal("3500000.00"));

            Product p3 = createProduct("SP003", "Converse Chuck Taylor", "GiÃƒÂ y Converse cÃ¡Â»â€¢ cao Ã„â€˜en", catConverse, "https://www.converse.vn/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/m/9/m9160c.jpg");
            createVariant(p3, size39, 100, new BigDecimal("1500000.00"));
            createVariant(p3, size40, 80, new BigDecimal("1500000.00"));

            Product p4 = createProduct("SP004", "Nike Air Max", "Desc", catNike, "/images/placeholder.svg");
            createVariant(p4, size39, 10, new BigDecimal("2000000.00"));

            Product p5 = createProduct("SP005", "Nike Dunk", "Desc", catNike, "/images/placeholder.svg");
            createVariant(p5, size39, 10, new BigDecimal("3000000.00"));

            // 6. Promotions
            Promotion promo = new Promotion();
            promo.setName("KhuyÃ¡ÂºÂ¿n mÃƒÂ£i mÃƒÂ¹a hÃƒÂ¨");
            promo.setDiscountType("PERCENT");
            promo.setDiscountValue(new BigDecimal("15.00"));
            promo.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
            promo.setEndDate(LocalDateTime.of(2030, 12, 31, 0, 0));
            promo.setStatus(true);
            promotionRepository.save(promo);

            PromotionDetail pd1 = new PromotionDetail();
            pd1.setPromotion(promo);
            pd1.setProduct(p1);
            promotionDetailRepository.save(pd1);

            PromotionDetail pd2 = new PromotionDetail();
            pd2.setPromotion(promo);
            pd2.setProduct(p2);
            promotionDetailRepository.save(pd2);
        }
    }

    private Category createCategoryIfNotFound(Integer id, String name) {
        return categoryRepository.findAll().stream().filter(c -> c.getName().equals(name)).findFirst().orElseGet(() -> {
            Category c = new Category();
            c.setName(name);
            c.setStatus(true);
            return categoryRepository.save(c);
        });
    }

    private Size createSizeIfNotFound(Integer id, String sizeValue) {
        return sizeRepository.findAll().stream().filter(s -> s.getSizeValue().equals(sizeValue)).findFirst().orElseGet(() -> {
            Size s = new Size();
            s.setSizeValue(sizeValue);
            return sizeRepository.save(s);
        });
    }

    private Product createProduct(String code, String name, String desc, Category cat, String imageUrl) {
        Product p = new Product();
        p.setCode(code);
        p.setName(name);
        p.setDescription(desc);
        p.setCategory(cat);
        p.setImageUrl(imageUrl);
        p.setStatus(true);
        return productRepository.save(p);
    }

    private ProductVariant createVariant(Product p, Size s, int qty, BigDecimal price) {
        ProductVariant v = new ProductVariant();
        v.setProduct(p);
        v.setSize(s);
        v.setQuantity(qty);
        v.setPrice(price);
        v.setStatus(true);
        return productVariantRepository.save(v);
    }
}