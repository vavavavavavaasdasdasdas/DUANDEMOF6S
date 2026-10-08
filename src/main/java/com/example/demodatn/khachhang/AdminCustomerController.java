package com.example.demodatn.khachhang;

import com.example.demodatn.core.PeopleService;
import com.example.demodatn.core.PeopleValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {
    private final CustomerRepository repository;
    private final PeopleService people;
    private final com.example.demodatn.banhang.OrderRepository orders;

    @GetMapping
    public String list(@RequestParam(defaultValue="") String keyword,@RequestParam(defaultValue="0") int page,Model model) {
        String query=keyword.trim();
        var pageable=PageRequest.of(Math.max(0,page),10,Sort.by(Sort.Direction.DESC,"id"));
        var result=repository.findByFullNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(query,query,query,pageable);
        if(result.getTotalPages()>0 && page>=result.getTotalPages())
            result=repository.findByFullNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(query,query,query,pageable.withPage(result.getTotalPages()-1));
        model.addAttribute("peoplePage",result);model.addAttribute("keyword",keyword);
        return "admin/khach-hang/danh-sach";
    }
    @GetMapping("/create")
    public String create(Model model) { model.addAttribute("customer",new CustomerDto());return "admin/khach-hang/bieu-mau"; }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id,Model model) {
        var item=people.customer(id);var form=new CustomerDto();BeanUtils.copyProperties(item,form);
        model.addAttribute("customer",form);return "admin/khach-hang/bieu-mau";
    }
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id,@RequestParam(defaultValue="0") int page,Model model) {
        var item=people.customer(id);model.addAttribute("person",item);model.addAttribute("self",people.isSelf(item.getUser()));
        model.addAttribute("orderPage",orders.findByCustomer_IdOrderByCreatedAtDescIdDesc(id,org.springframework.data.domain.PageRequest.of(Math.max(0,page),10)));
        return "admin/khach-hang/chi-tiet";
    }
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("customer") CustomerDto form,BindingResult errors,Model model,RedirectAttributes flash) {
        if(!errors.hasErrors()) {
            try {
                var saved=people.saveCustomer(form);flash.addFlashAttribute("message","Đã lưu thông tin.");
                return "redirect:/admin/customers/detail/"+saved.getId();
            } catch(PeopleValidationException ex) { errors.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
            catch(DataIntegrityViolationException ex) { errors.reject("duplicate","Dữ liệu mã/email/SĐT/tài khoản vừa được sử dụng. Kiểm tra lại trước khi lưu."); }
        }
        return "admin/khach-hang/bieu-mau";
    }
    @PostMapping("/status/{id}")
    public String status(@PathVariable Integer id,@RequestParam boolean enabled,RedirectAttributes flash) {
        try { people.customerStatus(id,enabled);flash.addFlashAttribute("message",enabled?"Đã kích hoạt. Tài khoản chỉ hoạt động khi mọi hồ sơ liên kết đều hoạt động.":"Đã vô hiệu hóa hồ sơ và tài khoản liên kết. Lịch sử vẫn được giữ nguyên."); }
        catch(IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/admin/customers/detail/"+id;
    }
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id,RedirectAttributes flash) {
        try { flash.addFlashAttribute("message",people.deleteCustomer(id)); }
        catch(IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        catch(DataIntegrityViolationException ex) { flash.addFlashAttribute("error","Dữ liệu vừa phát sinh tham chiếu. Không xóa; hãy vô hiệu hóa hồ sơ."); }
        return "redirect:/admin/customers";
    }
}
