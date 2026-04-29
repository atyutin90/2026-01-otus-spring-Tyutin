package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"genres"})
@ToString(exclude = {"genres"})
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    private long id;

    @Column("title")
    private String title;

    @Column("author_id")
    private Long authorId;

    @Transient
    private List<Long> genresIds = new ArrayList<>();

    public void addGenreId(Long genreId) {
        if (genreId != null) {
            genresIds.add(genreId);
        }
    }
}
