package ru.leo.data.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.leo.data.Chunk;
import ru.leo.data.IParser;
import ru.leo.data.Row;

public class InRamJsonParser implements IParser {
    private final ObjectMapper mapper = new ObjectMapper() {{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }};

    private final Rows rows = new Rows();

    @Override
    public void open(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String rowStr;
            while ((rowStr = reader.readLine()) != null) {
                Row row = mapper.readValue(rowStr, Row.class);
                rows.addRow(row);
            }
        }
    }

    @Override
    public List<Row> getRows(Chunk chunk) {
        return rows.getRows(chunk);
    }
}
