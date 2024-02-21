package ru.leo.minhash;

import java.io.IOException;
import java.util.Iterator;

// AutoCloseable because of reader for text, that should be closed.
public interface INGramsProducer extends AutoCloseable {
    /**
     * ASCII n-grams with fixed size 8. (8 bytes = 8 syms in ASCII)
     */
    Iterator<byte[]> getNGrams(String text) throws IOException;
}
