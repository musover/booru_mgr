package logic.master;

import dom.datatype.Post;
import logic.slave.PostUploadSlave;
import pers.net.IUploadable;
import pers.stor.Configuration;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class PostUploadMaster implements Runnable {

    private IUploadable booru = Configuration.getUploadDestination();
    private Collection<Post> posts;
    private ExecutorService pool = Executors.newFixedThreadPool(4);
    private CountDownLatch cdl;

    public PostUploadMaster(Collection<Post> posts, CountDownLatch cdl){
        this.posts = posts;
        this.cdl = cdl;
    }

    @Override
    public void run() {
        CountDownLatch slaveLatch = new CountDownLatch(posts.size());
        Logger.getLogger(getClass().getName()).info("The Post Upload Master has been summoned");
        for(Post p : posts){
            pool.submit(new PostUploadSlave(booru, p, slaveLatch));
        }

        pool.shutdown();
        try {
            slaveLatch.await();
        } catch (InterruptedException e) {
            Logger.getLogger(getClass().getName()).warning(e.getMessage());
            Thread.currentThread().interrupt();
        }
        cdl.countDown();
    }

}
