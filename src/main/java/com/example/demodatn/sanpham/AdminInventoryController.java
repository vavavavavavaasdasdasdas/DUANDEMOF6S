package com.example.demodatn.sanpham;
import com.example.demodatn.entity.sanpham.ProductVariant;
import com.example.demodatn.repository.sanpham.AdminCatalogQuery;
import com.example.demodatn.repository.sanpham.MaterialRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/admin/inventory") @RequiredArgsConstructor
public class AdminInventoryController {
    private final AdminCatalogQuery query;
    private final AdminCatalogService catalog;
    private final InventoryService inventory;
    private final MaterialRepository materials;
    @Value("${shop.low-stock-threshold:5}") private int threshold;
    @GetMapping
    public String list(@RequestParam(defaultValue="all") String stock,@RequestParam(required=false) Integer variantId,@RequestParam(defaultValue="0") String page,@RequestParam(defaultValue="0") String historyPage,@RequestParam(required=false) Integer materialId,Model model) {
        var result=query.variantsPage(null,stock,Math.max(0,threshold),page,materialId);
        model.addAttribute("materials",materials.findAll()); model.addAttribute("materialId",materialId);
        model.addAttribute("variants",result.getContent()); model.addAttribute("dataPage",result); model.addAttribute("stock",stock);
        model.addAttribute("threshold",Math.max(0,threshold)); var logs=query.history(variantId,historyPage,materialId);model.addAttribute("history",logs.getContent());model.addAttribute("historyPage",logs);
        model.addAttribute("historyVariant",variantId); return "admin/ton-kho/danh-sach";
    }
    @GetMapping("/adjust/{id}")
    public String adjust(@PathVariable Integer id,Model model) {
        ProductVariant variant=catalog.variant(id); InventoryForm form=new InventoryForm();
        form.setVariantId(id); form.setExpectedQuantity(variant.getQuantity()); model.addAttribute("adjustment",form);
        return form(id,model);
    }
    @PostMapping("/adjust")
    public String save(@Valid @ModelAttribute("adjustment") InventoryForm form,BindingResult result,Model model,RedirectAttributes flash) {
        if (form.getVariantId() == null)
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Thiếu variant cần điều chỉnh.");
        if(!result.hasErrors()) {
            try {
                inventory.adjust(form); flash.addFlashAttribute("message","Đã điều chỉnh tồn kho và ghi lịch sử.");
                return "redirect:/admin/inventory?variantId="+form.getVariantId();
            } catch(CatalogValidationException ex) { result.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
        }
        return form(form.getVariantId(),model);
    }
    private String form(Integer id,Model model) { model.addAttribute("item",catalog.variant(id)); return "admin/ton-kho/bieu-mau"; }
}

