package com.example.demodatn.banhang;

import com.example.demodatn.banhang.dto.CartItemDto;
import com.example.demodatn.banhang.dto.CheckoutRequest;
import com.example.demodatn.khachhang.Customer;
import com.example.demodatn.khachhang.CustomerRepository;
import com.example.demodatn.khuyenmai.VoucherService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CustomerRepository customerRepository;
    private final VoucherService voucherService;
    private final OrderRepository orderRepository;

    @Value("${shop.shipping.fee:30000}")
    private BigDecimal shippingFee;

    @Value("${shop.shipping.free-threshold:500000}")
    private BigDecimal freeShippingThreshold;
    
    @Value("${shop.bank.name:Vietcombank}")
    private String bankName;
    @Value("${shop.bank.account:123456789}")
    private String bankAccount;
    @Value("${shop.bank.owner:SHOP GIAY}")
    private String bankOwner;

    public OrderController(OrderService orderService, CustomerRepository customerRepository, VoucherService voucherService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.customerRepository = customerRepository;
        this.voucherService = voucherService;
        this.orderRepository = orderRepository;
    }

    private Customer getCustomer(Principal principal) {
        if (principal == null) return null;
        return customerRepository.findByUser_Username(principal.getName()).orElse(null);
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session, Principal principal, RedirectAttributes redirectAttributes) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "GiÃ¡Â»Â hÃƒÂ ng rÃ¡Â»â€”ng!");
            return "redirect:/cart";
        }
        
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItemDto item : cart) {
            subTotal = subTotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        BigDecimal fee = subTotal.compareTo(freeShippingThreshold) >= 0 ? BigDecimal.ZERO : shippingFee;
        
        Customer customer = getCustomer(principal);
        
        if (!model.containsAttribute("checkoutRequest")) {
            CheckoutRequest req = new CheckoutRequest();
            if (customer != null) {
                req.setReceiverName(customer.getFullName());
                req.setPhone(customer.getPhone());
                req.setEmail(customer.getEmail());
                req.setShippingAddress(customer.getAddress());
            }
            model.addAttribute("checkoutRequest", req);
        }
        
        model.addAttribute("cartItems", cart);
        model.addAttribute("subTotal", subTotal);
        model.addAttribute("shippingFee", fee);
        model.addAttribute("total", subTotal.add(fee));
        
        model.addAttribute("bankName", bankName);
        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("bankOwner", bankOwner);

        return "user/checkout";
    }
    
    @PostMapping("/checkout/validate-voucher-ajax")
    @ResponseBody
    public ResponseEntity<?> validateVoucher(@RequestParam String code, HttpSession session, Principal principal) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "GiÃ¡Â»Â hÃƒÂ ng rÃ¡Â»â€”ng"));
        
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItemDto item : cart) {
            subTotal = subTotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        
        Customer customer = getCustomer(principal);
        VoucherService.VoucherResult res = voucherService.validateAndCalculate(code, customer, subTotal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", res.valid);
        response.put("message", res.message);
        if (res.valid) {
            response.put("discountAmount", res.discountAmount);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout/place")
    public String placeOrder(@Valid @ModelAttribute("checkoutRequest") CheckoutRequest request, BindingResult result, 
                           HttpSession session, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "GiÃ¡Â»Â hÃƒÂ ng rÃ¡Â»â€”ng!");
            return "redirect:/cart";
        }
        
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItemDto item : cart) {
            subTotal = subTotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.checkoutRequest", result);
            redirectAttributes.addFlashAttribute("checkoutRequest", request);
            return "redirect:/checkout";
        }

        BigDecimal fee = subTotal.compareTo(freeShippingThreshold) >= 0 ? BigDecimal.ZERO : shippingFee;
        Customer customer = getCustomer(principal);

        try {
            Order order = orderService.placeOrder(request, cart, customer, fee);
            session.removeAttribute("cart");
            return "redirect:/orders/success/" + order.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("checkoutRequest", request);
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orders/success/{id}")
    public String orderSuccess(@PathVariable Integer id, Principal principal, Model model) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("KhÃƒÂ´ng tÃƒÂ¬m thÃ¡ÂºÂ¥y Ã„â€˜Ã†Â¡n hÃƒÂ ng"));
        
        Customer customer = getCustomer(principal);
        if (customer == null || !customer.getId().equals(order.getCustomer().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Không có quyền truy cập");
        }
        
        model.addAttribute("order", order);
        model.addAttribute("bankName", bankName);
        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("bankOwner", bankOwner);
        return "user/success";
    }
}