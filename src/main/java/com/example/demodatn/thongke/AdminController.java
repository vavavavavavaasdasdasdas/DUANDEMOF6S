package com.example.demodatn.thongke;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    @GetMapping(value = {"", "/", "/dashboard"})
    public String dashboard(@RequestParam(defaultValue = "7") int days, Model model) {
        model.addAttribute("dashboard", dashboardService.load(days == 30 ? 30 : 7));
        return "admin/bang-dieu-khien";
    }

    @GetMapping("/products")
    public String products(Model model,jakarta.servlet.http.HttpServletRequest request) {
        return "redirect:/admin/products-crud"+(request.getQueryString()==null?"":"?"+request.getQueryString());
    }

}
