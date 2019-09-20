package nightmare2;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import Model.Target_File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;

public class ViewController implements Initializable
{
	@FXML protected MenuItem saveMenuItem;
	@FXML protected MenuItem saveAsMenuItem;
	@FXML protected MenuItem closeMenuItem;
	@FXML protected MenuItem aboutMenuItem;
	
	@FXML protected ListView<String> moduleList;
	@FXML protected ToolBar toolbar;
	
	private static final String MODULE_PATH = System.getProperty("user.dir") + "/Modules/";
	private List<File> modules = new ArrayList<File>();
	
	private JDialog aboutBox;
	private File currentFile;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
		File dir = new File(MODULE_PATH);
		String[] extensions = new String[] { "nmm"};
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		for (File file : files) {
			modules.add(file);
			moduleList.getItems().add(file.getName());
		}
		
		moduleList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) 
			{
				if (click.getClickCount() == 2) 
				{
					openModule();
				}
			}
		});
	}
	
	@FXML protected void save()
	{
		try 
		{
			save(false);
		} 
		catch (Exception ex) 
		{
			Common_Dialogs.showCatchErrorDialog(ex);
		}
	}
	
	@FXML protected void saveAs()
	{
		try {
			save(true);
		} catch (Exception ex) {
			Common_Dialogs.showCatchErrorDialog(ex);
		}
	}
	
	@FXML protected void close(ActionEvent e)
	{
		if (JOptionPane.showConfirmDialog(null,("Are you sure you want to close " + "the file?"), "Confirm",javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION) {
			if (!Target_File.isSaved())
				Target_File.save(Common_Dialogs.showSaveFileDialog("file", toolbar.getScene().getWindow()));
			unload();
		}
	}
	
	@FXML protected void launchAboutWindow(ActionEvent e)
	{
		if (aboutBox == null) {
			JFrame mainFrame =
				App.getApplication().getMainFrame();
			aboutBox = new AboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		App.getApplication().show(aboutBox);
	}
	
	@FXML protected void selectFile()
	{
		if (!Target_File.isSaved() && Target_File.isOpen())
		{
			if (Common_Dialogs.showYesNoDialog("Close the file?") != javax.swing.JOptionPane.YES_OPTION)
				return;
			else {
				try 
				{
					Target_File.save(Common_Dialogs.showSaveFileDialog("file", toolbar.getScene().getWindow()));
				} 
				catch (Exception ex) 
				{
					Common_Dialogs.showCatchErrorDialog(ex);
				}
			}
		}

		unload();
		File toOpen = Common_Dialogs.showOpenFileDialog("file", toolbar.getScene().getWindow());
		if(toOpen == null)
			return;
		Target_File.open(toOpen);
		if (Target_File.isOpen()) {
			load();
			currentFile = toOpen;
			App.setInitialPath(null);
			Target_File.setLittleEndian();
			try 
			{
				ModuleFrame.start();
			} 
			catch (Exception ex) 
			{
				Common_Dialogs.showCatchErrorDialog(ex);
			}
		}
	}
	
	@FXML protected void openModule()
	{
		try
		{
			ModuleFrame.openModule(modules.get(moduleList.getSelectionModel().getSelectedIndex()));
		}
		catch(Exception ex)
		{
			
		}
	}
	
	@FXML private void directClose()
	{
		if (!Target_File.isSaved())
			Target_File.save(currentFile);
		unload();
	}
	
	@FXML private void directSave()
	{
		if(!Target_File.isSaved())
			Target_File.save(currentFile);
	}
	
	private void unload() {
		Target_File.closeFile();
		ModuleFrame.resetOpenModules();
		currentFile = null;
		
		saveMenuItem.setDisable(true);
		saveAsMenuItem.setDisable(true);
		closeMenuItem.setDisable(true);
		toolbar.setDisable(true);
		moduleList.setDisable(true);
	}

	private void load() {
		saveMenuItem.setDisable(false);
		saveAsMenuItem.setDisable(false);
		closeMenuItem.setDisable(false);
		toolbar.setDisable(false);
		moduleList.setDisable(false);
	}
	
	private void save(boolean saveAs) {
		try {
			if (!Target_File.isNamed() || saveAs)
				Target_File.save(Common_Dialogs.showSaveFileDialog("file", toolbar.getScene().getWindow()));
			else
				Target_File.save(new File(Target_File.fileName()));
		} catch (Exception e) {
			Common_Dialogs.showCatchErrorDialog(e);
		}
	}
}
