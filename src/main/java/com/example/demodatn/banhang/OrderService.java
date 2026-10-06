package com.example.demodatn.banhang;
import com.example.demodatn.khachhang.Customer;
import com.example.demodatn.khachhang.CustomerRepository;
import com.example.demodatn.sanpham.ProductVariant;
import com.example.demodatn.sanpham.ProductVariantRepository;
import com.example.demodatn.nhanvien.Employee;
import com.example.demodatn.khuyenmai.Voucher;
import com.example.demodatn.khuyenmai.VoucherRepository;

import com.example.demodatn.banhang.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(List<CartItemDto> cartItems, Customer customer, Employee employee, String orderType, Voucher voucher, String paymentMethod, String address) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setOrderType(orderType);
        order.setStatus("CONFIRMED");
        
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucher != null) {
            LocalDateTime now = LocalDateTime.now();
            if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
                throw new RuntimeException("Voucher is not yet active");
            }
            if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
                throw new RuntimeException("Voucher is expired");
            }
            if (voucher.getUsageLimit() != null && voucher.getUsedCount() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
                throw new RuntimeException("Voucher usage limit reached");
            }
            if (voucher.getMinOrderValue() != null && totalAmount.compareTo(voucher.getMinOrderValue()) < 0) {
                throw new RuntimeException("Order total does not meet minimum value for this voucher");
            }
            if (!Boolean.TRUE.equals(voucher.getStatus())) {
                throw new RuntimeException("Voucher is inactive");
            }

            if ("PERCENT".equals(voucher.getDiscountType())) {
                discountAmount = totalAmount.multiply(voucher.getDiscountValue()).divide(new BigDecimal(100));
            } else {
                discountAmount = voucher.getDiscountValue();
            }
            if (voucher.getMaxDiscount() != null && discountAmount.compareTo(voucher.getMaxDiscount()) > 0) {
                discountAmount = voucher.getMaxDiscount();
            }
            order.setVoucher(voucher);
            
            // Increment used count
            voucher.setUsedCount((voucher.getUsedCount() == null ? 0 : voucher.getUsedCount()) + 1);
            voucherRepository.save(voucher);
        }
        
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(totalAmount.subtract(discountAmount).max(BigDecimal.ZERO));
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(address);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);

        // Deduct stock and save details
        for (CartItemDto item : cartItems) {
            ProductVariant variant = productVariantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));
            
            int newQuantity = variant.getQuantity() - item.getQuantity();
            if (newQuantity < 0) {
                throw new InsufficientStockException("Not enough stock for " + item.getProductName());
            }
            variant.setQuantity(newQuantity);
            productVariantRepository.save(variant);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProductVariant(variant);
            detail.setPrice(item.getPrice());
            detail.setQuantity(item.getQuantity());
            detail.setTotalPrice(item.getTotalPrice());
            orderDetailRepository.save(detail);
        }

        return savedOrder;
    }
}
