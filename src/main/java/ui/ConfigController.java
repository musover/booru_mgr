package ui;

import dom.datatype.Post;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pers.net.Booru;
import pers.stor.Configuration;

import java.util.List;
import java.util.logging.Logger;

public class ConfigController {
    @FXML private TableColumn<String, Booru> typeColumn;
    @FXML private TableColumn<String, Booru> urlColumn;
    @FXML private TableColumn<String, Booru> usernameColumn;
    @FXML private TableColumn<String, Booru> apiKeyColumn;
    @FXML private TableView<Booru> tableView;

    @FXML private CheckBox tmCheckbox;
    @FXML private ChoiceBox<String> tmVendorBox;
    @FXML private TextField tmURLField;
    @FXML private TextField tmUserField;
    @FXML private PasswordField tmPassField;

    @FXML public void initialize(){
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        apiKeyColumn.setCellValueFactory(new PropertyValueFactory<>("apiKey"));
        tableView.setItems(FXCollections.observableList(Configuration.getBoards()));

        tmCheckbox.setSelected(Configuration.isDbEnabled());
        tmVendorBox.setItems(FXCollections.observableList(List.of("h2")));
        tmVendorBox.setValue(Configuration.getDbVendor());
        tmURLField.setText(Configuration.getDbUrl());
        tmUserField.setText(Configuration.getDbUser());
        tmPassField.setText(Configuration.getDbPass());
    }

}
