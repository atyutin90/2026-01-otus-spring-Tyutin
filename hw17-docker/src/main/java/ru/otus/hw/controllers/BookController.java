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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final GenreService genreService;

    private final AuthorService authorService;

    private final CommentService commentService;

    @GetMapping("/books")
    private String listPage(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        return "book/list";
    }

    @GetMapping("/books/new")
    private String editPage(Model model) {
        model.addAttribute("book", new BookDto(null, null, null, null));
        enrichment(model);
        return "book/edit";
    }

    @PostMapping("/books")
    private String saveBook(@Valid @ModelAttribute("book") BookDto book,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            enrichment(model);
            return "book/edit";
        }

        if (book.id() != null) {
            bookService.update(book);
        } else {
            book = bookService.create(book);
        }
        return "redirect:/books/%d".formatted(book.id());
    }

    @GetMapping("/books/{id}")
    private String showPage(@PathVariable long id, Model model) {
        var book = bookService.findById(id);
        var comments = commentService.findByBookId(id);
        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        enrichment(model);
        return "book/show";
    }

    @GetMapping("/books/{id}/edit")
    private String editPage(@PathVariable long id, Model model) {
        var book = bookService.findById(id);
        model.addAttribute("book", book);
        enrichment(model);
        return "book/edit";
    }

    @DeleteMapping("/books/{id}")
    private String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    private void enrichment(Model model) {
        var genres = genreService.findAll();
        var authors = authorService.findAll();
        model.addAttribute("genres", genres);
        model.addAttribute("authors", authors);
    }
}
