package fs;
	
import fs.data.MainData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			MainData.getInstance().setStage(primaryStage);
			Parent root = FXMLLoader.load(this.getClass().getResource("gui/layout/FatesScript.fxml"));
			primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("editorIcon.png")));
			primaryStage.setTitle("Fates Script");
			primaryStage.setScene(new Scene(root, 600, 600));
			primaryStage.getScene().getStylesheets().add(this.getClass().getResource("gui/jmetro/JMetroLightTheme.css")
					.toExternalForm());
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}