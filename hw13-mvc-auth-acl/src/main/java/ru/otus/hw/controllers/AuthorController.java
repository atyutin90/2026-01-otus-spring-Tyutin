package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Controller
@RequiredArgsConstructor
public class AuthorController extends AbstractAccessController {

    private final AuthorService authorService;

    private final MessageSource messageSource;

    @GetMapping("/authors")
    private String listPage(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "/author/list";
    }

    @GetMapping("/authors/new")
    private String editPage(Model model) {
        model.addAttribute("author", new AuthorDto(null, null));
        return "/author/edit";
    }

    @PostMapping("/authors")
    private String saveAuthor(@Valid @ModelAttribute("author") AuthorDto author,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "/author/edit";
        }

        return accessValidate(
            () -> {
                if (author.id() != null) {
                    authorService.update(author);
                    return "redirect:/authors/%d".formatted(author.id());
                } else {
                    var newAuthor = authorService.create(author);
                    return "redirect:/authors/%d".formatted(newAuthor.id());
                }
            },
            () -> {
                model.addAttribute("author", author);
                model.addAttribute("errorMessage",
                    messageSource.getMessage("error.not-allowed-create-or-modify-record", null, getLocale()));
                return "/author/edit";
            }
        );
    }

    @GetMapping("/authors/{id}")
    private String showPage(@PathVariable long id, Model model) {
        AuthorDto author = authorService.findById(id);
        model.addAttribute("author", author);
        return "/author/show";
    }

    @GetMapping("/authors/{id}/edit")
    private String editPage(@PathVariable long id, Model model) {
        AuthorDto author = authorService.findById(id);
        model.addAttribute("author", author);
        return "/author/edit";
    }

    @DeleteMapping("/authors/{id}")
    private String deleteAuthor(@PathVariable long id, Model model) {
        return accessValidate(
            () -> {
                authorService.deleteById(id);
                return "redirect:/authors";
            },
            () -> {
                model.addAttribute("authors", authorService.findAll());
                model.addAttribute("errorMessage",
                    messageSource.getMessage("error.not-allowed-delete-record", null, getLocale()));
                return "/author/list";
            }
        );
    }
}
