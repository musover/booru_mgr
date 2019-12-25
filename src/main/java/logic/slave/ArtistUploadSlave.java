package logic.slave;

import dom.datatype.Artist;
import pers.net.IArtistUploadable;

import javax.naming.NoPermissionException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtistUploadSlave implements Runnable{

    private IArtistUploadable booru;
    private Artist artist;

    public ArtistUploadSlave(IArtistUploadable booru, Artist a){
        this.booru = booru;
        this.artist = a;
    }

    @Override
    public void run() {
        Logger.getLogger(getClass().getName()).info("ArtistUploadSlave uploading "+artist.getName());
        try{
            booru.artistCreate(artist);
        } catch(IOException| NoPermissionException e){
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Exception we don't give an F-word about: ", e);
        }
    }
}
