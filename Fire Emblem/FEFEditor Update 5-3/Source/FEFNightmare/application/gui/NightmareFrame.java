package application.gui;

import application.model.Module;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

public abstract class NightmareFrame {

    public NightmareFrame(Module module, ComboBox<String> entryBox) {}

    abstract AnchorPane getAnchorPane();
}
