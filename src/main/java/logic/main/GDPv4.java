package logic.main;

import dom.datatype.Post;
import logic.master.PostDownloadMaster;
import pers.net.Booru;
import pers.net.Danbooru2;
import pers.net.Gelbooru;
import pers.stor.Configuration;
import pers.stor.datatype.PostStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class GDPv4 {

    private static ConcurrentHashMap<Booru, List<String>> downloadQueue = new ConcurrentHashMap<>();
    private static ConcurrentLinkedQueue<String> urlQueue = new ConcurrentLinkedQueue<>();
    private static List<Post> postList = new ArrayList<>();
    private static ExecutorService threadpool = Executors.newCachedThreadPool();

    private GDPv4(){

    }

    public static void setUrls(String urls){
        urlQueue.addAll(Arrays.asList(urls.split("\\r?\\n+")));
    }

    public static void parseUrls(){
        for(Booru b : Configuration.getBoards()){
            for(String url : urlQueue) {
                String postId = "";
                String searchtoken = "";
                Pattern search = null;
                String filtertoken = "";

                if (b instanceof Danbooru2) {
                    searchtoken = "(?:/\\d+)";
                    filtertoken = "/";
                } else if (b instanceof Gelbooru) {
                    searchtoken = "(?:id=\\d+)";
                    filtertoken = "id=";
                }

                if (url.toLowerCase().contains(b.getUrl().getHost().toLowerCase())) {
                    String[] matches = Pattern.compile(searchtoken)
                            .matcher(url)
                            .results()
                            .map(MatchResult::group)
                            .toArray(String[]::new);

                    postId = matches[0].replace(filtertoken, "");

                    downloadQueue.putIfAbsent(b, new ArrayList<>());
                    downloadQueue.get(b).add(postId);
                    urlQueue.remove(url);
                }
            }
        }

        System.out.println(downloadQueue);
    }

    public static void download(String urls) throws ExecutionException, InterruptedException {
        setUrls(urls);
        parseUrls();

        PostDownloadMaster dm = new PostDownloadMaster(downloadQueue);
        Future<List<Post>> result = threadpool.submit(dm);

        postList = result.get();
    }

    public static void shutdown(){
        threadpool.shutdown();
    }

    //temporary method
    public static void saveAll() throws IOException {
        PostStorage ps = PostStorage.getInstance();
        ps.saveAll(postList);
    }
}
