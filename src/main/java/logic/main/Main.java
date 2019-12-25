package logic.main;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Logger.getGlobal().setLevel(Level.ALL);
        Logger.getGlobal().addHandler(new FileHandler("/tmp/logs.txt"));
        String honzitu = Files.readString(Paths.get("/tmp","honzitu"));

        GDPv4.download(honzitu);
        GDPv4.enqueueUploads(GDPv4.getDownloads());
        GDPv4.upload();
        GDPv4.shutdown();
    }
}
