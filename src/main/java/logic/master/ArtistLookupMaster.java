package logic.master;

import dom.datatype.Artist;
import logic.slave.ArtistLookupSlave;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
    public List<Artist> call() throws ExecutionException, InterruptedException {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "ArtistMaster now running");

        List<Artist> artistList = new ArrayList<>();

        while(names.peek() == null || !names.peek().equals("$")){
            String n = names.poll(30, TimeUnit.SECONDS);
            Future<Artist> a = pool.submit(new ArtistLookupSlave(n));
            artistList.add(a.get());
        }

        pool.shutdown();
        return artistList;
    }
}
