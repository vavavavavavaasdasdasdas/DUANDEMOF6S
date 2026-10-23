package com.example.demodatn.sanpham;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/admin/products-crud") @RequiredArgsConstructor
public class AdminProductController {
    private final AdminCatalogService catalog;
    private final AdminCatalogQuery query;
    private final CategoryRepository categories;
    private final ProductGalleryService gallery;
    private final com.example.demodatn.core.Pagination pagination;
    @org.springframework.beans.factory.annotation.Value("${shop.upload-max-bytes:5242880}") private long maxBytes;

    @GetMapping
    public String list(@RequestParam(defaultValue="") String keyword, @RequestParam(required=false) Integer categoryId,
                       @RequestParam(required=false) Boolean status, @RequestParam(defaultValue="newest") String sort,
                       @RequestParam(defaultValue="0") String page,  Model model) {
        model.addAttribute("productPage",query.products(keyword,categoryId,status,sort,page));
        model.addAttribute("categories",categories.findAll());
        model.addAttribute("keyword",keyword); model.addAttribute("categoryId",categoryId);
        model.addAttribute("status",status); model.addAttribute("sort",sort);
        return "admin/san-pham/danh-sach";
    }
    @GetMapping("/create")
    public String create(Model model) { model.addAttribute("product",new AdminProductForm()); return form(model); }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Product product=catalog.product(id);
        AdminProductForm form=new AdminProductForm();
        form.setId(id); form.setCode(product.getCode()); form.setName(product.getName());
        form.setDescription(product.getDescription()); form.setImageUrl(product.getImageUrl());
        form.setCategoryId(product.getCategory()==null?null:product.getCategory().getId()); form.setStatus(product.getStatus());
        model.addAttribute("product",form); return form(model);
    }
    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id,@RequestParam(defaultValue="0") String page,Model model) {
        model.addAttribute("product",catalog.product(id));
        var result=query.variantsPage(id,"all",0,page);model.addAttribute("variants",result.getContent());model.addAttribute("variantPage",result); return "admin/san-pham/chi-tiet";
    }
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("product") AdminProductForm product, BindingResult result, Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                Product saved=catalog.saveProduct(product);
                flash.addFlashAttribute("message","Đã lưu sản phẩm. Tiếp tục quản lý size, giá và tồn kho ở phần variant.");
                return "redirect:/admin/products-crud/view/"+saved.getId();
            } catch(CatalogValidationException ex) { result.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
              catch(DataIntegrityViolationException ex) { result.rejectValue("code","duplicate","Mã sản phẩm đã tồn tại hoặc dữ liệu liên quan vừa thay đổi."); }
        }
        if(product.getId()!=null) product.setImageUrl(catalog.product(product.getId()).getImageUrl());
        return form(model);
    }
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes flash) {
        try { flash.addFlashAttribute("message",catalog.deleteProduct(id)); }
        catch(DataIntegrityViolationException ex) { flash.addFlashAttribute("error","Không thể xóa: sản phẩm vừa phát sinh dữ liệu liên quan."); }
        return "redirect:/admin/products-crud";
    }
    private String form(Model model) {
        AdminProductForm form=(AdminProductForm)model.getAttribute("product");
        model.addAttribute("productImages", form.getId()==null ? java.util.List.of() : gallery.list(form.getId()));
        model.addAttribute("categories",categories.findAll());
        model.addAttribute("uploadMaxBytes",maxBytes);
        return "admin/san-pham/bieu-mau";
    }
}
