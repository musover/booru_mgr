package dom.datatype;

import java.io.Serializable;

public class Image implements Serializable {

    private byte[] file;
    private String extension;

    public Image(){}
    public Image(byte[] file, String extension){
        this.file = file;
        this.extension = extension;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
