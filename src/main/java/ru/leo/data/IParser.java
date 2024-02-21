package ru.leo.data;

import java.io.IOException;
import java.nio.file.Path;

public interface IParser extends IChunkHolder {
    /**
     * Open file with given path.
     */
    void open(Path path) throws IOException;
}
