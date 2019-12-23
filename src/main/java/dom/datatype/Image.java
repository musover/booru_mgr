package dom.datatype;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Image implements Serializable {

    private transient Path path;
    private UUID id = UUID.randomUUID();
    private String extension;

    public Image(){
        // bean constructor
    }

    public byte[] getFile() throws IOException{
        return Files.readAllBytes(path);
    }

    public void setFile(byte[] file) throws IOException {
        path = Files.createTempFile(id.toString(),".tmp");
        path.toFile().deleteOnExit();
        Files.write(path, file);
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
