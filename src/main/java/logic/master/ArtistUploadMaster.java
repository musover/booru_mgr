package logic.master;

import dom.datatype.Artist;
import logic.slave.ArtistUploadSlave;
import pers.net.IArtistUploadable;
import pers.stor.Configuration;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

// to be honest I don't give a shit about the state of this one
public class ArtistUploadMaster implements Runnable {

    private IArtistUploadable booru = (IArtistUploadable) Configuration.getUploadDestination();
    private Collection<Artist> artists;
    private ExecutorService pool = Executors.newCachedThreadPool();

    public ArtistUploadMaster(Collection<Artist> artists){
        this.artists = artists;
    }

    @Override
    public void run() {
        Logger.getLogger(getClass().getName()).info("The Artist Upload Master has been summoned");
        for(Artist a : artists){
            pool.submit(new ArtistUploadSlave(booru, a));
        }

        pool.shutdown();
    }
}
