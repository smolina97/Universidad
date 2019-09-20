package fefeditor.gui.controllers;

import com.jfoenix.controls.JFXListView;
import fefeditor.Main;
import fefeditor.common.FileDialogs;
import fefeditor.common.ModuleUpdater;
import fefeditor.common.feflib.inject.InjectableBin;
import fefeditor.common.io.IOUtils;
import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Selection implements Initializable
{
	@FXML private JFXListView<String> typeList;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		typeList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2)
                openFile();
        });
		
		for(String s : GuiData.getInstance().getFileTypes())
			typeList.getItems().add(s);
	}

	@FXML private void close()
	{
		Platform.exit();
	}

	@FXML private void openAbout()
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("About FEFEditor");
		alert.setGraphic(new ImageView(Main.class.getResource("/assets/main.png").toString()));
		alert.setContentText("A general purpose editor for Fire Emblem Fates by thane98. Please consult the main thread " +
				"or readme if you need help with using any of the provided tools. Special thanks to SecretiveCactus, RainThunder, DeathChaos25, " +
				"SciresM, and Hextator for their works which helped make this editor a reality.");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/main.png")));
		alert.showAndWait();
	}
	
	private void openFile()
	{
		switch(typeList.getSelectionModel().getSelectedItem())
		{
			case "FEFNightmare":
				openFEFNightmare();
				break;
			case "Dispo Editor":
				openDispo();
				break;
			case "Person Editor":
				openPerson();
				break;
			case "Castle Join Editor":
				openJoin();
				break;
			case "Fates Script":
				openScript();
				break;
			case "Map Config Editor":
				openConfig();
				break;
			case "Dialogue Editor":
				openDialogue();
				break;
			case "Terrain Editor":
				openTerrain();
				break;
			case "Support Editor":
				openSupport();
				break;
			case "Dialogue Editor (DLC)":
				openDialogueDLC();
				break;
			case "Support Editor (DLC)":
				openSupportDLC();
				break;
			default:
				break;
		}
	}
	
	private void openDispo()
	{
		if(FileDialogs.openBin(GuiData.getInstance().getStage()))
		{
			try
			{
				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				Parent root = FXMLLoader.load(Main.class.getResource("/gui/fxml/dispo.fxml"));
				Scene scene = new Scene(root, 1230, 700);
				scene.getStylesheets().add(Main.class.getResource("/gui/css/dispo.css").toExternalForm());
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/dispo.png")));
				scene.getStylesheets().add(this.getClass().getResource("/gui/css/general.css").toExternalForm());
				stage.setScene(scene);
				stage.setTitle("Dispo Editor");
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(GuiData.getInstance().getStage());
				stage.show();

				FileData.getInstance().getWorkingFile().deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void openPerson()
	{
		if(FileDialogs.openBin(GuiData.getInstance().getStage()))
		{
			try 
			{
				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				Parent root = FXMLLoader.load(Main.class.getResource("/gui/fxml/person.fxml"));
				Scene scene = new Scene(root, 500, 475);
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/person.png")));
				stage.setScene(scene);
				stage.setTitle("Person Editor");
				stage.initModality(Modality.WINDOW_MODAL);
	            stage.initOwner(GuiData.getInstance().getStage());
	            stage.show();
	            
	            FileData.getInstance().getWorkingFile().deleteOnExit();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}	
		}
	}
	
	private void openJoin()
	{
		if(FileDialogs.openBin(GuiData.getInstance().getStage()))
		{
			try
			{
				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				Parent root = FXMLLoader.load(Main.class.getResource("/gui/fxml/join.fxml"));
				Scene scene = new Scene(root, 415, 325);
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/join.png")));
				stage.setScene(scene);
				stage.setTitle("Castle Join Editor");
				stage.initModality(Modality.WINDOW_MODAL);
	            stage.initOwner(GuiData.getInstance().getStage());
	            stage.show();
	            
	            FileData.getInstance().getWorkingFile().deleteOnExit();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}	
		}
	}

	private void openConfig()
	{
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(GuiData.getInstance().getStage());
		if(file != null)
		{
			try
			{
				File workingFile = File.createTempFile("FEFWORKING", null, FileData.getInstance().getTemp());
				FileData.getInstance().setWorkingFile(workingFile);
				byte[] out = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
				Files.write(Paths.get(workingFile.getCanonicalPath()), out);

				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				Parent root = FXMLLoader.load(Main.class.getResource("/gui/fxml/config.fxml"));
				Scene scene = new Scene(root, 275, 240);
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/rom3.png")));
				stage.setScene(scene);
				stage.setTitle("Map Config Editor");
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(GuiData.getInstance().getStage());
				stage.show();

				FileData.getInstance().getWorkingFile().deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void openDialogue()
	{
		DirectoryChooser chooser = new DirectoryChooser();
		File file = chooser.showDialog(GuiData.getInstance().getWorkingStage());
		if(file != null)
		{
			try
			{
				File workingFile = Files.createTempDirectory(Paths.get(FileData.getInstance().getTemp().getAbsolutePath()), "Text").toFile();
				IOUtils.copyFolder(file, workingFile);
				FileData.getInstance().setWorkingFile(workingFile);

				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				FXMLLoader loader = new FXMLLoader(Main.class.getResource("/gui/fxml/dialogue.fxml"));
				Parent root = loader.load();
				Dialogue controller = loader.getController();
				Scene scene = new Scene(root, 575, 325);
				controller.addAccelerators();
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/join.png")));
				stage.setScene(scene);
				stage.setTitle("Dialogue Editor");
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(GuiData.getInstance().getStage());
				stage.show();

				workingFile.deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void openDialogueDLC()
	{
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(GuiData.getInstance().getStage());
		if(file != null)
		{
			try
			{
				File workingFile = File.createTempFile("FEFWORKING", null, FileData.getInstance().getTemp());
				FileData.getInstance().setWorkingFile(workingFile);
				byte[] out = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
				Files.write(Paths.get(workingFile.getCanonicalPath()), out);

				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				FXMLLoader loader = new FXMLLoader(Main.class.getResource("/gui/fxml/dialogueDLC.fxml"));
				Parent root = loader.load();
				DialogueDLC controller = loader.getController();
				Scene scene = new Scene(root, 575, 325);
				controller.addAccelerators();
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/join.png")));
				stage.setScene(scene);
				stage.setTitle("Dialogue Editor");
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(GuiData.getInstance().getStage());
				stage.show();

				FileData.getInstance().getWorkingFile().deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void openTerrain()
	{
		if(FileDialogs.openBin(GuiData.getInstance().getStage()))
		{
			try
			{
				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				Parent root = FXMLLoader.load(Main.class.getResource("/gui/fxml/terrain.fxml"));
				Scene scene = new Scene(root, 1230, 700);
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/rom3.png")));
				scene.getStylesheets().add(Main.class.getResource("/gui/css/terrain.css").toExternalForm());
				scene.getStylesheets().add(this.getClass().getResource("/gui/css/general.css").toExternalForm());
				stage.setScene(scene);
				stage.setTitle("Terrain Editor");
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(GuiData.getInstance().getStage());
				stage.show();

				FileData.getInstance().getWorkingFile().deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void openSupport()
	{
		if(FileDialogs.openBin(GuiData.getInstance().getStage()))
		{
			try
			{
				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				FXMLLoader loader = new FXMLLoader(Main.class.getResource("/gui/fxml/support.fxml"));
				Parent root = loader.load();
				Support controller = loader.getController();
				Scene scene = new Scene(root, 500, 400);
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/gameData.png")));
				scene.getStylesheets().add(this.getClass().getResource("/gui/css/general.css").toExternalForm());
				scene.getStylesheets().add(this.getClass().getResource("/gui/css/support.css").toExternalForm());
				stage.setScene(scene);
				stage.setTitle("Support Editor");
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(GuiData.getInstance().getStage());
				stage.show();
				if(controller.shouldClose())
					stage.close();

				FileData.getInstance().getWorkingFile().deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void openSupportDLC()
	{
		if(FileDialogs.openBin(GuiData.getInstance().getStage()))
		{
			try
			{
				Stage stage = new Stage();
				GuiData.getInstance().setWorkingStage(stage);
				FXMLLoader loader = new FXMLLoader(Main.class.getResource("/gui/fxml/supportDLC.fxml"));
				Parent root = loader.load();
				SupportDLC controller = loader.getController();
				Scene scene = new Scene(root, 500, 400);
				stage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/gameData.png")));
				scene.getStylesheets().add(this.getClass().getResource("/gui/css/general.css").toExternalForm());
				scene.getStylesheets().add(this.getClass().getResource("/gui/css/support.css").toExternalForm());
				stage.setScene(scene);
				stage.setTitle("Support Editor (DLC)");
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(GuiData.getInstance().getStage());
				stage.show();
				if(controller.shouldClose())
					stage.close();

				FileData.getInstance().getWorkingFile().deleteOnExit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void openScript()
	{
		try 
		{
			@SuppressWarnings("unused")
			Process proc = Runtime.getRuntime().exec("java -jar external/Fates-Script.jar");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void openFEFNightmare()
	{
		try
		{
			@SuppressWarnings("unused")
			Process proc = Runtime.getRuntime().exec("java -jar external/FEFNightmare3.jar");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}