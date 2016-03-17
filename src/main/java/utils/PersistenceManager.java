package utils;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

/**
 * Created by felipe on 02/03/16.
 */
public interface PersistenceManager {

    void save(File output);
    void load(File input) throws IOException, ParseException;
}
