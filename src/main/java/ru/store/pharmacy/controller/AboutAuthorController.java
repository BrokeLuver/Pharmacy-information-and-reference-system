package ru.store.pharmacy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutAuthorController {

    @GetMapping("/about_author")
    public String about() {
        return "main/about_author";
    }
}
