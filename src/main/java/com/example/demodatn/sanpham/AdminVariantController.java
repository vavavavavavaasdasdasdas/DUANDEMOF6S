package com.example.demodatn.sanpham;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

@Controller @RequiredArgsConstructor
public class AdminVariantController {
    private final AdminCatalogService catalog;
    private final AdminCatalogQuery query;
    private final ProductRepository products;
    private final SizeRepository sizes;
    @GetMapping("/admin/variants")
    public String list(@RequestParam(required=false) Integer productId, @RequestParam(defaultValue="0") int page, Model model) { return listFor(productId,page,model); }
    @GetMapping("/admin/products-crud/{productId}/variants")
    public String productVariants(@PathVariable Integer productId, @RequestParam(defaultValue="0") int page, Model model) { return listFor(productId,page,model); }
    private String listFor(Integer productId, int page, Model model) {
        var result=query.variantsPage(productId,"all",0,page,20);
        model.addAttribute("variants",result.getContent()); model.addAttribute("dataPage",result);
        model.addAttribute("selectedProduct",productId==null?null:catalog.product(productId));
        model.addAttribute("products",products.findAll()); model.addAttribute("productId",productId);
        return "admin/bien-the/danh-sach";
    }
    @GetMapping("/admin/variants/create")
    public String create(@RequestParam(required=false) Integer productId, Model model) {
        VariantForm form=new VariantForm(); form.setProductId(productId); model.addAttribute("variant",form);
        return form(model);
    }
    @GetMapping("/admin/variants/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        ProductVariant variant=catalog.variant(id); VariantForm form=new VariantForm();
        form.setId(id); form.setProductId(variant.getProduct().getId()); form.setSizeId(variant.getSize().getId());
        form.setPrice(variant.getPrice()); form.setQuantity(variant.getQuantity()); form.setExpectedQuantity(variant.getQuantity()); form.setStatus(variant.getStatus());
        model.addAttribute("variant",form); return form(model);
    }
    @PostMapping("/admin/variants/save")
    public String save(@Valid @ModelAttribute("variant") VariantForm form, BindingResult result, Model model, RedirectAttributes flash) {
        if(!result.hasErrors()) {
            try {
                ProductVariant saved=catalog.saveVariant(form);
                flash.addFlashAttribute("message",com.example.demodatn.core.NhanHienThi.text("ui.052"));
                return "redirect:/admin/products-crud/"+saved.getProduct().getId()+"/variants";
            } catch(CatalogValidationException ex) { result.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
              catch(DataIntegrityViolationException ex) { result.rejectValue("sizeId","duplicate","Cặp sản phẩm–size đã tồn tại hoặc dữ liệu vừa thay đổi."); }
        }
        return form(model);
    }
    private String form(Model model) {
        model.addAttribute("products",products.findAll()); model.addAttribute("sizes",sizes.findAll());
        return "admin/bien-the/bieu-mau";
    }
}

