package dom.datatype;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post implements Serializable {

    private String id;
    private Image image;
    private Map<String, List<String>> tags;
    private String source;
    private Rating rating;
    private String parent = "";

    public Post(){
        tags = new HashMap<>();
    }

    public Post(String id, Image image, Rating rating) {
        this.id = id;
        this.image = image;
        this.rating = rating;
    }

    private void setTags(String key, String value){
        tags.put(key, Arrays.asList(value.split(" ")));
    }

    private List<String> getTagList(String key){
        return tags.get(key);
    }

    private String getTagstring(String key, boolean raw) {
        StringBuilder tagstr = new StringBuilder((raw) ? " " : "");
        boolean isGeneral = key.equalsIgnoreCase(TagType.GENERAL);
        List<String> taglist = getTagList(key);
        if(taglist == null)
            return "";
        for (String s : taglist) {
            tagstr.append((raw && !isGeneral) ? key + ":" : "").append(s).append(" ");
        }

        return tagstr.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Map<String, List<String>> getTags() {
        return tags;
    }

    public void setTags(Map<String, List<String>> tags) {
        this.tags = tags;
    }

    public String getTagstring(){
        StringBuilder tagstring = new StringBuilder();
        for(String k : tags.keySet()){
            tagstring.append(getTagstring(k, true));
        }

        return tagstring.toString();
    }

    public String getArtists(){
        return getArtists(true);
    }

    public String getArtists(boolean raw){
        return getTagstring(TagType.ARTIST, raw);
    }

    public void setArtists(String tag){
        setTags(TagType.ARTIST,tag);
    }

    public List<String> getArtistList(){
        return tags.get(TagType.ARTIST);
    }

    public String getCopyright(){
        return getCopyright(true);
    }

    public String getCopyright(boolean raw){
        return getTagstring(TagType.COPYRIGHT, raw);
    }

    public void setCopyright(String tag){
        setTags(TagType.COPYRIGHT, tag);
    }

    public String getCharacters(){
        return getCharacters(true);
    }

    public String getCharacters(boolean raw){
        return getTagstring(TagType.CHARACTER, raw);
    }

    public void setCharacters(String tag){
        setTags(TagType.CHARACTER, tag);
    }

    public String getMeta(){
        return getMeta(true);
    }

    public String getMeta(boolean raw){
        return getTagstring(TagType.META, raw);
    }

    public void setMeta(String tag){
        setTags(TagType.META, tag);
    }

    public String getGeneral(){
        return getTagstring(TagType.GENERAL, false);
    }

    public void setGeneral(String tag){
        setTags(TagType.GENERAL, tag);
    }

    public Rating getRating(){
        return rating;
    }

    public void setRating(Rating r){
        rating = r;
    }

    public String getParent(){
        return parent;
    }

    public void setParent(String p){
        this.parent = p;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String s){
        this.source = s;
    }

    @Override
    public String toString() {
        return id;
    }
}
