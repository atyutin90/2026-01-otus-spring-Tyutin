package ru.otus.hw.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dto.CommentDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

public class CommentConverterTest {

    @DisplayName("проверка конвертации Comment -> CommentDto")
    @Test
    void shouldConvertCommentToCommentDto() {
        var comment = getDbComments(getDbBooks()).stream().findFirst().orElse(null);
        var expectedCommentDto = new CommentDto(comment.getId(), comment.getBookId(), comment.getText());
        assertEquals(CommentConverter.commentDtoOf(comment), expectedCommentDto);
    }
}
