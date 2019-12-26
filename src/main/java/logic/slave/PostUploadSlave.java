package logic.slave;

import dom.datatype.Post;
import logic.main.GDPv4;
import pers.net.IUploadable;

import javax.naming.NoPermissionException;
import java.io.IOException;
import java.util.logging.Logger;

public class PostUploadSlave implements Runnable {

    private IUploadable booru;
    private Post post;

    public PostUploadSlave(IUploadable b, Post p){
        booru = b;
        post = p;
    }
    @Override
    public void run() {
        Logger.getLogger(getClass().getName()).info("PostUploadSlave uploading "+ post.getId());
        try{
            booru.postCreate(post);
            GDPv4.setSuccessfulUpload(post);
            Logger.getLogger(getClass().getName()).info(post.getId()+", upload successful");
        } catch(IOException e) {
            if(e.getMessage().contains("duplicate")) {
                GDPv4.setDuplicateUpload(post);
                Logger.getLogger(getClass().getName()).info(post.getId() + ", upload duplicate");
            } else {
                GDPv4.setFailedUpload(post);
                Logger.getLogger(getClass().getName()).info(post.getId()+", upload failed due to " + e.getMessage());
            }
        } catch(NoPermissionException e){
            GDPv4.setFailedUpload(post);
            Logger.getLogger(getClass().getName()).info(post.getId()+", upload failed due to missing authentication.");
            // we should bail out here
        }


    }
}
