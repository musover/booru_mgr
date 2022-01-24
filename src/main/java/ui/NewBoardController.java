package ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import pers.net.Booru;
import pers.net.IUploadable;
import pers.stor.Configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NewBoardController {
    @FXML private ChoiceBox<String> classChoiceBox;
    @FXML private TextField urlField;
    @FXML private TextField userField;
    @FXML private PasswordField passField;

    @FXML
    public void initialize(){
        Reflections r = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forPackage("pers.net"))
                /* and maybe */
                .filterInputsBy(new FilterBuilder().includePackage("pers.net")));
        Set<Class<? extends Booru>> classes = r.getSubTypesOf(Booru.class);

        List<String> classNames = classes.stream().map(Class::getCanonicalName).collect(Collectors.toList());

        classChoiceBox.setItems(FXCollections.observableList(classNames));
    }

    public void createBoard(ActionEvent actionEvent) {
        Class<?> booruClass;
        Booru b;
        try {
            booruClass = Class.forName(classChoiceBox.getValue());
            Constructor<?> constructor = booruClass.getConstructor();
            b = (Booru) constructor.newInstance();
        } catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e){
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "How in the heck.", e);
            return;
        }

        try{
            b.setUrl(new URL(urlField.getText()));
        } catch(MalformedURLException e){
            Alert a = new Alert(Alert.AlertType.ERROR, "The provided URL is not valid.");
            a.show();
        }

        if(b instanceof IUploadable && (!userField.getText().isEmpty() && !passField.getText().isEmpty())){
            ((IUploadable) b).setUsername(userField.getText());
            ((IUploadable) b).setApiKey(passField.getText());
        }

        Configuration.getBoards().add(b);


    }

    public void disableSomeFields(ActionEvent actionEvent) {
        Class<?> booruClass;
        try {
            booruClass = Class.forName(classChoiceBox.getValue());
            boolean isIUploadable = isIUploadable(booruClass);
            userField.setDisable(!isIUploadable);
            passField.setDisable(!isIUploadable);
        } catch(ClassNotFoundException e){
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "How in the heck.", e);
        }
    }

    private boolean isIUploadable(Class<?> c){
        for(Class<?> i : c.getInterfaces()){
            if(i.getCanonicalName().contains("IUploadable"))
                return true;
        }

        return false;
    }
}
