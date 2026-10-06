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
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {

    private final PromotionRepository promotionRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("promotions", promotionRepository.findAll());
        return "admin/promotions/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("promotion", new PromotionDto());
        return "admin/promotions/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("promotion") PromotionDto promotionDto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/promotions/form";
        }

        Promotion promotion = new Promotion();
        if (promotionDto.getId() != null) {
            promotion = promotionRepository.findById(promotionDto.getId()).orElse(new Promotion());
        }
        
        promotion.setName(promotionDto.getName());
        promotion.setDiscountType(promotionDto.getDiscountType());
        promotion.setDiscountValue(promotionDto.getDiscountValue());
        promotion.setStartDate(promotionDto.getStartDate());
        promotion.setEndDate(promotionDto.getEndDate());
        promotion.setStatus(promotionDto.getStatus());

        promotionRepository.save(promotion);
        return "redirect:/admin/promotions";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow();
        PromotionDto dto = new PromotionDto();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDiscountType(promotion.getDiscountType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setStatus(promotion.getStatus());
        
        model.addAttribute("promotion", dto);
        return "admin/promotions/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        promotionRepository.deleteById(id);
        return "redirect:/admin/promotions";
    }
}
