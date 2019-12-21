package pers.stor;

public interface TagManager {
    void insertTag(String tagName, String tagType);
    String getType(String tagName);
    void createTable();
}
