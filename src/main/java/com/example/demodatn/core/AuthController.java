package com.example.demodatn.core;

import com.example.demodatn.nhanvien.RegisterRequest;
import com.example.demodatn.nhanvien.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/dang-ky";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest request, 
                                  BindingResult bindingResult, 
                                  Model model, 
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/dang-ky";
        }
        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("message", com.example.demodatn.core.NhanHienThi.text("ui.040"));
            return "redirect:/login";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("error", com.example.demodatn.core.NhanHienThi.text("ui.041"));
            return "auth/dang-ky";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/dang-ky";
        }
    }
}
