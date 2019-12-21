package pers.stor;

import java.sql.SQLException;

public interface TagManager {
    void insertTag(String tagName, String tagType) throws SQLException;
    String getType(String tagName) throws SQLException;
    void createTable() throws SQLException;
}
