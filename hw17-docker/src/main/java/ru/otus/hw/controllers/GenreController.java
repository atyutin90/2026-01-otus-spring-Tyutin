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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    private String listPage(Model model) {
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genre/list";
    }

    @GetMapping("/genres/new")
    private String editPage(Model model) {
        model.addAttribute("genre", new GenreDto(null, null));
        return "genre/edit";
    }

    @PostMapping("/genres")
    private String saveGenre(@Valid @ModelAttribute("genre") GenreDto genre,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "genre/edit";
        }

        if (genre.id() != null) {
            genreService.update(genre);
        } else {
            genre = genreService.create(genre);
        }
        return "redirect:/genres/%d".formatted(genre.id());
    }

    @GetMapping("/genres/{id}")
    private String showPage(@PathVariable long id, Model model) {
        GenreDto genre = genreService.findById(id);
        model.addAttribute("genre", genre);
        return "genre/show";
    }

    @GetMapping("/genres/{id}/edit")
    private String editPage(@PathVariable long id, Model model) {
        var genre = genreService.findById(id);
        model.addAttribute("genre", genre);
        return "genre/edit";
    }

    @DeleteMapping("/genres/{id}")
    private String deleteGenre(@PathVariable long id) {
        genreService.deleteById(id);
        return "redirect:/genres";
    }
}
