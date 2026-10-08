package com.example.demodatn.banhang;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@WithMockUser(username="user",roles="USER")
class UserUiIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    @Test
    void historyAndOwnedDetailUseSharedLayoutAndResponsiveTables() throws Exception {
        var mvc=MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        Integer id=jdbc.queryForObject("SELECT TOP(1) o.id FROM orders o JOIN customers c ON c.id=o.customer_id JOIN users u ON u.id=c.user_id WHERE u.username='user' AND o.order_type='ONLINE' ORDER BY o.id",Integer.class);
        mvc.perform(get("/orders")).andExpect(status().isOk()).andExpect(content().string(containsString("/css/he-thong-thiet-ke.css"))).andExpect(content().string(containsString("table-responsive")));
        mvc.perform(get("/orders/"+id)).andExpect(status().isOk()).andExpect(content().string(containsString("table-responsive"))).andExpect(content().string(not(containsString("customerName"))));
    }
    @Test
    void orderDetailAndSuccessRejectOtherCustomersAndMissingIds() throws Exception {
        var mvc=MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        Integer id=jdbc.queryForObject("SELECT TOP(1) o.id FROM orders o JOIN customers c ON c.id=o.customer_id JOIN users u ON u.id=c.user_id WHERE u.username<>'user' ORDER BY o.id",Integer.class);
        mvc.perform(get("/orders/"+id)).andExpect(status().isForbidden());
        mvc.perform(get("/orders/success/"+id)).andExpect(status().isForbidden());
        mvc.perform(get("/orders/-1")).andExpect(status().isNotFound());
        mvc.perform(get("/orders/success/-1")).andExpect(status().isNotFound());
    }
    @Test @org.springframework.transaction.annotation.Transactional
    void catalogDoesNotRenderLinksToProductsWithoutActiveStatus() throws Exception {
        jdbc.update("UPDATE products SET status=NULL WHERE id=4");
        MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build().perform(get("/"))
                .andExpect(status().isOk()).andExpect(content().string(not(containsString("href=\"/products/4\""))));
    }
    @Test
    void cartLoadsItsExistingActionsInsideLayoutContent() throws Exception {
        MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build().perform(get("/cart"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("/js/gio-hang.js"))).andExpect(content().string(containsString("store-confirm")));
    }
}
