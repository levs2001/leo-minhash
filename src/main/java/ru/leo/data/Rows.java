package ru.leo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * For simple mapping with jackson.
 */
public record Rows(List<Row> rows, Map<Long, Row> rowMap) implements IRowsHolder {
    public Rows() {
        this(new ArrayList<>(), new HashMap<>());
    }

    public void addRow(Row row) {
        rowMap.put(row.id(), row);
        rows.add(row);
    }

    @Override
    public Row getRow(long id) {
        return rowMap.get(id);
    }

    @Override
    public List<Row> getRows(List<Long> ids) {
        return ids.stream().map(rowMap::get).filter(Objects::nonNull).toList();
    }

    @Override
    public List<Row> getRows(Chunk chunk) {
        if (chunk.beginInd() >= rows.size()) {
            return null;
        }

        List<Row> result = new ArrayList<>(chunk.count());
        for (int i = chunk.beginInd(); i < Math.min(chunk.indexOfLast() + 1, rows.size()); i++) {
            result.add(rows.get(i));
        }

        return result;
    }
}
