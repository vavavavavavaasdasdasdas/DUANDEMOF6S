package com.example.demodatn.khuyenmai;

import com.example.demodatn.banhang.*;
import com.example.demodatn.khachhang.*;
import com.example.demodatn.sanpham.*;
import com.example.demodatn.nhanvien.*;
import com.example.demodatn.khuyenmai.*;
import com.example.demodatn.thongke.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final VoucherRepository voucherRepository;
    private final CustomerRepository customerRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vouchers", voucherRepository.findAll());
        return "admin/vouchers/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("voucher", new VoucherDto());
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/vouchers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("voucher") VoucherDto voucherDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerRepository.findAll());
            return "admin/vouchers/form";
        }

        Voucher voucher = new Voucher();
        if (voucherDto.getId() != null) {
            voucher = voucherRepository.findById(voucherDto.getId()).orElse(new Voucher());
        }
        
        voucher.setCode(voucherDto.getCode());
        voucher.setName(voucherDto.getName());
        voucher.setDiscountType(voucherDto.getDiscountType());
        voucher.setDiscountValue(voucherDto.getDiscountValue());
        voucher.setMinOrderValue(voucherDto.getMinOrderValue());
        voucher.setMaxDiscount(voucherDto.getMaxDiscount());
        voucher.setStartDate(voucherDto.getStartDate());
        voucher.setEndDate(voucherDto.getEndDate());
        voucher.setUsageLimit(voucherDto.getUsageLimit());
        voucher.setUsedCount(voucherDto.getUsedCount());
        voucher.setIsPublic(voucherDto.getIsPublic());
        voucher.setStatus(voucherDto.getStatus());
        
        if (voucherDto.getCustomerId() != null) {
            voucher.setCustomer(customerRepository.findById(voucherDto.getCustomerId()).orElse(null));
        } else {
            voucher.setCustomer(null);
        }

        voucherRepository.save(voucher);
        return "redirect:/admin/vouchers";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow();
        VoucherDto dto = new VoucherDto();
        dto.setId(voucher.getId());
        dto.setCode(voucher.getCode());
        dto.setName(voucher.getName());
        dto.setDiscountType(voucher.getDiscountType());
        dto.setDiscountValue(voucher.getDiscountValue());
        dto.setMinOrderValue(voucher.getMinOrderValue());
        dto.setMaxDiscount(voucher.getMaxDiscount());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        dto.setUsageLimit(voucher.getUsageLimit());
        dto.setUsedCount(voucher.getUsedCount());
        dto.setIsPublic(voucher.getIsPublic());
        dto.setStatus(voucher.getStatus());
        
        if (voucher.getCustomer() != null) {
            dto.setCustomerId(voucher.getCustomer().getId());
        }
        
        model.addAttribute("voucher", dto);
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/vouchers/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        voucherRepository.deleteById(id);
        return "redirect:/admin/vouchers";
    }
}
