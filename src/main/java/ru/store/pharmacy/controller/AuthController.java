package ru.store.pharmacy.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.store.pharmacy.model.User;
import ru.store.pharmacy.repository.UserRepository;
import ru.store.pharmacy.service.UserService;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home() {
        return "main/home";
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "registered", required = false) String registered,
            Model model
    ) {
        if (error != null) model.addAttribute("error", "Неверные учетные данные");
        if (registered != null) model.addAttribute("registered", "Регистрация прошла успешно!");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "redirect:/register?error=username";
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            return "redirect:/register?error=phone";
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "redirect:/register?error=email";
        }
        user.setRole("ROLE_USER");
        userService.createUserWithHistory(user);
        return "redirect:/login?registered";
    }

    // можно перенастроить для первого админа
    @PostConstruct
    public void initAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setPhone("+79857154035");
            admin.setEmail("luverbigboy@gamil.com");
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("Администратор создан: admin/admin");
        } else {
            System.out.println("Администратор уже существует");
        }
    }
}
