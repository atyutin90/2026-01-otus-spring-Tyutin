package ru.otus.hw.services.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.mongo.CommentDoc;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentMongoServiceImpl implements CommentMongoService {

    private final CommentMongoRepository commentRepository;

    private final BookMongoRepository bookRepository;

    @Override
    public Optional<CommentDoc> findById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDoc> findByBookId(String bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        return commentRepository.findByBookId(bookId);
    }

    @Override
    @Transactional
    public CommentDoc insert(String text, String bookId) {
        return save(null, text, bookId);
    }

    @Override
    @Transactional
    public CommentDoc update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private CommentDoc save(String id, String text, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        var comment = new CommentDoc(id, text, book);
        return commentRepository.save(comment);
    }
}
