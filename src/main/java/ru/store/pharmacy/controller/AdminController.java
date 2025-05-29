package ru.store.pharmacy.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.store.pharmacy.service.MedicinesService;
import ru.store.pharmacy.model.User;
import ru.store.pharmacy.service.UserService;
import ru.store.pharmacy.model.Medicines;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final MedicinesService medicinesService;

    public AdminController(UserService userService, MedicinesService medicinesService) {
        this.userService = userService;
        this.medicinesService = medicinesService;
    }

    // управление препаратами

    @GetMapping("/medicines")
    public String showMedicines(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model,
            HttpServletRequest request) {

        model.addAttribute("medicines", medicinesService.searchAndSort(searchQuery, sortBy, direction));
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("currentUri", request.getRequestURI());

        return "admin/medicines";
    }

    @GetMapping("/medicines/new")
    public String showAddMedicineForm(Model model) {
        model.addAttribute("medicine", new Medicines());
        return "admin/medicine-form";
    }

    @PostMapping("/medicines")
    public String saveMedicine(@ModelAttribute("medicine") Medicines medicine) {
        if (medicine.getId() != null) {
            medicinesService.updateMedicine(medicine.getId(), medicine);
        } else {
            medicinesService.addMedicine(medicine);
        }
        return "redirect:/admin/medicines";
    }

    @GetMapping("/medicines/edit/{id}")
    public String showEditMedicineForm(@PathVariable Long id, Model model) {
        Medicines medicine = medicinesService.getMedicineById(id);
        model.addAttribute("medicine", medicine);
        return "admin/medicine-form";
    }

    @PostMapping("/medicines/{id}")
    public String updateMedicine(@PathVariable Long id, @ModelAttribute("medicines") Medicines medicines) {
        medicinesService.updateMedicine(id, medicines);
        return "redirect:/admin/medicines";
    }

    @PostMapping("/medicines/delete/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        medicinesService.deleteMedicine(id);
        return "redirect:/admin/medicines";
    }

    // управление пользователями

    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{userId}/toggle-admin")
    public String toggleAdminRole(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.getUserByUsername(authentication.getName());
            userService.toggleAdminRole(userId, currentUser.getId());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.getUserByUsername(authentication.getName());
            userService.deleteUser(id, currentUser.getId());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // управление статистикой

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        model.addAttribute("medicineStats", medicinesService.getMedicineStatistics());
        model.addAttribute("userStats", userService.getUserStatistics());
        return "admin/statistics";
    }

    @GetMapping("/statistics/data")
    @ResponseBody
    public Map<String, Object> getStatisticsData() {
        return Map.of(
                "medicine", medicinesService.getMedicineStatistics(),
                "users", userService.getUserStatistics()
        );
    }
}
