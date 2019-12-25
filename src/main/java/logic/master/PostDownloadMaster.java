package logic.master;

import dom.datatype.Post;
import logic.main.GDPv4;
import logic.slave.PostDownloadSlave;
import pers.net.Booru;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PostDownloadMaster implements Callable<List<Post>> {

    private Map<Booru, List<String>> map;
    private ExecutorService pool;

    public PostDownloadMaster(Map<Booru,List<String>> map){
        this.map = map;
        pool = Executors.newCachedThreadPool();
    }
    @Override
    public List<Post> call() throws InterruptedException {
        Logger.getLogger(getClass().getName()).info("The Post Master has been summoned");
        List<Post> posts = new ArrayList<>();
        for(Map.Entry<Booru, List<String>> e : map.entrySet()){
            for(String id : e.getValue()){
                PostDownloadSlave thread = new PostDownloadSlave(e.getKey(), id);
                Future<Post> result = pool.submit(thread);
                try {
                    Post p = result.get();
                    if (p != null)
                        posts.add(p);
                    if (p != null && !p.getArtists(false).isEmpty())
                        GDPv4.enqueueArtists(p.getArtists(false).split(" "));
                } catch(ExecutionException ex){
                    Logger.getLogger(getClass().getName()).info("Some stuff went wrong: " + ex.getMessage());
                }
            }
        }

        GDPv4.enqueueArtist("$");

        pool.shutdown();

        return posts;
    }
}
