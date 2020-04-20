package ui;

import dom.datatype.Post;
import pers.net.Booru;
import pers.net.Szurubooru;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestMain {

    public static void main(String[] args) throws MalformedURLException, IOException {
        Booru sb = new Szurubooru("https://sb.musover.eu");

        Post p = sb.getPost("1");

        System.out.println(p);
    }
}
