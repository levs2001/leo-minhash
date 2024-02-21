package ru.leo.data;

/**
 * @param beginInd - index of start row.
 * @param count    - count of rows.
 */
public record Chunk(int beginInd, int count) {
    public int indexOfLast() {
        return beginInd + count - 1;
    }

    /**
     * Get next chunk with same count.
     */
    public Chunk next() {
        return new Chunk(beginInd + count, count);
    }
}
