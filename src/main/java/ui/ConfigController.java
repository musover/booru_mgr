package ui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pers.net.Booru;
import pers.net.IArtistSource;
import pers.net.IUploadable;
import pers.stor.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigController {
    @FXML private TableColumn<Booru, String> typeColumn;
    @FXML private TableColumn<Booru, String> urlColumn;
    @FXML private TableColumn<Booru, String> usernameColumn;
    @FXML private TableColumn<Booru, String> apiKeyColumn;
    @FXML private TableView<Booru> tableView;
    @FXML private ChoiceBox<String> artistChoiceBox;
    @FXML private ChoiceBox<String> uploadChoiceBox;

    @FXML private CheckBox tmCheckbox;
    @FXML private Label datadirLabel;
    @FXML private ChoiceBox<String> tmVendorBox;
    @FXML private TextField tmURLField;
    @FXML private TextField tmUserField;
    @FXML private PasswordField tmPassField;

    @FXML public void initialize(){
        datadirLabel.setText(Configuration.getDatadir());
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        usernameColumn.setCellValueFactory(x -> {
            Booru b = x.getValue();
            return Bindings.createStringBinding(() -> {
                if(b instanceof IUploadable){
                    return ((IUploadable) b).getUsername();
                } else {
                    return "";
                }
            });
        });

        apiKeyColumn.setCellValueFactory(x -> {
            Booru b = x.getValue();
            return Bindings.createStringBinding(() -> {
                if(b instanceof IUploadable){
                    return ((IUploadable) b).getApiKey();
                } else {
                    return "";
                }
            });
        });
        tableView.setItems(FXCollections.observableList(Configuration.getBoards()));
        artistChoiceBox.getItems().add("Disable lookup");

        for(Booru b : Configuration.getBoards()){
            if(b instanceof IUploadable){
                uploadChoiceBox.getItems().add(b.getUrl().toString());
            }
            if(b instanceof IArtistSource){
                artistChoiceBox.getItems().add(b.getUrl().toString());
            }
        }


        if(Configuration.getArtistSource() != null)
            artistChoiceBox.setValue(Configuration.getArtistSource().getUrl().toString());
        else
            artistChoiceBox.setValue("Disable lookup");

        if(Configuration.getUploadDestination()!=null)
            uploadChoiceBox.setValue(Configuration.getUploadDestination().getUrl().toString());

        tmCheckbox.setSelected(Configuration.isDbEnabled());
        tmVendorBox.setItems(FXCollections.observableList(Configuration.getSupportedDbVendors()));
        tmVendorBox.setValue(Configuration.getDbVendor());
        tmURLField.setText(Configuration.getDbUrl());
        tmUserField.setText(Configuration.getDbUser());
        tmPassField.setText(Configuration.getDbPass());
    }

    public void newBooru(ActionEvent actionEvent) {
        Parent loader;
        try {
            loader = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("NewBoard.fxml")));
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "Could not show the board creation dialog.");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "FXML Loading failed.", e);
            return;
        }

        Stage s = new Stage();
        Scene sc = new Scene(loader);
        s.setTitle("New board");
        s.setScene(sc);
        s.showAndWait();
        tableView.refresh();
    }

    public void browseDataDir(ActionEvent actionEvent) {
        Stage dialog = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(Configuration.getDatadir()));
        dc.setTitle("Choose a new data directory");

        File f = dc.showDialog(dialog);

        if(f!=null) {
            Configuration.setDatadir(f.getAbsolutePath());
            datadirLabel.setText(f.getAbsolutePath());
        }
    }

    public void artistChange(ActionEvent actionEvent) {
        saveArtistSource();
    }

    public void uploadChange(ActionEvent actionEvent) {
        saveUploadDestination();
    }

    public void checked(ActionEvent actionEvent) {
        Configuration.setDbEnabled(tmCheckbox.isSelected());
    }

    public void vendorChange(ActionEvent actionEvent) {
        Configuration.setDbVendor(tmVendorBox.getValue());
    }

    public void urlChange(ActionEvent actionEvent) {
        Configuration.setDbUrl(tmURLField.getText());
    }

    public void userChange(ActionEvent actionEvent) {
        Configuration.setDbUser(tmUserField.getText());
    }

    public void passChange(ActionEvent actionEvent) {
        Configuration.setDbPass(tmPassField.getText());
    }

    public void save(){
        saveArtistSource();
        saveUploadDestination();

        Configuration.setDbEnabled(tmCheckbox.isSelected());
        Configuration.setDbVendor(tmVendorBox.getValue());
        Configuration.setDbUrl(tmURLField.getText());
        Configuration.setDbUser(tmUserField.getText());
        Configuration.setDbPass(tmPassField.getText());

        try{
            Configuration.save();
        } catch(IOException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "Failed to save the configuration. Check the logs for more details");
            a.show();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Config save failed.", e);
        }
    }

    private void saveArtistSource() {
        if (artistChoiceBox.getValue().equals("Disable lookup")) {
            Configuration.setArtistSource(null);
        } else {
            IArtistSource s;
            for (Booru b : Configuration.getBoards()) {
                if (b.getUrl().toString().equalsIgnoreCase(artistChoiceBox.getValue())) {
                    s = (IArtistSource) b;
                    Configuration.setArtistSource(s);
                    return;
                }
            }
        }
    }

    private void saveUploadDestination(){
        IUploadable u;
        for(Booru b: Configuration.getBoards()){
            if(b.getUrl().toString().equalsIgnoreCase(uploadChoiceBox.getValue())) {
                u = (IUploadable) b;
                Configuration.setUploadDestination(u);
                return;
            }
        }
    }
}
