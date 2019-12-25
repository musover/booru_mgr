package dom.datatype;

import java.io.Serializable;
import java.util.List;

public class Artist implements Serializable {

    private String name;
    private List<String> urls;
    private String otherNames = "";
    private String groupName = "";

    public Artist(){}

    public Artist(String name, List<String> urls){
        this.name = name;
        this.urls = urls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUrls() {
        return urls;
    }

    public String getUrlString(){
        if(urls == null)
            return "";

        StringBuilder sb = new StringBuilder();
        for(String u : urls){
            sb.append(u).append(" ");
        }

        return sb.toString();
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
