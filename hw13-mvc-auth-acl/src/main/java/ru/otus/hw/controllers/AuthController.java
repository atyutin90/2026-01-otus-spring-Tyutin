package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.exceptions.UserAlreadyExistException;
import ru.otus.hw.services.UserService;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final MessageSource messageSource;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", UserDto.builder().build());
        return "auth/register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "auth/login";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") UserDto user,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        } else {
            try {
                userService.register(user);
                return "auth/login";
            } catch (UserAlreadyExistException e) {
                bindingResult.reject(
                    "error",
                    messageSource.getMessage("error.user-already-registered", null, getLocale())
                );
                return "auth/register";
            }
        }
    }
}
