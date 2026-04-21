package ru.otus.hw.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Mono<String> handleError() {
        return Mono.just("redirect:/");
    }
}
