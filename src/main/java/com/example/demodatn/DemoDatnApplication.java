package com.example.demodatn;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.nhanvien.User;
import com.example.demodatn.nhanvien.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class DemoDatnApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDatnApplication.class, args);
    }

    @Bean
    public CommandLineRunner initPasswords(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<User> adminOpt = userRepository.findByUsername("admin");
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                admin.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(admin);
            }

            Optional<User> userOpt = userRepository.findByUsername("user");
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(passwordEncoder.encode("user123"));
                userRepository.save(user);
            }
        };
    }
}
