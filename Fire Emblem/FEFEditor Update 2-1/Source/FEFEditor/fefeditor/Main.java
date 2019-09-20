package fefeditor;

import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application 
{
	@Override
	public void start(Stage primaryStage) 
	{
		GuiData.getInstance().setStage(primaryStage);
		FileData.getInstance();
		
		try 
		{
			Parent root = FXMLLoader.load(this.getClass().getResource("/gui/fxml/typeSelection.fxml"));
			Scene scene = new Scene(root,300,350);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/assets/main.png")));
			scene.getStylesheets().add(this.getClass().getResource("/gui/css/general.css").toExternalForm());
			primaryStage.setTitle("FEFEditor");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}