package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> findByBookId(long bookId);

    CommentDto findById(long id);

    void update(CommentDto data);

    CommentDto create(long bookId, CommentDto data);

    void deleteById(long id);
}
