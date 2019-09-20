package application.gui;

import application.data.Gui;
import application.data.PrefsSingleton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Options implements Initializable {
    @FXML private ToggleSwitch decompress;
    @FXML private ToggleSwitch copyRaw;
    @FXML private TextField pathBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PrefsSingleton prefs = PrefsSingleton.getInstance();
        decompress.setSelected(prefs.isCompressionEnabled());
        copyRaw.setSelected(prefs.isCopyRaw());
        pathBox.setText(prefs.getModulePath());

        decompress.selectedProperty().addListener((observable, oldValue, newValue) -> {
            prefs.setCompressionEnabled(newValue);
        });
        copyRaw.selectedProperty().addListener((observable, oldValue, newValue) -> {
            prefs.setCopyRaw(newValue);
        });
        pathBox.textProperty().addListener((observable, oldValue, newValue) -> {
            prefs.setModulePath(newValue);
        });
    }

    @FXML private void openPathSelection() {
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(Gui.getInstance().getStage());
        if(file != null) {
            pathBox.setText(file.getAbsolutePath());
        }
    }
}
