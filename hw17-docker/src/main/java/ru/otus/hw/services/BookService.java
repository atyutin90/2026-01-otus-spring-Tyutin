package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import java.util.List;

public interface BookService {

    List<BookDto> findAll();

    BookDto findById(long id);

    void update(BookDto data);

    BookDto create(BookDto data);

    void deleteById(long id);
}
