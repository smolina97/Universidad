package fefeditor.gui.controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import fefeditor.bin.blocks.CharacterBlock;
import fefeditor.bin.formats.FatesPersonFile;
import fefeditor.common.FileDialogs;
import fefeditor.common.io.IOUtils;
import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Person implements Initializable
{
	@FXML private JFXListView<String> personList;
	@FXML private JFXTextField pidText;
	@FXML private JFXTextField aidText;
	@FXML private JFXTextField fidText;
	@FXML private JFXTextField mPidText;
	@FXML private JFXTextField mPidHText;
	@FXML private JFXTextField combatText;
	@FXML private JFXTextField voiceText;

	private MenuItem addBlock;
	private MenuItem removeBlock;
	
	private FatesPersonFile person;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		try {
			person = new FatesPersonFile(FileData.getInstance().getWorkingFile(), "rw");
			updateList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ContextMenu contextMenu = new ContextMenu();
		addBlock = new MenuItem("Add Block");
		removeBlock = new MenuItem("Remove Block");
		contextMenu.getItems().add(addBlock);
		contextMenu.getItems().add(removeBlock);
		personList.setContextMenu(contextMenu);
		
		addListeners();
	}
	
	@FXML private void save()
	{
		if(personList.getSelectionModel().getSelectedIndex() != -1)
		{
			CharacterBlock c = person.getCharacters().get(personList.getSelectionModel().getSelectedIndex());
			c.setPid(pidText.getText());
			c.setFid(fidText.getText());
			c.setAid(aidText.getText());
			c.setmPid(mPidText.getText());
			c.setmPidH(mPidHText.getText());
			c.setCombatMusic(combatText.getText());
			c.setEnemyVoice(voiceText.getText());
			updateList();
		}
	}
	
	@FXML private void export()
	{
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter lzFilter = new FileChooser.ExtensionFilter("Compressed Bin File (*.bin.lz)", "*.bin.lz");
		FileChooser.ExtensionFilter binFilter = new FileChooser.ExtensionFilter("Bin File (*.bin)", "*.bin");
		FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files", "*");
		chooser.getExtensionFilters().addAll(lzFilter, binFilter, allFilter);
		File file = chooser.showSaveDialog(GuiData.getInstance().getWorkingStage());
		if(file != null)
		{
			String fileName;
			if(file.getName().endsWith(".bin"))
				fileName = file.getName().substring(0, file.getName().length() - 4);
			else if(file.getName().endsWith(".bin.lz"))
				fileName = file.getName().substring(0, file.getName().length() - 7);
			else
				fileName = file.getName();
			try
			{
				person.writeFile(fileName);
				FileDialogs.saveStandardBin(person, file);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	@FXML private void updateFields()
	{
		if(personList.getSelectionModel().getSelectedIndex() != -1)
		{
			CharacterBlock c = person.getCharacters().get(personList.getSelectionModel().getSelectedIndex());
			pidText.setText(c.getPid());
			aidText.setText(c.getAid());
			fidText.setText(c.getFid());
			mPidText.setText(c.getmPid());
			mPidHText.setText(c.getmPidH());
			combatText.setText(c.getCombatMusic());
			voiceText.setText(c.getEnemyVoice());
		}
	}
	
	private void clearFields()
	{
		pidText.clear();
		aidText.clear();
		fidText.clear();
		mPidText.clear();
		mPidHText.clear();
		combatText.clear();
		voiceText.clear();
	}
	
	private void updateList()
	{
		clearFields();
		personList.getItems().clear();
		for(CharacterBlock c : person.getCharacters()) {
			personList.getItems().add(c.getPid());
			System.out.println(c.getPid());
		}
	}
	
	private void addListeners()
	{
		GuiData.getInstance().getWorkingStage().setOnCloseRequest(we -> {
            try
            {
                person.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
		
		addBlock.setOnAction(event -> {
            CharacterBlock c = new CharacterBlock();
            if(person.getCharacters().size() > 0)
                c = new CharacterBlock(person.getCharacters().get(0));
            c.setPid("Placeholder PID");
            person.getCharacters().add(c);
            updateList();
        });
		
		removeBlock.setOnAction(event -> {
            if(personList.getSelectionModel().getSelectedIndex() != -1)
                person.getCharacters().remove(personList.getSelectionModel().getSelectedIndex());
            updateList();
        });
	}
}
