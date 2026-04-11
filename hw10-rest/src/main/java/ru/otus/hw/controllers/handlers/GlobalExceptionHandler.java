package ru.otus.hw.controllers.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.exceptions.GenresException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
    public ResponseEntity<ErrorMessageDto> handeBookNotFoundException() {
        String errorText = messageSource.getMessage("error.book.not-found", null, getLocale());
        return new ResponseEntity<>(new ErrorMessageDto(errorText), NOT_FOUND);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handeAuthorNotFoundException() {
        String errorText = messageSource.getMessage("error.author.not-found", null, getLocale());
        return new ResponseEntity<>(new ErrorMessageDto(errorText), NOT_FOUND);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handeValidate(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        for (var error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handeGenreNotFoundException() {
        String errorText = messageSource.getMessage("error.genre.not-found", null, getLocale());
        return new ResponseEntity<>(new ErrorMessageDto(errorText), NOT_FOUND);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handeCommentNotFoundException() {
        String errorText = messageSource.getMessage("error.comment.not-found", null, getLocale());
        return new ResponseEntity<>(new ErrorMessageDto(errorText), NOT_FOUND);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(GenresException.class)
    public ResponseEntity<ErrorMessageDto> handeGenresException() {
        String errorText = messageSource.getMessage("error.bad-request", null, getLocale());
        return new ResponseEntity<>(new ErrorMessageDto(errorText), NOT_FOUND);
    }
}
