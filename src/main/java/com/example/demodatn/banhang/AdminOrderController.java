package com.example.demodatn.banhang;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderRepository orderRepository;
    private final OrderStatusService orderService;
    private final InvoiceRepository invoices;
    public static final java.util.Map<String,String> STATUS_LABELS=OrderStatusService.LABELS;

    @ModelAttribute public void labels(Model model) { model.addAttribute("statusLabels",STATUS_LABELS); }

    private final com.example.demodatn.core.Pagination pagination;

    @GetMapping
    public String list(@RequestParam(required=false) String status,@RequestParam(defaultValue="") String q,
            @RequestParam(defaultValue="0") String page,Model model) {
        Specification<Order> filter=(r,x,c)->c.conjunction();
        if(status!=null && !status.isBlank()) filter=filter.and((r,x,c)->c.equal(r.get("status"),status));
        if(!q.isBlank()) { String code=q.trim().toUpperCase(java.util.Locale.ROOT);
            filter=filter.and((r,x,c)->c.like(c.upper(r.get("orderCode")), "%"+code.replace("!","!!").replace("%","!%").replace("_","!_")+"%", '!')); }
        final var selection=filter;
        model.addAttribute("orders",pagination.load(page,Sort.by(Sort.Order.desc("createdAt"),Sort.Order.desc("id")),request->orderRepository.findAll(selection,request)));
        model.addAttribute("q",q);model.addAttribute("status",status);
        return "admin/don-hang/danh-sach";
    }

    @GetMapping({"/{id}","/detail/{id}"})
    public String detail(@PathVariable Integer id,Model model) {
        var order=orderRepository.findById(id).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        model.addAttribute("order",order);
        model.addAttribute("allowedStatuses",orderService.allowedStatuses(order));
        model.addAttribute("invoice",invoices.findByOrder_Id(id).orElse(null));
        return "admin/don-hang/chi-tiet";
    }

    @PostMapping({"/update-status/{id}","/{id}/status"})
    public String updateStatus(@PathVariable Integer id,@RequestParam String status,RedirectAttributes flash) {
        try { orderService.updateStatus(id,status); flash.addFlashAttribute("message","Đã cập nhật đơn hàng. Đơn hủy được hoàn kho và ghi lịch sử."); }
        catch(IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/admin/orders/"+id;
    }

    @PostMapping("/{id}/confirm-payment")
    public String payment(@PathVariable Integer id,RedirectAttributes flash) {
        try { orderService.confirmPayment(id); flash.addFlashAttribute("message","Đã xác nhận nhận chuyển khoản."); }
        catch(IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/admin/orders/"+id;
    }
}
