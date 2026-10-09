package com.example.demodatn.config;

import com.example.demodatn.nhanvien.*;
import com.example.demodatn.sanpham.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DataInitializerTest {
    @Test
    void twoRunsCreateOnlyFoundationOnce() throws Exception {
        RoleRepository roles = mock(RoleRepository.class);
        UserRepository users = mock(UserRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        CategoryRepository categories = mock(CategoryRepository.class);
        SizeRepository sizes = mock(SizeRepository.class);
        PasswordEncoder passwords = mock(PasswordEncoder.class);
        Map<String, Role> roleRows = new HashMap<>();
        Map<String, User> userRows = new HashMap<>();
        List<Category> categoryRows = new ArrayList<>();
        List<Size> sizeRows = new ArrayList<>();
        when(roles.findByName(anyString())).thenAnswer(i -> roleRows.get(i.getArgument(0)));
        when(roles.save(any())).thenAnswer(i -> { Role r=i.getArgument(0); roleRows.put(r.getName(),r); return r; });
        when(users.findByUsername(anyString())).thenAnswer(i -> Optional.ofNullable(userRows.get(i.getArgument(0))));
        when(users.save(any())).thenAnswer(i -> { User u=i.getArgument(0); userRows.put(u.getUsername(),u); return u; });
        when(categories.findAll()).thenAnswer(i -> new ArrayList<>(categoryRows));
        when(categories.save(any())).thenAnswer(i -> { Category c=i.getArgument(0); categoryRows.add(c); return c; });
        when(sizes.findAll()).thenAnswer(i -> new ArrayList<>(sizeRows));
        when(sizes.save(any())).thenAnswer(i -> { Size s=i.getArgument(0); sizeRows.add(s); return s; });
        when(passwords.encode("admin123")).thenReturn("hashed");
        DataInitializer initializer = new DataInitializer(roles, users, employees, categories, sizes, passwords);
        initializer.run();
        initializer.run();
        assertEquals(Set.of("ROLE_ADMIN", "ROLE_USER"), roleRows.keySet());
        assertEquals(Set.of("admin"), userRows.keySet());
        assertEquals(3, categoryRows.size());
        assertEquals(3, sizeRows.size());
        verify(employees, times(1)).save(any());
        verify(passwords, times(1)).encode("admin123");
    }
}
