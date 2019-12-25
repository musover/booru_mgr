package logic.slave;

import dom.datatype.Post;
import pers.net.Booru;

import java.io.IOException;
import java.util.concurrent.Callable;

public class PostDownloadSlave implements Callable<Post> {
    private Booru board;
    private String postId;

    public PostDownloadSlave(Booru board, String postId){
        this.board = board;
        this.postId = postId;
    }
    @Override
    public Post call() throws IOException {
        System.out.println("A slave is slaving away at "+postId);
        return board.getPost(postId, false);
    }
}
