package com.example.demodatn.banhang;
import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import com.example.demodatn.banhang.Order;
import com.example.demodatn.banhang.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("order", orderRepository.findById(id).orElse(null));
        return "admin/orders/detail";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id, @RequestParam String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return "redirect:/admin/orders/detail/" + id;
    }
}
