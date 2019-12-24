package logic.main;

import java.io.IOException;

public class Configuration {

    private static String dbVendor = "h2";
    private static String dbUrl = "jdbc:h2:mem:gdpv4";

    public static String getDbVendor() {
        return dbVendor;
    }

    public static void setDbVendor(String dbVendor) {
        Configuration.dbVendor = dbVendor;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static void setDbUrl(String dbUrl) {
        Configuration.dbUrl = dbUrl;
    }

    public static String getWorkdir() {
        return workdir;
    }

    public static void setWorkdir(String workdir) {
        Configuration.workdir = workdir;
    }

    private static String workdir = System.getProperty("user.dir");
    private Configuration(){}
    static {
        // TODO: shit that reads from a YAML/whatever and puts it into the args
    }

    public static void save() throws IOException {
        throw new UnsupportedOperationException(); //NYI
    }

    public static void load() throws IOException {
        throw new UnsupportedOperationException(); // NYI
    }
}
