package logic.master;

import dom.datatype.Post;
import logic.slave.PostDownloadSlave;
import pers.net.Booru;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class PostDownloadMaster implements Callable<List<Post>> {

    private Map<Booru, List<String>> map;
    private ExecutorService pool;

    public PostDownloadMaster(Map<Booru,List<String>> map){
        this.map = map;
        pool = Executors.newCachedThreadPool();
    }
    @Override
    public List<Post> call() throws ExecutionException, InterruptedException {
        System.out.println("The Post Master has been summoned");
        List<Post> posts = new ArrayList<>();
        for(Map.Entry<Booru, List<String>> e : map.entrySet()){
            for(String id : e.getValue()){
                PostDownloadSlave thread = new PostDownloadSlave(e.getKey(), id);
                Future<Post> result = pool.submit(thread);
                posts.add(result.get());
            }
        }

        pool.shutdown();

        return posts;
    }
}
