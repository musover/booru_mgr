package logic.slave;

import dom.datatype.Post;
import pers.net.Booru;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDownloadSlave implements Callable<Post> {
    private Booru board;
    private String postId;

    public PostDownloadSlave(Booru board, String postId){
        this.board = board;
        this.postId = postId;
    }
    @Override
    public Post call() throws IOException {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "ArtistSlave looking up "+postId);
        return board.getPost(postId, false);
    }
}
