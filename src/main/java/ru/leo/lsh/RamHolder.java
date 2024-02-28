package ru.leo.lsh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RamHolder<T> implements IHolder<T> {
    private static final String BUCKETS_FILE = "buckets.json";
    private static final String CONFIG_FILE = "config.json";

    // stagesCount - count of minhashes
    private final List<List<Set<T>>> stageBuckets;
    private final IBucketsProducer bucketsProducer;
    private final Config config;

    private RamHolder(Config config, List<List<Set<T>>> stageBuckets) {
        this.config = config;
        this.stageBuckets = stageBuckets;
        bucketsProducer = new BucketsProducer(config.stagesCount, config.bucketsCount, config.bucketsLineSize);
    }

    @Override
    public void add(T id, String text) {
        int[] currentBuckets = bucketsProducer.buckets(text);
        for (int i = 0; i < currentBuckets.length; i++) {
            List<Set<T>> hashBuckets = stageBuckets.get(i);
            int bucket = currentBuckets[i];
            hashBuckets.get(bucket).add(id);
        }
    }

    @Override
    public List<T> findNearest(String text) {
        int[] currentBuckets = bucketsProducer.buckets(text);
        Set<T> nearest = new HashSet<>(stageBuckets.get(0).get(currentBuckets[0]));
        // TODO: Надо не все бакеты пересекать, а то он только дубликаты ищет
        //  (регулировать точность, пересекать без одного и объединять, например).
        for (int i = 1; i < currentBuckets.length; i++) {
            nearest.retainAll(stageBuckets.get(i).get(currentBuckets[i]));
        }

        return new ArrayList<>(nearest);
    }

    @Override
    public void save(Path path) throws IOException {
        Files.createDirectory(path);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(path.resolve(BUCKETS_FILE).toFile(), stageBuckets);
        mapper.writeValue(path.resolve(CONFIG_FILE).toFile(), config);
    }

    public record Config(int stagesCount, int bucketsCount, int bucketsLineSize) {
    }

    public static <T> RamHolder<T> create(Config config) {
        List<List<Set<T>>> stageBuckets = new ArrayList<>(config.stagesCount);
        for (int i = 0; i < config.stagesCount; i++) {
            List<Set<T>> stage = new ArrayList<>(config.bucketsCount);
            for (int j = 0; j < config.bucketsCount; j++) {
                stage.add(new HashSet<>());
            }
            stageBuckets.add(stage);
        }

        return new RamHolder<>(config, stageBuckets);
    }

    public static <T> RamHolder<T> create(int stagesCount, int bucketsCount, int bucketsLineSize) {
        return create(new Config(stagesCount, bucketsCount, bucketsLineSize));
    }

    /**
     * Only for long because int and long is json can be same.
     */
    public static RamHolder<Long> fromFile(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Config config = mapper.readValue(path.resolve(CONFIG_FILE).toFile(), new TypeReference<>() {
        });
        List<List<Set<Long>>> stageBuckets = mapper.readValue(path.resolve(BUCKETS_FILE).toFile(), new TypeReference<>() {
        });

        return new RamHolder<>(config, stageBuckets);
    }
}
