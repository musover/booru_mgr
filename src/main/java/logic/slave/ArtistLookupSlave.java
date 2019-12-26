package logic.slave;

import dom.datatype.Artist;
import pers.net.IArtistSource;
import pers.stor.Configuration;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArtistLookupSlave implements Callable<Artist> {

    private IArtistSource booru = Configuration.getArtistSource();
    private String name;

    public ArtistLookupSlave(String name){
        this.name = name;
    }
    @Override
    public Artist call() throws IOException {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "ArtistSlave looking up {0}", name);
        return booru.artistGet(name);
    }
}
