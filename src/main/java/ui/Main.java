package ui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent loader = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("MainForm.fxml")));
        Scene scene = new Scene(loader);
        stage.setTitle("Booru Manager");
        stage.setScene(scene);
        stage.show();
    }
}
