package com.example.demodatn.khuyenmai;
import com.example.demodatn.entity.khuyenmai.Voucher;
import com.example.demodatn.khachhang.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/admin/vouchers") @RequiredArgsConstructor
public class AdminVoucherController {
    private final VoucherRepository vouchers;
    private final CustomerRepository customers;
    private final DiscountAdminService discounts;
    private final com.example.demodatn.core.Pagination pagination;
    @GetMapping public String list(@RequestParam(defaultValue="0") String page,Model model) { var result=pagination.load(page,org.springframework.data.domain.Sort.by("id").descending(),vouchers::findAll);model.addAttribute("vouchers",result.getContent());model.addAttribute("dataPage",result);return "admin/voucher/danh-sach"; }
    private String form(Model model) { model.addAttribute("customers",customers.findAll());return "admin/voucher/bieu-mau"; }
    @GetMapping("/create") public String create(Model model) { model.addAttribute("voucher",new VoucherDto());return form(model); }
    @GetMapping("/edit/{id}") public String edit(@PathVariable Integer id,Model model) {
        Voucher v=discounts.voucher(id);VoucherDto f=new VoucherDto();BeanUtils.copyProperties(v,f);
        if(v.getCustomer()!=null) f.setCustomerId(v.getCustomer().getId());model.addAttribute("voucher",f);model.addAttribute("usedCount",v.getUsedCount());return form(model);
    }
    @PostMapping("/save") public String save(@Valid @ModelAttribute("voucher") VoucherDto f,BindingResult errors,Model model,RedirectAttributes flash) {
        if(!errors.hasErrors()) try { discounts.saveVoucher(f);flash.addFlashAttribute("message","Đã lưu voucher.");return "redirect:/admin/vouchers"; }
        catch(DiscountValidationException ex) { errors.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
        catch(DataIntegrityViolationException ex) { errors.rejectValue("code","duplicate","Mã voucher đã tồn tại hoặc dữ liệu đã thay đổi. Kiểm tra rồi lưu lại."); }
        return form(model);
    }
    @PostMapping("/delete/{id}") public String delete(@PathVariable Integer id,RedirectAttributes flash) { flash.addFlashAttribute("message",discounts.deleteVoucher(id));return "redirect:/admin/vouchers"; }
}
