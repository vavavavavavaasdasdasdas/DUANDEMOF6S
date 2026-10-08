package com.example.demodatn.khachhang;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CustomerRepository customerRepository;
    private final com.example.demodatn.nhanvien.UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewProfile(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";
        String username = auth.getName();
        Customer customer = customerRepository.findByUser_Username(username).orElse(null);
        model.addAttribute("customer", customer);
        return "user/ho-so";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam String email,
                                @RequestParam String address,
                                Authentication auth,
                                RedirectAttributes ra) {
        String username = auth.getName();
        Customer customer = customerRepository.findByUser_Username(username).orElse(null);
        if (customer != null) {
            phone=com.example.demodatn.core.PeopleService.phone(phone);
            email=com.example.demodatn.core.PeopleService.email(email);
            if(customerRepository.existsByEmailIgnoreCaseAndIdNot(email,customer.getId())
                    || customerRepository.existsByPhoneAndIdNot(phone,customer.getId())) {
                ra.addFlashAttribute("error","Email hoặc số điện thoại đã tồn tại.");
                return "redirect:/profile";
            }
            customer.setFullName(fullName);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(address);
            try {
                customerRepository.saveAndFlush(customer);
                ra.addFlashAttribute("message", "Cập nhật thông tin thành công!");
            } catch(org.springframework.dao.DataIntegrityViolationException ex) {
                ra.addFlashAttribute("error","Email hoặc số điện thoại vừa được sử dụng. Vui lòng thử lại.");
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        String username = auth.getName();
        var user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                ra.addFlashAttribute("error", "Mật khẩu cũ không chính xác!");
                return "redirect:/profile";
            }
            if (!newPassword.equals(confirmPassword)) {
                ra.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "redirect:/profile";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            ra.addFlashAttribute("message", "Đổi mật khẩu thành công!");
        }
        return "redirect:/profile";
    }
}
