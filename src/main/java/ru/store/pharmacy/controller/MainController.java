package ru.store.pharmacy.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.store.pharmacy.service.MedicinesService;

@Controller
@RequestMapping("/main")
public class MainController {

    private final MedicinesService medicinesService;

    public MainController(MedicinesService medicinesService) {
        this.medicinesService = medicinesService;
    }

    @GetMapping
    public String main(
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

        return "main/main";
    }
}