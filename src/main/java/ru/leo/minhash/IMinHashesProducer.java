package ru.leo.minhash;

public interface IMinHashesProducer {
    long[] getHashes(String text);

    void reset();
}
