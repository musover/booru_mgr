package ui;

import dom.datatype.Post;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import pers.net.Booru;
import pers.net.Danbooru2;
import pers.net.IArtistSource;
import pers.net.IUploadable;
import pers.stor.Configuration;

import java.util.List;
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
    @FXML private ChoiceBox<String> tmVendorBox;
    @FXML private TextField tmURLField;
    @FXML private TextField tmUserField;
    @FXML private PasswordField tmPassField;

    @FXML public void initialize(){
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

        if(Configuration.getUploadDestination()!=null)
            uploadChoiceBox.setValue(Configuration.getUploadDestination().getUrl().toString());

        tmCheckbox.setSelected(Configuration.isDbEnabled());
        tmVendorBox.setItems(FXCollections.observableList(List.of("h2")));
        tmVendorBox.setValue(Configuration.getDbVendor());
        tmURLField.setText(Configuration.getDbUrl());
        tmUserField.setText(Configuration.getDbUser());
        tmPassField.setText(Configuration.getDbPass());
    }

    public void newBooru(ActionEvent actionEvent) {
        Stage o = new Stage();
        DialogPane p = new DialogPane();
        p.setHeaderText("Sorry.");
        p.setContentText("This function has not been implemented yet");
        o.setScene(new Scene(p));
        o.show();
    }
}
