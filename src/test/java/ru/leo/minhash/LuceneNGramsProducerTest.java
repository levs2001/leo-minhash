package ru.leo.minhash;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LuceneNGramsProducerTest {
    private static final LuceneNGramsProducer nGramsConsumer = new LuceneNGramsProducer();

    @Test
    public void testBasic() throws IOException {
        // Из предложения длиной 14 можем построить 7 nGrams
        assertEquals(7, hashesFromNGrams("Mama myla ramu").size());
    }

    @Test
    public void testDifference() throws IOException {
        assertEquals(hashesFromNGrams("Mama myla ramu"), hashesFromNGrams("Mama myla ramu"));
        assertNotEquals(hashesFromNGrams("Mama myla ramu"), hashesFromNGrams("Papa myla ramu"));
    }

    private Set<Integer> hashesFromNGrams(String text) throws IOException {
        Iterator<byte[]> nGrams = nGramsConsumer.getNGrams(text);
        Set<Integer> nGramSet = new HashSet<>();
        while (nGrams.hasNext()) {
            byte[] nGram = nGrams.next();
            assertEquals(nGram.length, 8);
            nGramSet.add(Arrays.hashCode(nGram));
        }
        return nGramSet;
    }

    @AfterAll
    public static void close() throws Exception {
        nGramsConsumer.close();
    }
}