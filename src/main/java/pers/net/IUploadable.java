package pers.net;

import dom.datatype.Post;

import javax.naming.NoPermissionException;
import java.io.IOException;

public interface IUploadable extends IBooru {
    void postCreate(Post p) throws IOException, NoPermissionException;
    String getUsername();
    void setUsername(String username);
    String getApiKey();
    void setApiKey(String apiKey);
    void updateBasicAuth();
}
