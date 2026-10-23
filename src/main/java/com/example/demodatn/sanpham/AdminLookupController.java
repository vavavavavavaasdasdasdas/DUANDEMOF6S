package com.example.demodatn.sanpham;
import com.example.demodatn.entity.sanpham.Category;
import com.example.demodatn.entity.sanpham.Material;
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

@Controller @RequestMapping("/admin/{kind:categories|sizes|materials}") @RequiredArgsConstructor
public class AdminLookupController {
    private final CategoryRepository categories;
    private final SizeRepository sizes;
    private final MaterialRepository materials;
    private final AdminCatalogService catalog;
    @ModelAttribute
    public void common(@PathVariable String kind, Model model) {
        model.addAttribute("kind",kind); model.addAttribute("lookupTitle","sizes".equals(kind)?"Size":("materials".equals(kind)?"Chất liệu":"Danh mục"));
    }
    private final com.example.demodatn.core.Pagination pagination;

    @GetMapping
    public String list(@PathVariable String kind, @RequestParam(defaultValue="0") String page, Model model) {
        model.addAttribute("items","sizes".equals(kind)? pagination.load(page,org.springframework.data.domain.Sort.by("id"),sizes::findAll):("materials".equals(kind)?pagination.load(page,org.springframework.data.domain.Sort.by("id"),materials::findAll):pagination.load(page,org.springframework.data.domain.Sort.by("id"),categories::findAll))); return "admin/danh-muc-kich-co/danh-sach";
    }
    @GetMapping("/create")
    public String create(Model model) { model.addAttribute("lookup",new LookupForm()); return "admin/danh-muc-kich-co/bieu-mau"; }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String kind,@PathVariable Integer id, Model model) {
        LookupForm form=new LookupForm(); form.setId(id);
        if("sizes".equals(kind)) form.setValue(sizes.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)).getSizeValue());
        else if ("materials".equals(kind)) { Material material=materials.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)); form.setValue(material.getName()); form.setStatus(material.getStatus()); }
        else { Category category=categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND)); form.setValue(category.getName()); form.setStatus(category.getStatus()); }
        model.addAttribute("lookup",form); return "admin/danh-muc-kich-co/bieu-mau";
    }
    @PostMapping("/save")
    public String save(@PathVariable String kind,@Valid @ModelAttribute("lookup") LookupForm form,BindingResult result,Model model,RedirectAttributes flash) {
        if(!result.hasErrors()) {
            try { catalog.saveLookup(kind,form); flash.addFlashAttribute("message","Đã lưu "+("sizes".equals(kind)?"size.":("materials".equals(kind)?"chất liệu.":"danh mục."))); return "redirect:/admin/"+kind; }
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

