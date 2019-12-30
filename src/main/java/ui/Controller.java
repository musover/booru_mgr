package ui;

import dom.datatype.Post;
import dom.datatype.Rating;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.main.GDPv4;
import logic.main.UploadState;
import pers.stor.Configuration;
import pers.stor.datatype.PostStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    @FXML private GridPane tagFormGridPane;
    @FXML private AnchorPane rightAnchorPane;
    @FXML private ImageView imageView;
    @FXML private TextField copyrightTextField;
    @FXML private TextField characterTextField;
    @FXML private TextField artistTextField;
    @FXML private TextField tagTextField;
    @FXML private TextField metaTextField;
    @FXML private ListView<Post> list;
    @FXML private Spinner<Integer> parentSpinner;
    @FXML private ChoiceBox<Rating> ratingChoiceBox;

    @FXML
    public void initialize(){
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.getSelectionModel().selectedItemProperty().addListener((observableValue, post, t1) -> updatePostView(t1));
    }

    public void exportFilePicker(ActionEvent actionEvent) {
        Stage dialog = new Stage();
        Post selected = list.getSelectionModel().getSelectedItem();
        if(selected == null)
            return;
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Configuration.getWorkdir()));
        fc.setInitialFileName(selected.getImage().getPseudofilename());
        File save = fc.showSaveDialog(dialog);
        fc.setTitle("Select destination to export to");

        if(save == null)
            return;

        PostStorage ps = PostStorage.getInstance();
        try {
            ps.exportFile(selected, save.toPath());
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "The export has failed. Check the logs for more details");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Export failed.", e);
        }
    }

    public ObservableList<Rating> getRatings(){
        return FXCollections.observableArrayList(Rating.values());
    }

    public void loadSomeShit(ActionEvent actionEvent) {
        PostStorage ps = PostStorage.getInstance();
        Stage dialog = new Stage();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Configuration.getDatadir()));
        fc.setTitle("Choose one or more files to open");
        List<File> files = fc.showOpenMultipleDialog(dialog);
        if(files == null)
            return;

        List<Post> posts = new ArrayList<>();
        for(File f : files){
            try {
                posts.add(ps.load(f.toPath()));
            } catch(IOException e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Post loading failed.", e);
            }
        }

        list.setItems(FXCollections.observableList(posts));
    }

    public void updatePostView(Post p) {
            if(p == null)
                return;

            copyrightTextField.setText(p.getCopyright(false));
            characterTextField.setText(p.getCharacters(false));
            artistTextField.setText(p.getArtists(false));
            tagTextField.setText(p.getGeneral());
            metaTextField.setText(p.getMeta(false));
            Image i;
            try{
                i = new Image(p.getImage().getImageInputStream());
            } catch(IOException e){
                Alert a = new Alert(Alert.AlertType.ERROR, "Could not show the selected image.");
                a.show();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Image Loading failed.", e);
                return;
            }
            imageView.setImage(i);
            ratingChoiceBox.setValue(p.getRating());

            centerImage();

        }

    public void centerImage() {
        Image img = imageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            reducCoeff = Math.min(ratioX, ratioY);

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);

        }
    }

    public void configForm(ActionEvent actionEvent) {
        Parent loader;
        try {
            loader = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("ConfigForm.fxml")));
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "Could not show the configuration dialog.");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "FXML Loading failed.", e);
            return;
        }
        Stage newStage = new Stage();
        Scene s = new Scene(loader);
        newStage.setTitle("Preferences");
        newStage.setScene(s);
        newStage.show();
    }

    public void pasteDialog(ActionEvent actionEvent) {
        Parent loader;
        try {
            loader = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("DownloadBox.fxml")));
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "Could not show the download dialog.");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "FXML Loading failed.", e);
            return;
        }

        Stage newStage = new Stage();
        Scene s = new Scene(loader);
        newStage.setTitle("Download");
        newStage.setScene(s);
        newStage.showAndWait();

        list.getItems().addAll(GDPv4.getDownloads());
    }

    public void defaultSaveSel(ActionEvent actionEvent) {
        PostStorage ps = PostStorage.getInstance();
        try {
            ps.saveAll(list.getSelectionModel().getSelectedItems());
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "Saving has failed. Check the logs for more details");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Save failed.", e);
        }
    }

    public void saveSelAs(ActionEvent actionEvent) {
        PostStorage ps = PostStorage.getInstance();
        Stage dialog = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(Configuration.getDatadir()));
        dc.setTitle("Choose a directory to save to");

        File f = dc.showDialog(dialog);

        try {
            ps.saveAll(list.getSelectionModel().getSelectedItems(), f.toPath());
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "Saving has failed. Check the logs for more details");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Save failed.", e);
        }
    }

    public void defaultExportSel(ActionEvent actionEvent)  {
        PostStorage ps = PostStorage.getInstance();
        try {
            ps.exportAll(list.getSelectionModel().getSelectedItems());
        } catch(IOException e){
                Alert a = new Alert(Alert.AlertType.ERROR, "The export has failed. Check the logs for more details");
                a.show();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Export failed.", e);
            }
    }

    public void exportSelAs(ActionEvent actionEvent){
        PostStorage ps = PostStorage.getInstance();
        Stage dialog = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(Configuration.getDatadir()));
        dc.setTitle("Choose a directory to export to");

        File f = dc.showDialog(dialog);

        try {
            ps.exportAll(list.getSelectionModel().getSelectedItems(), f.toPath());
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "The export has failed. Check the logs for more details");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Export failed.", e);
        }
    }

    public void uploadSel(ActionEvent actionEvent) {
        if(Configuration.getUploadDestination() == null){
            Alert a = new Alert(Alert.AlertType.ERROR, "No Upload destination configured. Cannot continue.");
            a.show();
            return;
        }
        List<Post> p = list.getSelectionModel().getSelectedItems();
        GDPv4.enqueueUploads(p);
        CountDownLatch cdl = new CountDownLatch(1);
        GDPv4.upload(cdl);

        try{
            cdl.await();
        } catch(InterruptedException e){
            Logger.getLogger(getClass().getName()).warning(e.getMessage());
            Thread.currentThread().interrupt();
        }
        int successCounter = 0;
        int duplicateCounter = 0;
        int errorCounter = 0;

        for(Map.Entry<Post, UploadState> e : GDPv4.getUploads().entrySet()){
            switch(e.getValue()){
                case SUCCESSFUL:
                    successCounter++;
                    break;
                case DUPLICATE:
                    duplicateCounter++;
                    break;
                case FAILED:
                    errorCounter++;
                    break;
            }
        }

        Alert.AlertType e = Alert.AlertType.INFORMATION;
        if(duplicateCounter>0)
            e = Alert.AlertType.WARNING;
        if(errorCounter>0)
            e = Alert.AlertType.ERROR;

        String content = successCounter + " uploads completed.\n" +
                duplicateCounter + " uploads were duplicates.\n" +
                errorCounter + " uploads failed.";
        Alert a = new Alert(e, content);
        a.show();

    }

    public void quit(ActionEvent actionEvent) {
        GDPv4.shutdown();
        Platform.exit();
    }

    public void saveFilePicker(ActionEvent actionEvent) {
        Stage dialog = new Stage();
        Post selected = list.getSelectionModel().getSelectedItem();
        if(selected == null)
            return;
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Configuration.getWorkdir()));
        fc.setInitialFileName(selected.getImage().getPseudofilename()+".json");
        File save = fc.showSaveDialog(dialog);
        fc.setTitle("Select destination to save to");

        if(save == null)
            return;
        PostStorage ps = PostStorage.getInstance();
        try {
            ps.saveFile(selected, save.toPath());
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "The save has failed. Check the logs for more details");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Save failed.", e);
        }
    }

    public void okButtonClicked(ActionEvent actionEvent) {
        updateSelectedPost();
    }

    public void deletThis(ActionEvent actionEvent) {
        list.getItems().remove(list.getSelectionModel().getSelectedItem());
    }
    
    public void updateSelectedPost(){
        Post p = list.getSelectionModel().getSelectedItem();
        
        p.setCopyright(copyrightTextField.getText());
        p.setCharacters(characterTextField.getText());
        p.setArtists(artistTextField.getText());
        p.setMeta(metaTextField.getText());
        p.setGeneral(tagTextField.getText());
        
        p.setRating(ratingChoiceBox.getValue());
        p.setParent((parentSpinner.getValue() == 0) ? "" : parentSpinner.getValue().toString());
    }

    public void textFieldUpdated(ActionEvent actionEvent) {
        updateSelectedPost();
    }
}
