package logic.master;

import dom.datatype.Artist;
import logic.slave.ArtistLookupSlave;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtistLookupMaster implements Callable<List<Artist>> {
    private BlockingQueue<String> names;
    private ExecutorService pool = Executors.newCachedThreadPool();
    public ArtistLookupMaster(BlockingQueue<String> names) {
        this.names = names;
    }
    @Override
    public List<Artist> call() throws InterruptedException {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "ArtistMaster now running");

        List<Artist> artistList = new ArrayList<>();

        while(names.peek() == null || !names.peek().equals("$")){
            String n = names.poll(30, TimeUnit.SECONDS);
            if(n == null)
                break;
            try {
                Future<Artist> a = pool.submit(new ArtistLookupSlave(n));
                artistList.add(a.get());
            } catch(ExecutionException e){
                Exception ex = (Exception) e.getCause();
                if(ex instanceof IndexOutOfBoundsException || ex instanceof NullPointerException)
                    Logger.getLogger(getClass().getName()).info("Some stuff went wrong: " + ex.getMessage());
                else
                    Logger.getLogger(getClass().getName()).warning("Some stuff went wronger: " + ex.getMessage());

            }
        }

        pool.shutdown();
        return artistList;
    }
}
