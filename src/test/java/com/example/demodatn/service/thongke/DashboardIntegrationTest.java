package com.example.demodatn.service.thongke;
import com.example.demodatn.controller.AdminPageAdvice;
import com.example.demodatn.repository.thongke.DashboardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional // All fixture writes roll back; no seed data persists.
class DashboardIntegrationTest {
    @Autowired DashboardRepository repository;
    @Autowired JdbcTemplate jdbc;
    @Autowired WebApplicationContext context;

    @Test
    void revenueIncludesStartExcludesEndUnpaidAndCancelled() {
        LocalDate start = LocalDate.of(2040, 1, 1), end = start.plusDays(1);
        BigDecimal before = (BigDecimal) repository.revenue(start, end);
        addOrder("2040-01-01T00:00:00", "PAID", "DELIVERED", 12345);
        addOrder("2040-01-01T23:59:59", "PAID", "CONFIRMED", 55);
        addOrder("2040-01-02T00:00:00", "PAID", "DELIVERED", 999);
        addOrder("2040-01-01T12:00:00", "UNPAID", "PENDING", 999);
        addOrder("2040-01-01T12:00:00", "PAID", "CANCELLED", 999);
        assertEquals(0, before.add(new BigDecimal("12400")).compareTo((BigDecimal) repository.revenue(start, end)));
    }

    private void addOrder(String date, String payment, String status, int amount) {
        jdbc.update("INSERT INTO orders(order_code,order_type,status,total_amount,final_amount,payment_status,created_at) VALUES (?,'ONLINE',?,?,?,?,CAST(? AS datetime))",
                "AUDIT-" + UUID.randomUUID(), status, amount, amount, payment, date);
    }

    @Test
    void lowStockIncludesThresholdAndZeroButNotInactiveVariants() {
        Integer id = jdbc.queryForObject("SELECT TOP (1) v.id FROM product_variants v JOIN products p ON p.id=v.product_id WHERE p.status=1 ORDER BY v.id", Integer.class);
        assertNotNull(id);
        jdbc.update("UPDATE product_variants SET quantity=5,status=1 WHERE id=?", id);
        assertTrue(repository.lowStock(5).stream().anyMatch(row -> id.equals(row.get("id"))));
        assertFalse(repository.lowStock(4).stream().anyMatch(row -> id.equals(row.get("id"))));
        jdbc.update("UPDATE product_variants SET quantity=0 WHERE id=?", id);
        assertTrue(repository.lowStock(0).stream().anyMatch(row -> id.equals(row.get("id"))));
        jdbc.update("UPDATE product_variants SET status=0 WHERE id=?", id);
        assertFalse(repository.lowStock(5).stream().anyMatch(row -> id.equals(row.get("id"))));
    }

    @Test
    @WithMockUser(roles = "USER")
    void ordinaryUserCannotAccessAnyAdminRouteOrNestedMutation() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        for (var item : AdminPageAdvice.NAV) mvc.perform(get(item.route())).andExpect(status().isForbidden());
        for (String path : new String[]{"/admin", "/admin/", "/admin/products", "/admin/orders/detail/1", "/admin/arbitrary/deep/path"}) {
            mvc.perform(get(path)).andExpect(status().isForbidden());
        }
        for (String path : new String[]{"/admin/pos/add", "/admin/pos/checkout", "/admin/orders/update-status/1", "/admin/products-crud/save"}) {
            mvc.perform(post(path).with(csrf())).andExpect(status().isForbidden());
        }
    }
}
