package com.example.demodatn.khachhang;

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
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/customers/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "admin/customers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("customer") CustomerDto customerDto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/customers/form";
        }

        Customer customer = new Customer();
        if (customerDto.getId() != null) {
            customer = customerRepository.findById(customerDto.getId()).orElse(new Customer());
        }
        customer.setCustomerCode(customerDto.getCustomerCode());
        customer.setFullName(customerDto.getFullName());
        customer.setPhone(customerDto.getPhone());
        customer.setEmail(customerDto.getEmail());
        customer.setAddress(customerDto.getAddress());
        customer.setStatus(customerDto.getStatus());

        customerRepository.save(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setFullName(customer.getFullName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setStatus(customer.getStatus());
        
        model.addAttribute("customer", dto);
        return "admin/customers/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        customerRepository.deleteById(id);
        return "redirect:/admin/customers";
    }
}
