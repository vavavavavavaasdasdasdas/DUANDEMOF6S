package com.example.demodatn.banhang;
import com.example.demodatn.khachhang.Customer;
import com.example.demodatn.khachhang.CustomerRepository;
import com.example.demodatn.sanpham.ProductVariant;
import com.example.demodatn.sanpham.ProductVariantRepository;
import com.example.demodatn.nhanvien.Employee;
import com.example.demodatn.khuyenmai.Voucher;
import com.example.demodatn.khuyenmai.VoucherRepository;
import com.example.demodatn.khuyenmai.VoucherService;
import com.example.demodatn.banhang.dto.CartItemDto;
import com.example.demodatn.banhang.dto.CheckoutRequest;
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
    private final VoucherService voucherService;
    private final PricingService pricingService;

    @Transactional(rollbackFor = Exception.class)
    public Order placeOrder(CheckoutRequest request, List<CartItemDto> cartItems, Customer customer, BigDecimal shippingFee) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giá» hÃ ng rá»—ng!");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderType("ONLINE");
        order.setStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus("UNPAID");
        order.setReceiverName(request.getReceiverName());
        order.setPhoneNumber(request.getPhone());
        order.setEmail(request.getEmail());
        order.setShippingAddress(request.getShippingAddress());
        
        BigDecimal subTotal = BigDecimal.ZERO;

        for (CartItemDto item : cartItems) {

            
            ProductVariant variant = productVariantRepository.findById(item.getVariantId())
                    .orElseThrow(() -> new IllegalArgumentException("Sáº£n pháº©m khÃ´ng tá»“n táº¡i!"));
            
            PriceInfo priceInfo = pricingService.calculatePrice(variant);
            BigDecimal price = priceInfo.getFinalPrice();

            int updatedRows = productVariantRepository.deductStock(variant.getId(), item.getQuantity());
            if (updatedRows == 0) {
                throw new IllegalArgumentException("Sáº£n pháº©m " + variant.getProduct().getName() + " - Size " + variant.getSize().getSizeValue() + " khÃ´ng Ä‘á»§ tá»“n kho!");
            }

            OrderDetail detail = new OrderDetail();
            detail.setProductVariant(variant);
            detail.setProductName(variant.getProduct().getName());
            detail.setSizeName(variant.getSize().getSizeValue());
            detail.setPrice(price);
            detail.setQuantity(item.getQuantity());
            detail.setTotalPrice(price.multiply(new BigDecimal(item.getQuantity())));
            
            order.addDetail(detail);
            subTotal = subTotal.add(detail.getTotalPrice());
        }

        order.setTotalAmount(subTotal);
        order.setShippingFee(shippingFee);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            VoucherService.VoucherResult vResult = voucherService.validateAndCalculate(request.getVoucherCode(), customer, subTotal);
            if (!vResult.valid) {
                throw new IllegalArgumentException("Voucher lá»—i: " + vResult.message);
            }
            discountAmount = vResult.discountAmount;
            order.setVoucher(vResult.voucher);
            order.setDiscountAmount(discountAmount);
            
            vResult.voucher.setUsedCount((vResult.voucher.getUsedCount() == null ? 0 : vResult.voucher.getUsedCount()) + 1);
            voucherRepository.save(vResult.voucher);
        } else {
            order.setDiscountAmount(BigDecimal.ZERO);
        }

        BigDecimal finalAmount = subTotal.subtract(discountAmount).add(shippingFee);
        order.setFinalAmount(finalAmount);

        order.setOrderCode("DH-" + UUID.randomUUID().toString()); // Temp
        order = orderRepository.save(order);
        order.setOrderCode(String.format("DH%06d", order.getId()));
        
        return order;
    }

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
            discountAmount = voucher.getDiscountValue();
            order.setVoucher(voucher);
        }
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(totalAmount.subtract(discountAmount).max(BigDecimal.ZERO));
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(address);
        Order savedOrder = orderRepository.save(order);
        for (CartItemDto item : cartItems) {
            ProductVariant variant = productVariantRepository.findById(item.getVariantId()).orElseThrow();
            int newQuantity = variant.getQuantity() - item.getQuantity();
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