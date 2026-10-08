package com.example.demodatn.banhang;
import com.example.demodatn.khachhang.Customer;
import com.example.demodatn.sanpham.ProductVariant;
import com.example.demodatn.nhanvien.Employee;
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
    @org.springframework.beans.factory.annotation.Value("${shop.shipping.fee:30000}") private BigDecimal configuredShippingFee;
    @org.springframework.beans.factory.annotation.Value("${shop.shipping.free-threshold:500000}") private BigDecimal freeShippingThreshold;
    private final VoucherService voucherService;
    private final PricingService pricingService;
    private final com.example.demodatn.sanpham.InventoryService inventoryService;

    private final InvoiceRepository invoices;

    @Transactional(rollbackFor = Exception.class)
    public Order placeOrder(CheckoutRequest request, List<CartItemDto> cartItems, Customer customer, BigDecimal ignoredShippingFee) {
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
        return saveSale(order, cartItems, request.getVoucherCode(), null);
    }

    @Transactional(rollbackFor = Exception.class)
    public Order createPosOrder(List<CartItemDto> items, Customer customer, Employee employee,
            String voucherCode, String paymentMethod, String address, BigDecimal cashReceived) {
        if (employee == null || !Boolean.TRUE.equals(employee.getStatus()) || employee.getUser() == null || !Boolean.TRUE.equals(employee.getUser().getStatus()))
            throw new IllegalArgumentException("Tài khoản chưa gắn với nhân viên đang hoạt động.");
        if (!java.util.Set.of("CASH", "TRANSFER").contains(paymentMethod))
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
        if (customer != null && !Boolean.TRUE.equals(customer.getStatus()))
            throw new IllegalArgumentException("Khách hàng đã bị vô hiệu hóa.");
        Order order = new Order();
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setOrderType("POS");
        order.setStatus("CONFIRMED");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("CASH".equals(paymentMethod) ? "PAID" : "UNPAID");
        order.setShippingAddress(address);
        return saveSale(order, items, voucherCode, cashReceived);
    }

    private Order saveSale(Order order, List<CartItemDto> items, String voucherCode, BigDecimal cashReceived) {
        var sorted = sortedItems(items);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItemDto item : sorted) {
            ProductVariant variant = inventoryService.lock(item.getVariantId());
            BigDecimal price = pricingService.calculatePrice(variant).getFinalPrice();
            OrderDetail detail = new OrderDetail();
            detail.setProductVariant(variant);
            detail.setProductName(variant.getProduct().getName());
            detail.setSizeName(variant.getSize().getSizeValue());
            detail.setPrice(price);
            detail.setQuantity(item.getQuantity());
            detail.setTotalPrice(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            order.addDetail(detail);
            subtotal = subtotal.add(detail.getTotalPrice());
        }
        order.setTotalAmount(subtotal);
        order.setShippingFee("POS".equals(order.getOrderType()) || subtotal.compareTo(freeShippingThreshold) >= 0
                ? BigDecimal.ZERO : configuredShippingFee);
        order.setDiscountAmount(BigDecimal.ZERO);
        if (voucherCode != null && !voucherCode.isBlank()) {
            var result = voucherService.consume(voucherCode, order.getCustomer(), subtotal);
            order.setVoucher(result.voucher);
            order.setDiscountAmount(result.discountAmount);
        }
        order.setFinalAmount(subtotal.subtract(order.getDiscountAmount()).add(order.getShippingFee()));
        if ("POS".equals(order.getOrderType()) && "CASH".equals(order.getPaymentMethod())
                && (cashReceived == null || cashReceived.compareTo(order.getFinalAmount()) < 0))
            throw new IllegalArgumentException("Tiền khách đưa phải đủ thanh toán tổng đơn hàng.");
        order.setOrderCode("DH-" + UUID.randomUUID());
        orderRepository.save(order);
        order.setOrderCode(String.format("DH%06d", order.getId()));
        for (CartItemDto item : sorted)
            inventoryService.sell(item.getVariantId(), item.getQuantity(), order.getOrderCode());
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceCode(String.format("HD%06d", order.getId()));
        invoice.setIssuedAt(order.getCreatedAt());
        invoice.setCustomerName(order.getCustomer() != null ? order.getCustomer().getFullName()
                : order.getReceiverName() != null ? order.getReceiverName() : "Khách lẻ");
        invoice.setCustomerPhone(order.getCustomer() != null ? order.getCustomer().getPhone() : order.getPhoneNumber());
        invoice.setEmployeeName(order.getEmployee() == null ? com.example.demodatn.core.NhanHienThi.text("ui.026") : order.getEmployee().getFullName());
        if ("POS".equals(order.getOrderType()) && "CASH".equals(order.getPaymentMethod())) {
            invoice.setCashReceived(cashReceived);
            invoice.setChangeAmount(cashReceived.subtract(order.getFinalAmount()));
        }
        invoices.save(invoice);
        return order;
    }

    public List<String> allowedStatuses(Order order) {
        return switch (order.getStatus()) {
            case "PENDING" -> List.of("CONFIRMED", "CANCELLED");
            case "CONFIRMED" -> "POS".equals(order.getOrderType())
                    ? List.of("DELIVERED", "CANCELLED") : List.of("SHIPPING", "CANCELLED");
            case "PREPARING" -> List.of("SHIPPING", "CANCELLED");
            case "SHIPPING" -> List.of("DELIVERED", "CANCELLED");
            default -> List.of();
        };
    }

    @Transactional
    public void confirmPayment(Integer id) {
        Order order = orderRepository.findForUpdate(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        if ("CANCELLED".equals(order.getStatus()) || !java.util.Set.of("TRANSFER", "BANK_TRANSFER").contains(order.getPaymentMethod()))
            throw new IllegalArgumentException("Chỉ xác nhận chuyển khoản cho đơn chưa hủy.");
        order.setPaymentStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
    }

    private List<CartItemDto> sortedItems(List<CartItemDto> items) {
        if (items == null || items.isEmpty() || items.stream().anyMatch(i -> i == null || i.getVariantId() == null || i.getQuantity() == null || i.getQuantity() <= 0))
            throw new IllegalArgumentException("Giỏ hàng hoặc số lượng không hợp lệ.");
        return items.stream().sorted(java.util.Comparator.comparing(CartItemDto::getVariantId)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer id, String status) {
        if (!java.util.Set.of("PENDING","CONFIRMED","PREPARING","SHIPPING","DELIVERED","CANCELLED").contains(status))
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ.");
        Order order = orderRepository.findForUpdate(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        if (status.equals(order.getStatus())) return;
        if (!allowedStatuses(order).contains(status))
            throw new IllegalArgumentException("Không thể chuyển trạng thái theo luồng này.");
        if ("DELIVERED".equals(status)) {
            if (java.util.Set.of("TRANSFER", "BANK_TRANSFER").contains(order.getPaymentMethod()) && !"PAID".equals(order.getPaymentStatus()))
                throw new IllegalArgumentException("Cần xác nhận đã nhận chuyển khoản trước khi hoàn tất.");
            order.setPaymentStatus("PAID");
        }
        if ("CANCELLED".equals(status)) {
            for (OrderDetail detail : order.getOrderDetails().stream().sorted(java.util.Comparator.comparing(d -> d.getProductVariant().getId())).toList()) {
                inventoryService.restore(detail.getProductVariant().getId(),detail.getQuantity(),order.getOrderCode());
            }
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
    }
}
