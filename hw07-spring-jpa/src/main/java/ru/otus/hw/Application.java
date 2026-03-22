package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// Оставил для Web интерфейса H2
		// Console.main(args);
		SpringApplication.run(Application.class, args);
	}

}
