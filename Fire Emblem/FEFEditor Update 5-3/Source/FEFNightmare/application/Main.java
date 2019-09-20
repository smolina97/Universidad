package application;

import application.data.Gui;
import application.gui.Hub;
import application.model.TargetFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception {
        Gui.getInstance().setStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/fxml/hub.fxml"));
        Parent root = loader.load();
        Hub hub = loader.getController();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("assets/fefnightmare.png")));
        primaryStage.setTitle("FEFNightmare");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.getScene().getStylesheets().add(getClass().getResource("gui/jmetro/JMetroLightTheme.css").toExternalForm());
        primaryStage.show();

        if(getParameters().getRaw().size() > 0) {
            TargetFile.open(new File(getParameters().getRaw().get(0)));
            hub.toggleButtons();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
