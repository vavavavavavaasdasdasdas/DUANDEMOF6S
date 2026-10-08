package com.example.demodatn.sanpham;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/admin/{kind:categories|sizes}") @RequiredArgsConstructor
public class AdminLookupController {
    private final CategoryRepository categories;
    private final SizeRepository sizes;
    private final AdminCatalogService catalog;
    @ModelAttribute
    public void common(@PathVariable String kind, Model model) {
        model.addAttribute("kind",kind); model.addAttribute("lookupTitle","sizes".equals(kind)?"Size":"Danh mục");
    }
    @GetMapping
    public String list(@PathVariable String kind, @RequestParam(defaultValue="0") int page, Model model) {
        long count = "sizes".equals(kind) ? sizes.count() : categories.count();
        int current = Math.min(Math.max(0,page),(int)Math.max(0,(count-1)/20));
        var paging = org.springframework.data.domain.PageRequest.of(current,20,org.springframework.data.domain.Sort.by("id"));
        model.addAttribute("items","sizes".equals(kind)? sizes.findAll(paging):categories.findAll(paging)); return "admin/danh-muc-kich-co/danh-sach";
    }
    @GetMapping("/create")
    public String create(Model model) { model.addAttribute("lookup",new LookupForm()); return "admin/danh-muc-kich-co/bieu-mau"; }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String kind,@PathVariable Integer id, Model model) {
        LookupForm form=new LookupForm(); form.setId(id);
        if("sizes".equals(kind)) form.setValue(sizes.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)).getSizeValue());
        else { Category category=categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)); form.setValue(category.getName()); form.setStatus(category.getStatus()); }
        model.addAttribute("lookup",form); return "admin/danh-muc-kich-co/bieu-mau";
    }
    @PostMapping("/save")
    public String save(@PathVariable String kind,@Valid @ModelAttribute("lookup") LookupForm form,BindingResult result,Model model,RedirectAttributes flash) {
        if(!result.hasErrors()) {
            try { catalog.saveLookup(kind,form); flash.addFlashAttribute("message","Đã lưu "+("sizes".equals(kind)?"size.":"danh mục.")); return "redirect:/admin/"+kind; }
            catch(CatalogValidationException ex) { result.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
            catch(DataIntegrityViolationException ex) { result.rejectValue("value","duplicate","Giá trị đã tồn tại hoặc vừa được sử dụng."); }
        }
        return "admin/danh-muc-kich-co/bieu-mau";
    }
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String kind,@PathVariable Integer id,RedirectAttributes flash) {
        try { String message=catalog.deleteLookup(kind,id); flash.addFlashAttribute(message.startsWith("Không")?"error":"message",message); }
        catch(DataIntegrityViolationException ex) { flash.addFlashAttribute("error","Không thể xóa vì dữ liệu đang được tham chiếu."); }
        return "redirect:/admin/"+kind;
    }
}

