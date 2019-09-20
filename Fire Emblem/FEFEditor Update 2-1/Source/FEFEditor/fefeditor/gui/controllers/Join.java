package fefeditor.gui.controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import fefeditor.bin.blocks.JoinBlock;
import fefeditor.bin.formats.FatesJoinFile;
import fefeditor.common.FileDialogs;
import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Join implements Initializable
{
	@FXML private JFXListView<String> joinList;
	@FXML private JFXTextField characterText;
	@FXML private JFXTextField birthrightText;
	@FXML private JFXTextField conquestText;
	@FXML private JFXTextField revelationText;

	private MenuItem addBlock;
	private MenuItem removeBlock;
	
	private FatesJoinFile file;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		try {
			file = new FatesJoinFile(FileData.getInstance().getWorkingFile(), "rw");
			file.readBlocks();
			for(JoinBlock j : file.getBlocks())
				joinList.getItems().add(j.getCharacter());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ContextMenu contextMenu = new ContextMenu();
		addBlock = new MenuItem("Add Block");
		removeBlock = new MenuItem("Remove Block");
		contextMenu.getItems().add(addBlock);
		contextMenu.getItems().add(removeBlock);
		joinList.setContextMenu(contextMenu);
		
		addEventHandlers();
	}
	
	@FXML private void save()
	{
		if(joinList.getSelectionModel().getSelectedIndex() != -1)
		{
			int index = joinList.getSelectionModel().getSelectedIndex();
			JoinBlock block = file.getBlocks().get(index);
			block.setCharacter(characterText.getText());
			block.setBirthrightJoin(birthrightText.getText());
			block.setConquestJoin(conquestText.getText());
			block.setRevelationJoin(revelationText.getText());
			updateList();
		}
	}
	
	@FXML private void export()
	{
		try
		{
			file.writeFile();
			FileDialogs.saveStandardBin(GuiData.getInstance().getWorkingStage(), file);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void addEventHandlers()
	{
		GuiData.getInstance().getWorkingStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				try 
				{
					file.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		});
		
		joinList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) 
			{
				if(joinList.getSelectionModel().getSelectedIndex() != -1)
				{
					int index = newValue.intValue();
					characterText.setText(file.getBlocks().get(index).getCharacter());
					birthrightText.setText(file.getBlocks().get(index).getBirthrightJoin());
					conquestText.setText(file.getBlocks().get(index).getConquestJoin());
					revelationText.setText(file.getBlocks().get(index).getRevelationJoin());
				}
			}
		});
		
		addBlock.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				JoinBlock j = new JoinBlock();
				if(file.getBlocks().size() > 0)
					j = new JoinBlock(file.getBlocks().get(0));
				file.getBlocks().add(j);
				updateList();
			}
		});
		
		removeBlock.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				if(joinList.getSelectionModel().getSelectedIndex() != -1)
					file.getBlocks().remove(joinList.getSelectionModel().getSelectedIndex());
				updateList();
			}
		});
	}
	
	private void updateList()
	{
		joinList.getItems().clear();
		for(JoinBlock j : file.getBlocks())
			joinList.getItems().add(j.getCharacter());
	}
}
