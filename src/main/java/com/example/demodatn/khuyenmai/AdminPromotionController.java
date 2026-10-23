package com.example.demodatn.khuyenmai;
import com.example.demodatn.entity.khuyenmai.Promotion;
import com.example.demodatn.sanpham.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.stream.Collectors;
@Controller @RequestMapping("/admin/promotions") @RequiredArgsConstructor
public class AdminPromotionController {
    private final PromotionRepository promotions;
    private final PromotionDetailRepository details;
    private final ProductRepository products;
    private final DiscountAdminService discounts;
    private final com.example.demodatn.core.Pagination pagination;
    @GetMapping public String list(@RequestParam(defaultValue="0") String page,Model model) { var result=pagination.load(page,org.springframework.data.domain.Sort.by("id").descending(),promotions::findAll);model.addAttribute("promotions",result.getContent());model.addAttribute("dataPage",result);return "admin/khuyen-mai/danh-sach"; }
    @GetMapping("/create") public String create(Model model) { model.addAttribute("promotion",new PromotionDto());return "admin/khuyen-mai/bieu-mau"; }
    @GetMapping("/edit/{id}") public String edit(@PathVariable Integer id,Model model) { PromotionDto f=new PromotionDto();BeanUtils.copyProperties(discounts.promotion(id),f);model.addAttribute("promotion",f);return "admin/khuyen-mai/bieu-mau"; }
    @PostMapping("/save") public String save(@Valid @ModelAttribute("promotion") PromotionDto f,BindingResult errors,RedirectAttributes flash) {
        if(!errors.hasErrors()) try { Promotion p=discounts.savePromotion(f);flash.addFlashAttribute("message","Đã lưu đợt giảm giá. Chọn sản phẩm áp dụng.");return "redirect:/admin/promotions/"+p.getId()+"/products"; }
        catch(DiscountValidationException ex) { errors.rejectValue(ex.getField(),"invalid",ex.getMessage()); }
        return "admin/khuyen-mai/bieu-mau";
    }
    @GetMapping("/{id}/products") public String selection(@PathVariable Integer id,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="0") String page,Model model) {
        model.addAttribute("promotion",discounts.promotion(id));
        org.springframework.data.jpa.domain.Specification<com.example.demodatn.entity.sanpham.Product> filter=(r,x,c)->c.or(c.like(c.lower(r.get("name")),like(q),'!'),c.like(c.lower(r.get("code")),like(q),'!'));
        var result=pagination.load(page,org.springframework.data.domain.Sort.by("name","id"),request->products.findAll(filter,request));
        model.addAttribute("products",result.getContent());model.addAttribute("productPage",result);model.addAttribute("q",q);
        model.addAttribute("selected",details.findByPromotion_IdAndProduct_IdIn(id,result.getContent().stream().map(p->p.getId()).toList()).stream().map(d->d.getProduct().getId()).collect(Collectors.toSet()));return "admin/khuyen-mai/san-pham-ap-dung";
    }
    @PostMapping("/{id}/products") public String select(@PathVariable Integer id,@RequestParam(required=false) List<Integer> productIds,@RequestParam(required=false) List<Integer> visibleIds,@RequestParam(defaultValue="0") String page,@RequestParam(defaultValue="") String q,RedirectAttributes flash) {
        try { discounts.selectProductPage(id,visibleIds==null?List.of():visibleIds,productIds==null?List.of():productIds);flash.addFlashAttribute("message","Đã lưu sản phẩm áp dụng."); }
        catch(IllegalArgumentException ex) { flash.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/admin/promotions/"+id+"/products?page="+java.net.URLEncoder.encode(page,java.nio.charset.StandardCharsets.UTF_8)+"&q="+java.net.URLEncoder.encode(q,java.nio.charset.StandardCharsets.UTF_8);
    }
    private String like(String q){return "%"+q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_").replace("[","![")+"%";}
    @PostMapping("/delete/{id}") public String delete(@PathVariable Integer id,RedirectAttributes flash) { discounts.deletePromotion(id);flash.addFlashAttribute("message","Đã xóa đợt giảm giá; giá đơn đã đặt được giữ nguyên.");return "redirect:/admin/promotions"; }
}
