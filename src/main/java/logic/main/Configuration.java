package logic.main;

import java.io.IOException;
import java.io.Serializable;

public class Configuration {

    public static String DB_VENDOR = "h2";
    public static String DB_URL = "jdbc:h2:mem:gdpv4";
    public static String WORKDIR = System.getProperty("user.dir");
    private Configuration(){}
    static {
        // TODO: shit that reads from a YAML/whatever and puts it into the args
    }

    public static void save() throws IOException {
    }

    public static void load() throws IOException {
    }
}
