package ru.leo.minhash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeMinHashesProducer implements IMinHashesProducer {
    private static final Logger log = LoggerFactory.getLogger(TreeMinHashesProducer.class);

    private final INGramsProducer nGramsProducer = new LuceneNGramsProducer();
    private final ArrayList<SortedSet<Long>> hashes;

    public TreeMinHashesProducer(int hashesCount) {
        hashes = new ArrayList<>(hashesCount);
        for (int i = 0; i < hashesCount; i++) {
            hashes.add(new TreeSet<>());
        }
    }

    @Override
    public long[] getHashes(String text) {
        Iterator<byte[]> nGrams;
        try {
            nGrams = nGramsProducer.getNGrams(text);
        } catch (IOException e) {
            log.error("Can't parse n-grams", e);
            return null;
        }

        while (nGrams.hasNext()) {
            addNGram(nGrams.next());
        }

        long[] result = new long[hashes.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = hashes.get(i).first();
        }

        // TODO: Не нравится, что nGramsProducer закрывается и тут и в reset
        closeNGramsProducer();
        return result;
    }

    @Override
    public void reset() {
        for (Set<Long> hash : hashes) {
            hash.clear();
        }
        closeNGramsProducer();
    }

    private void addNGram(byte[] nGram) {
        for (int i = 0; i < hashes.size(); i++) {
            // TODO: В теории можно переделать на поиск минимума, без всяких сетов.
            hashes.get(i).add(hash(getLongLittleEndian(nGram), i));
        }
    }

    private void closeNGramsProducer() {
        try {
            nGramsProducer.close();
        } catch (Exception e) {
            log.error("Can't close nGramsProducer during reset.", e);
            throw new RuntimeException(e);
        }
    }

    // TODO: Более эффективный хеш
    private static long hash(long a, int b) {
        long result = (31 + a) * 31 + b;;
        for (int i = 0; i < b; i++) {
            result = (31 + result) * 31 + b;
        }
        return result;
    }

    private static long getLongLittleEndian(byte[] buf) {
        return ((long) buf[7] << 56) // no mask needed
                | ((buf[6] & 0xffL) << 48)
                | ((buf[5] & 0xffL) << 40)
                | ((buf[4] & 0xffL) << 32)
                | ((buf[3] & 0xffL) << 24)
                | ((buf[2] & 0xffL) << 16)
                | ((buf[1] & 0xffL) << 8)
                | ((buf[0] & 0xffL)); // no shift needed
    }
}
