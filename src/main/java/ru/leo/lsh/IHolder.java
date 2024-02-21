package ru.leo.lsh;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IHolder<T> {
    /**
     * Add text to base.
     */
    void add(T id, String text);

    /**
     * @return text that are near given.
     */
    List<T> findNearest(String text);

    void save(Path path) throws IOException;
}
