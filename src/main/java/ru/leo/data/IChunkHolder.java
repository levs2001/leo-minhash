package ru.leo.data;

import java.util.List;

public interface IChunkHolder {
    /**
     * Get rows in given chunk.
     *
     * @return null if we chunk beginning is too big, otherwise rows that we have for this chunk.
     */
    List<Row> getRows(Chunk chunk);
}
