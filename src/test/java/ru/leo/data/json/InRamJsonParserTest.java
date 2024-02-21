package ru.leo.data.json;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.leo.data.Chunk;
import ru.leo.data.IParser;
import ru.leo.data.Row;

import static org.junit.jupiter.api.Assertions.*;

class InRamJsonParserTest {
    private static final Path smallTestJsonPath = Paths.get("src", "test", "resources", "small_test.json");

    @Test
    public void testSmallParsing() throws IOException {
        IParser parser = new InRamJsonParser();
        parser.open(smallTestJsonPath);
        Chunk firstChunk = new Chunk(0, 1);
        List<Row> firstRow = parser.getRows(firstChunk);
        assertEquals(1, firstRow.size());
        assertEquals(1L, firstRow.get(0).id());
        assertFalse(firstRow.get(0).text().isEmpty());

        assertEquals(firstRow, parser.getRows(firstChunk));

        Chunk secondChunk = firstChunk.next();
        List<Row> secondRow = parser.getRows(secondChunk);
        assertEquals(1, secondRow.size());
        assertEquals(2L, secondRow.get(0).id());

        Chunk threeRowsChunk = new Chunk(0, 3);
        List<Row> threeRows = parser.getRows(threeRowsChunk);
        assertEquals(3, threeRows.size());
    }

    @Test
    public void testMoreThanAllRead() throws IOException {
        IParser parser = new InRamJsonParser();
        parser.open(smallTestJsonPath);
        Chunk allChunk = new Chunk(0, 10);
        List<Row> allRows = parser.getRows(allChunk);
        assertEquals(10, allRows.size());
        assertNull(parser.getRows(allChunk.next()));

        // in file 10 rows, but chunk is bigger, it is ok, just give what we can.
        allChunk = new Chunk(0, 11);
        allRows = parser.getRows(allChunk);
        assertEquals(10, allRows.size());
    }
}