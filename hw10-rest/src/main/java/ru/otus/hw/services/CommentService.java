package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> findAll();

    List<CommentDto> findByBookId(long bookId);

    CommentDto findById(long id);

    CommentDto update(CommentDto data);

    CommentDto create(CommentDto data);

    void deleteById(long id);
}
