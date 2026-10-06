package com.example.demodatn.sanpham;

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
@RequestMapping("/admin/products-crud")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("product") ProductDto productDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/products/form";
        }

        Product product = new Product();
        if (productDto.getId() != null) {
            product = productRepository.findById(productDto.getId()).orElse(new Product());
        }
        product.setCode(productDto.getCode());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productDto.getImageUrl());
        product.setStatus(productDto.getStatus());
        
        if (productDto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(productDto.getCategoryId()).orElse(null));
        }

        productRepository.save(product);
        return "redirect:/admin/products-crud";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setStatus(product.getStatus());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }
        model.addAttribute("product", dto);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/products/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products-crud";
    }
}
