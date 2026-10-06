package com.example.demodatn.nhanvien;

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
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
public class AdminEmployeeController {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "admin/employees/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("employee", new EmployeeDto());
        return "admin/employees/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("employee") EmployeeDto employeeDto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/employees/form";
        }

        Employee employee = new Employee();
        if (employeeDto.getId() != null) {
            employee = employeeRepository.findById(employeeDto.getId()).orElse(new Employee());
        }
        
        employee.setEmployeeCode(employeeDto.getEmployeeCode());
        employee.setFullName(employeeDto.getFullName());
        employee.setPhone(employeeDto.getPhone());
        employee.setEmail(employeeDto.getEmail());
        employee.setAddress(employeeDto.getAddress());
        employee.setPosition(employeeDto.getPosition());
        employee.setStatus(employeeDto.getStatus());
        
        if (employeeDto.getUserId() != null) {
            employee.setUser(userRepository.findById(employeeDto.getUserId()).orElse(null));
        }

        employeeRepository.save(employee);
        return "redirect:/admin/employees";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setFullName(employee.getFullName());
        dto.setPhone(employee.getPhone());
        dto.setEmail(employee.getEmail());
        dto.setAddress(employee.getAddress());
        dto.setPosition(employee.getPosition());
        dto.setStatus(employee.getStatus());
        
        if (employee.getUser() != null) {
            dto.setUserId(employee.getUser().getId());
        }
        
        model.addAttribute("employee", dto);
        return "admin/employees/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        employeeRepository.deleteById(id);
        return "redirect:/admin/employees";
    }
}
