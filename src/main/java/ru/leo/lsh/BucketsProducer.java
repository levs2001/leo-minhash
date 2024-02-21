package ru.leo.lsh;

import ru.leo.minhash.IMinHashesProducer;
import ru.leo.minhash.TreeMinHashesProducer;

public class BucketsProducer implements IBucketsProducer {
    private final int stagesCount;
    private final int bucketsCount;
    private final int bucketLineSize;
    private final IMinHashesProducer minHashesProducer;

    BucketsProducer(int stagesCount, int bucketsCount, int bucketLineSize) {
        this.stagesCount = stagesCount;
        this.bucketsCount = bucketsCount;
        this.bucketLineSize = bucketLineSize;
        minHashesProducer = new TreeMinHashesProducer(stagesCount * bucketLineSize);
    }

    @Override
    public int[] buckets(String text) {
        minHashesProducer.reset();
        long[] hashes = minHashesProducer.getHashes(text);

        int[] buckets = new int[stagesCount];
        for (int i = 0; i < stagesCount; i++) {
            // Hash algorithm copied from Arrays.hash(long[])
            int result = 1;
            for (int j = 0; j < bucketLineSize; j++) {
                long element = hashes[i * bucketLineSize + j];
                int elementHash = (int) (element ^ (element >>> 32));
                result = 31 * result + elementHash;
            }
            buckets[i] = Math.abs(result) % bucketsCount;
        }

        return buckets;
    }
}
