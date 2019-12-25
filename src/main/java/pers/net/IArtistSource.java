package pers.net;

import dom.datatype.Artist;

import java.io.IOException;

public interface IArtistSource extends IBooru {

    Artist artistGet(String name) throws IOException;
}
