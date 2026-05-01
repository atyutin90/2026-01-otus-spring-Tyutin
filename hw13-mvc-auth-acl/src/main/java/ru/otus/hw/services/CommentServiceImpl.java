package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.springframework.security.acls.domain.BasePermission.DELETE;
import static org.springframework.security.acls.domain.BasePermission.WRITE;
import static ru.otus.hw.converters.CommentConverter.commentDtoOf;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final AclServiceService aclServiceService;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByBookId(long bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id %d not found".formatted(bookId)));
        return commentRepository.findByBookId(bookId).stream()
                .map(CommentConverter::commentDtoOf)
                .toList();
    }

    @Override
    public CommentDto findById(long id) {
        return commentRepository.findById(id)
                .map(CommentConverter::commentDtoOf)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id: %d not found".formatted(id)));
    }

    @PreAuthorize("hasPermission(#data.id, 'ru.otus.hw.models.Comment', 'WRITE')")
    @Override
    @Transactional
    public void update(CommentDto data) {
        var id = data.id();
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id: %d not found".formatted(id)));
        comment.setText(data.text());
        commentRepository.save(comment);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Override
    @Transactional
    public CommentDto create(long bookId, CommentDto data) {
        var comment = save(0L, data.text(), bookId);
        aclServiceService.createAcl(comment, false, WRITE, DELETE);
        return commentDtoOf(comment);
    }

    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Comment', 'DELETE')")
    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }
}
