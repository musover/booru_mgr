package ui;

import dom.datatype.Post;
import dom.datatype.Rating;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pers.stor.Configuration;
import pers.stor.datatype.PostStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Post> observableValue, Post post, Post t1) {
                try {
                    updatePostView(t1);
                } catch (IOException e) {
                    Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
                }
            }
        });
    }

    public void exportFilePicker(ActionEvent actionEvent) throws IOException {
        Stage dialog = new Stage();
        Post selected = list.getSelectionModel().getSelectedItem();
        if(selected == null)
            return;
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Configuration.getWorkdir()));
        fc.setInitialFileName(selected.getImage().getPseudofilename());
        File save = fc.showSaveDialog(dialog);
        fc.setTitle("Select destination to export to");

        PostStorage ps = PostStorage.getInstance();
        ps.exportFile(selected, save.toPath());
    }

    public ObservableList<Rating> getRatings(){
        return FXCollections.observableArrayList(Rating.values());
    }

    public void loadSomeShit(ActionEvent actionEvent) throws IOException {
        PostStorage ps = PostStorage.getInstance();
        Stage dialog = new Stage();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Configuration.getDatadir()));
        fc.setTitle("Choose one or more files to open");
        List<File> files = fc.showOpenMultipleDialog(dialog);

        List<Post> posts = new ArrayList<>();
        for(File f : files){
            posts.add(ps.load(f.toPath()));
        }

        list.setItems(FXCollections.observableList(posts));
    }

    public void updatePostView(Post p) throws IOException {
            if(p == null)
                return;

            copyrightTextField.setText(p.getCopyright(false));
            characterTextField.setText(p.getCharacters(false));
            artistTextField.setText(p.getArtists(false));
            tagTextField.setText(p.getGeneral());
            metaTextField.setText(p.getMeta(false));
            Image i = new Image(p.getImage().getImageInputStream());
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

    public void configForm(ActionEvent actionEvent) throws IOException {
        Parent loader = FXMLLoader.load(Objects.requireNonNull(this.getClass().getClassLoader().getResource("ConfigForm.fxml")));
        Stage newStage = new Stage();
        Scene s = new Scene(loader);
        newStage.setTitle("Preferences");
        newStage.setScene(s);
        newStage.show();
    }
}
