package com.example.demodatn.banhang;
import com.example.demodatn.entity.donhang.Invoice;
import com.example.demodatn.entity.donhang.Order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/invoices")
@RequiredArgsConstructor
public class AdminInvoiceController {
    private final InvoiceRepository invoices;
    @org.springframework.beans.factory.annotation.Value("${shop.name:SHOP GIÀY}") private String shopName;
    @org.springframework.beans.factory.annotation.Value("${shop.address:}") private String shopAddress;
    @org.springframework.beans.factory.annotation.Value("${shop.phone:}") private String shopPhone;

    private final com.example.demodatn.core.Pagination pagination;

    @GetMapping
    public String list(@RequestParam(defaultValue="") String q,@RequestParam(required=false) LocalDate from,
            @RequestParam(required=false) LocalDate to,@RequestParam(defaultValue="0") String page,Model model) {
        Specification<Invoice> filter=(root,query,cb)->cb.conjunction();
        if(!q.isBlank()) { String code=q.trim().toUpperCase(java.util.Locale.ROOT);
            filter=filter.and((r,x,c)->c.like(c.upper(r.get("invoiceCode")), "%"+code.replace("!","!!").replace("%","!%").replace("_","!_")+"%", '!')); }
        if(from!=null) filter=filter.and((r,x,c)->c.greaterThanOrEqualTo(r.get("issuedAt"),from.atStartOfDay()));
        if(to!=null) filter=filter.and((r,x,c)->c.lessThan(r.get("issuedAt"),to.plusDays(1).atStartOfDay()));
        if(from!=null && to!=null && to.isBefore(from)) model.addAttribute("error","Ngày kết thúc phải từ ngày bắt đầu.");
        final var selection=filter;
        model.addAttribute("invoices",pagination.load(page,Sort.by(Sort.Order.desc("issuedAt"),Sort.Order.desc("id")),request->invoices.findAll(selection,request)));
        model.addAttribute("q",q);model.addAttribute("from",from);model.addAttribute("to",to);
        return "admin/hoa-don/danh-sach";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id,Model model) {
        model.addAttribute("invoice",invoices.findById(id).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND)));
        model.addAttribute("shopName",shopName);model.addAttribute("shopAddress",shopAddress);model.addAttribute("shopPhone",shopPhone);
        return "admin/hoa-don/chi-tiet";
    }
}
