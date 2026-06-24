package ru.otus.hw.controllers.handlers;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.exceptions.GenresException;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound() {
        return "redirect:/";
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handeMethodArgumentTypeMismatchException() {
        String errorText = messageSource.getMessage("error.bad-request", null, getLocale());
        ModelAndView modelAndView = new ModelAndView("/error-page");
        modelAndView.addObject("message", errorText);
        modelAndView.addObject("status", BAD_REQUEST.value());
        return modelAndView;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(BookNotFoundException.class)
    public ModelAndView handeBookNotFoundException() {
        String errorText = messageSource.getMessage("error.book.not-found", null, getLocale());
        ModelAndView modelAndView = new ModelAndView("/error-page");
        modelAndView.addObject("message", errorText);
        modelAndView.addObject("status", NOT_FOUND.value());
        return modelAndView;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(AuthorNotFoundException.class)
    public ModelAndView handeAuthorNotFoundException() {
        String errorText = messageSource.getMessage("error.author.not-found", null, getLocale());
        ModelAndView modelAndView = new ModelAndView("/error-page");
        modelAndView.addObject("message", errorText);
        modelAndView.addObject("status", NOT_FOUND.value());
        return modelAndView;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(GenreNotFoundException.class)
    public ModelAndView handeGenreNotFoundException() {
        String errorText = messageSource.getMessage("error.genre.not-found", null, getLocale());
        ModelAndView modelAndView = new ModelAndView("/error-page");
        modelAndView.addObject("message", errorText);
        modelAndView.addObject("status", NOT_FOUND.value());
        return modelAndView;
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CommentNotFoundException.class)
    public ModelAndView handeCommentNotFoundException() {
        String errorText = messageSource.getMessage("error.comment.not-found", null, getLocale());
        ModelAndView modelAndView = new ModelAndView("/error-page");
        modelAndView.addObject("message", errorText);
        modelAndView.addObject("status", NOT_FOUND.value());
        return modelAndView;
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(GenresException.class)
    public ModelAndView handeGenresException() {
        String errorText = messageSource.getMessage("error.bad-request", null, getLocale());
        ModelAndView modelAndView = new ModelAndView("/error-page");
        modelAndView.addObject("message", errorText);
        modelAndView.addObject("status", BAD_REQUEST.value());
        return modelAndView;
    }

    @ResponseStatus(TOO_MANY_REQUESTS)
    @ExceptionHandler(CallNotPermittedException.class)
    public ModelAndView handeCallNotPermittedException(CallNotPermittedException ex) {
        ex.getMessage();
        String errorText = messageSource.getMessage("error.too-many-request", null, getLocale());
        ModelAndView modelAndView = new ModelAndView("/error-page");
        modelAndView.addObject("message", errorText);
        modelAndView.addObject("status", TOO_MANY_REQUESTS.value());
        return modelAndView;
    }

}
