package com.example.demodatn.thongke;

import com.example.demodatn.banhang.dto.CartItemDto;
import com.example.demodatn.banhang.dto.PosCheckoutRequest;
import com.example.demodatn.banhang.OrderService;
import com.example.demodatn.banhang.InsufficientStockException;
import com.example.demodatn.khachhang.Customer;
import com.example.demodatn.khachhang.CustomerRepository;
import com.example.demodatn.nhanvien.Employee;
import com.example.demodatn.nhanvien.EmployeeRepository;
import com.example.demodatn.sanpham.ProductVariant;
import com.example.demodatn.sanpham.ProductService;
import com.example.demodatn.sanpham.ProductVariantRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    private final ProductVariantRepository productVariantRepository;

    @GetMapping(value = {"", "/", "/dashboard"})
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/pos")
    public String pos(HttpSession session, Model model) {
        List<CartItemDto> posCart = (List<CartItemDto>) session.getAttribute("posCart");
        if (posCart == null) posCart = new ArrayList<>();
        model.addAttribute("posCart", posCart);
        model.addAttribute("variants", productVariantRepository.findAll());
        model.addAttribute("total", posCart.stream().map(CartItemDto::getTotalPrice).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        
        PosCheckoutRequest checkoutRequest = new PosCheckoutRequest();
        checkoutRequest.setPaymentMethod("CASH");
        model.addAttribute("posCheckoutRequest", checkoutRequest);
        return "admin/pos";
    }

    @PostMapping("/pos/add")
    public String posAddToCart(@RequestParam Integer variantId, @RequestParam Integer quantity, HttpSession session) {
        ProductVariant variant = productService.getVariantById(variantId);
        if (variant == null || variant.getQuantity() < quantity) return "redirect:/admin/pos?error=NotEnoughStock";
        
        List<CartItemDto> posCart = (List<CartItemDto>) session.getAttribute("posCart");
        if (posCart == null) posCart = new ArrayList<>();

        boolean found = false;
        for (CartItemDto item : posCart) {
            if (item.getVariantId().equals(variantId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            posCart.add(new CartItemDto(variant.getProduct().getId(), variant.getId(), variant.getProduct().getName(), variant.getSize().getSizeValue(), variant.getPrice(), quantity));
        }
        session.setAttribute("posCart", posCart);
        return "redirect:/admin/pos";
    }

    @PostMapping("/pos/checkout")
    public String posCheckout(HttpSession session, Principal principal, @Valid @ModelAttribute("posCheckoutRequest") PosCheckoutRequest request, BindingResult bindingResult) {
        List<CartItemDto> posCart = (List<CartItemDto>) session.getAttribute("posCart");
        if (posCart == null || posCart.isEmpty()) return "redirect:/admin/pos?error=EmptyCart";

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/pos?error=InvalidCheckoutRequest";
        }

        try {
            Employee employee = null;
            if (principal != null) {
                employee = employeeRepository.findAll().stream()
                    .filter(e -> e.getUser() != null && e.getUser().getUsername().equals(principal.getName()))
                    .findFirst().orElse(null);
            }

            Customer guest = customerRepository.findAll().stream().filter(c -> "KhÃƒÂ¡ch lÃ¡ÂºÂ»".equals(c.getFullName())).findFirst().orElseGet(() -> {
                Customer c = new Customer();
                c.setFullName("KhÃƒÂ¡ch lÃ¡ÂºÂ»");
                return customerRepository.save(c);
            });

            orderService.createOrder(posCart, guest, employee, "POS", null, request.getPaymentMethod(), "TÃ¡ÂºÂ¡i quÃ¡ÂºÂ§y");
            session.removeAttribute("posCart");
            return "redirect:/admin/pos?success=OrderCreated";
        } catch(InsufficientStockException e) {
            return "redirect:/admin/pos?error=" + e.getMessage();
        } catch(Exception e) {
            return "redirect:/admin/pos?error=" + e.getMessage();
        }
    }
}



