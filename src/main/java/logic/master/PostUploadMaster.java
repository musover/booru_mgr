package logic.master;

import dom.datatype.Post;
import logic.slave.PostUploadSlave;
import pers.net.IUploadable;
import pers.stor.Configuration;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class PostUploadMaster implements Runnable {

    private IUploadable booru = Configuration.getUploadDestination();
    private Collection<Post> posts;
    private ExecutorService pool = Executors.newFixedThreadPool(4);

    public PostUploadMaster(Collection<Post> posts){
        this.posts = posts;
    }

    @Override
    public void run() {
        Logger.getLogger(getClass().getName()).info("The Post Upload Master has been summoned");
        for(Post p : posts){
            pool.submit(new PostUploadSlave(booru, p));
        }

        pool.shutdown();
    }
}
