package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import logic.main.GDPv4;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadController {
    @FXML private TextArea textArea;
    @FXML private ProgressIndicator progress;


    public void downloadURLs(ActionEvent actionEvent) {
        progress.setProgress(-1.0);
        download();
        progress.setProgress(1);
    }

    public void download(){
        String urls = textArea.getText();
        try {
            GDPv4.download(urls);
        } catch (ExecutionException e) {
            progress.setProgress(0.0);
            Alert a = new Alert(Alert.AlertType.ERROR, "Download failed. Check the logs for more details.");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Download failed failed.", e.getCause());
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}
