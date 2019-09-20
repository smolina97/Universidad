package application.data;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gui
{
    private static Gui instance;
    private Stage stage;
    private StackPane stackPane;

    private Gui() {}

    public static Gui getInstance() {
        if(instance == null)
            instance = new Gui();
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }
}
