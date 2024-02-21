package ru.leo.minhash;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class LuceneNGramsProducer implements INGramsProducer {
    // Fixed size of n-grams 8.
    private final Tokenizer tokenizer = new NGramTokenizer(8, 8);
    private CharTermAttribute charTermAttribute;

    @Override
    public Iterator<byte[]> getNGrams(String text) throws IOException {
        tokenizer.setReader(new StringReader(text));
        charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
        tokenizer.reset();

        return new NGramsIterator();
    }

    @Override
    public void close() throws Exception {
        // На случай если не дошли до конца итератора
        tokenizer.end();
        tokenizer.close();
    }

    public class NGramsIterator implements Iterator<byte[]> {
        private boolean end;
        private byte[] current;

        @Override
        public boolean hasNext() {
            if (end) {
                return false;
            }

            if (current == null) {
                try {
                    if (tokenizer.incrementToken()) {
                        current = charTermAttribute.toString().getBytes(StandardCharsets.US_ASCII);
                    } else {
                        tokenizer.end();
                        tokenizer.close();
                        end = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return current != null;
        }

        @Override
        public byte[] next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            byte[] result = current;
            current = null;
            return result;
        }
    }
}
