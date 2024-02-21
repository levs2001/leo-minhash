package ru.leo.lsh;

public interface IBucketsProducer {
    /**
     * Every bucket is an integer in [0, bucketsCount].
     */
    int[] buckets(String text);
}
