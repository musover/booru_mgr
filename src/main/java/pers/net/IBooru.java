package pers.net;

import dom.datatype.Post;

import java.io.IOException;
import java.net.URL;

public interface IBooru {

    void setUrl(URL url);
    URL getUrl();
    Post getPost(String id) throws IOException;
    Post getPost(String id, boolean hideDomain) throws IOException;
}
