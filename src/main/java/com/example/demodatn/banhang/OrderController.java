package com.example.demodatn.banhang;
import com.example.demodatn.banhang.dto.CartItemDto;
import com.example.demodatn.banhang.dto.CheckoutRequest;
import com.example.demodatn.banhang.dto.OrderResponseDto;
import com.example.demodatn.khachhang.Customer;
import com.example.demodatn.khachhang.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";
        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.stream().map(CartItemDto::getTotalPrice).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        model.addAttribute("checkoutRequest", new CheckoutRequest());
        return "user/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(HttpSession session,
                                  @Valid @ModelAttribute("checkoutRequest") CheckoutRequest request,
                                  BindingResult bindingResult,
                                  Model model) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            model.addAttribute("total", cart.stream().map(CartItemDto::getTotalPrice).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
            return "user/checkout";
        }

        try {
            Customer customer = new Customer();
            customer.setFullName(request.getCustomerName());
            customer.setPhone(request.getPhoneNumber());
            customer.setAddress(request.getShippingAddress());
            customerRepository.save(customer); // basic guest logic

            Order order = orderService.createOrder(cart, customer, null, "ONLINE", null, request.getPaymentMethod(), request.getShippingAddress());
            // Since order service might not set phone properly on order yet, let's just make sure it's created
            order.setPhoneNumber(request.getPhoneNumber());
            orderRepository.save(order);
            
            session.removeAttribute("cart");
            return "redirect:/orders/success?code=" + order.getOrderCode();
        } catch (InsufficientStockException e) {
            return "redirect:/orders/checkout?error=" + e.getMessage();
        } catch (Exception e) {
            return "redirect:/orders/checkout?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/success")
    public String success(@RequestParam String code, Model model) {
        model.addAttribute("code", code);
        return "user/success";
    }

    private OrderResponseDto mapToDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setCustomerName(order.getCustomer() != null ? order.getCustomer().getFullName() : "");
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setShippingAddress(order.getShippingAddress());
        return dto;
    }

    @GetMapping
    public String myOrders(Model model) {
        List<OrderResponseDto> orders = orderRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@org.springframework.web.bind.annotation.PathVariable Integer id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            model.addAttribute("order", mapToDto(order));
            List<OrderDetail> details = orderDetailRepository.findAll().stream().filter(d -> d.getOrder().getId().equals(id)).collect(Collectors.toList());
            model.addAttribute("orderDetails", details);
        }
        return "user/order-detail";
    }
}
