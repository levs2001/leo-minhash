package ru.leo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.leo.data.Chunk;
import ru.leo.data.IParser;
import ru.leo.data.Row;
import ru.leo.data.json.InRamJsonParser;
import ru.leo.data.json.Rows;
import ru.leo.lsh.IHolder;
import ru.leo.lsh.RamHolder;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Path resources = Paths.get("src", "main", "resources");
    private static final Path dsPath = resources.resolve("wiki.json");
    private static final Path hashPath = resources.resolve("hash");

    private static final int STAGES_COUNT = 5;
    private static final int BUCKETS_COUNT = 5;
    private static final int BUCKETS_LINE_SIZE = 5;

    private static final int CHUNK_SIZE = 100;

    public static void main(String[] args) throws IOException {
        final IParser parser = new InRamJsonParser();
        parser.open(dsPath);

        final IHolder<Long> holder = new RamHolder<>(STAGES_COUNT, BUCKETS_COUNT, BUCKETS_LINE_SIZE);

        Rows rows = new Rows();
        Chunk chunk = new Chunk(0, CHUNK_SIZE);
        List<Row> rowsChunk;
        while ((rowsChunk = parser.getRows(chunk)) != null) {
            rowsChunk.forEach(r -> {
                holder.add(r.id(), r.text());
                rows.addRow(r);
            });
            chunk = chunk.next();
        }

        holder.save(hashPath);
        Row candidate = rows.getRow(889L);
        List<Long> nearest = holder.findNearest(candidate.text());

        log.info("Candidate: {}", candidate);
        log.info("Duplicate ids: {}", nearest);
        log.info("Duplicate rows: {}", rows.getRows(nearest));
    }
}
