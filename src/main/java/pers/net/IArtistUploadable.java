package pers.net;

import dom.datatype.Artist;

import javax.naming.NoPermissionException;
import java.io.IOException;

public interface IArtistUploadable extends IUploadable {
    void artistCreate(Artist a) throws IOException, NoPermissionException;
}
