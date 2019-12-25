package pers.stor.datatype;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface DataStorage<T> {
    /**
     * Saves an object to the default directory
     * @param t Object to save
     * @throws IOException .
     */
    void save(T t) throws IOException;

    /**
     * Saves an object to the directory specified in path
     * @param t Object to save
     * @param path Must be a directory.
     * @throws IOException .
     */
    void save(T t, Path path) throws IOException;

    /**
     * Saves a list of objects to the default directory.
     * @param t Objects to save
     * @throws IOException .
     */
    void saveAll(List<T> t) throws IOException;

    /**
     * Saves a list of objects to the directory specified in path
     * @param t Objects to save
     * @param path Must be a directory.
     * @throws IOException .
     */
    void saveAll(List<T> t, Path path) throws IOException;

    /**
     * Loads an object from the specified path
     * @param p path
     * @return A properly constructed object (I hope)
     * @throws IOException .
     */
    T load(Path p) throws IOException;

    /**
     * Loads a list of objects from the directory specified in p
     * @param p Must be a directory.
     * @return A list of properly constructed objects (I hope)
     * @throws IOException .
     */
    List<T> loadAll(Path p) throws IOException;
}