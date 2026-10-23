package com.example.demodatn.nhanvien;
import com.example.demodatn.core.PeopleService;
import com.example.demodatn.core.PeopleValidationException;
import com.example.demodatn.repository.nhanvien.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
public class AdminEmployeeController {
    private final EmployeeRepository repository;
    private final PeopleService people;

    private final com.example.demodatn.core.Pagination pagination;

    @GetMapping
    public String list(@RequestParam(defaultValue="") String keyword,@RequestParam(defaultValue="0") String page,Model model) {
        String query=keyword.trim();
        var result=pagination.load(page,Sort.by(Sort.Direction.DESC,"id"),request->repository.findByFullNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(query,query,query,request));
        model.addAttribute("peoplePage",result);model.addAttribute("keyword",keyword);
        return "admin/nhan-vien/danh-sach";
    }
    @GetMapping("/create")
    public String create(Model model) { model.addAttribute("employee",new EmployeeDto());return "admin/nhan-vien/bieu-mau"; }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id,Model model) {
        var item=people.employee(id);var form=new EmployeeDto();BeanUtils.copyProperties(item,form);
        form.setUsername(item.getUser().getUsername());
        model.addAttribute("employee",form);return "admin/nhan-vien/bieu-mau";
    }
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id,@RequestParam(defaultValue="0") String page,Model model) {
        var item=people.employee(id);model.addAttribute("person",item);model.addAttribute("self",people.isSelf(item.getUser()));
        return "admin/nhan-vien/chi-tiet";
    }
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("employee") EmployeeDto form,BindingResult errors,Model model,RedirectAttributes flash) {
        if(!errors.hasErrors()) {
            try {
                var saved=people.saveEmployee(form);flash.addFlashAttribute("message","Đã lưu thông tin.");
                return "redirect:/admin/employees/detail/"+saved.getId();
            } catch(PeopleValidationException ex) { errors.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
            catch(DataIntegrityViolationException ex) { errors.reject("duplicate","Dữ liệu mã/email/SĐT/tài khoản vừa được sử dụng. Kiểm tra lại trước khi lưu."); }
        }
        form.setPassword(null);
        return "admin/nhan-vien/bieu-mau";
    }
    @PostMapping("/status/{id}")
    public String status(@PathVariable Integer id,@RequestParam boolean enabled,RedirectAttributes flash) {
        try { people.employeeStatus(id,enabled);flash.addFlashAttribute("message",enabled?"Đã kích hoạt. Tài khoản chỉ hoạt động khi mọi hồ sơ liên kết đều hoạt động.":"Đã vô hiệu hóa hồ sơ và tài khoản liên kết. Lịch sử vẫn được giữ nguyên."); }
        catch(IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/admin/employees/detail/"+id;
    }
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id,RedirectAttributes flash) {
        try { flash.addFlashAttribute("message",people.deleteEmployee(id)); }
        catch(IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        catch(DataIntegrityViolationException ex) { flash.addFlashAttribute("error","Dữ liệu vừa phát sinh tham chiếu. Không xóa; hãy vô hiệu hóa hồ sơ."); }
        return "redirect:/admin/employees";
    }
}
