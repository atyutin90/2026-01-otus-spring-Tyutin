package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

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
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/author/edit";
        }

        if (author.id() != null) {
            authorService.update(author);
        } else {
            author = authorService.create(author);
        }
        return "redirect:/authors/%d".formatted(author.id());
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
    private String deleteAuthor(@PathVariable long id) {
        authorService.deleteById(id);
        return "redirect:/authors";
    }
}
