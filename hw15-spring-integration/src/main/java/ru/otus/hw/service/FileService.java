package ru.otus.hw.service;

import ru.otus.hw.domain.Caterpillar;

import java.util.List;

public interface FileService {

    List<Caterpillar> getCaterpillarsData(String fileName);
}
