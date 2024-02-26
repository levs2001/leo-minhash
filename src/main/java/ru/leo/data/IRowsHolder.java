package ru.leo.data;

import java.util.List;

public interface IRowsHolder extends IChunkHolder {
    Row getRow(long id);
    List<Row> getRows(List<Long> ids);
}
