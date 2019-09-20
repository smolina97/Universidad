package fefeditor.gui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import fefeditor.bin.blocks.DispoBlock;
import fefeditor.bin.blocks.DispoFaction;
import fefeditor.bin.formats.FatesDispoFile;
import fefeditor.common.ArrayUtils;
import fefeditor.common.FileDialogs;
import fefeditor.common.feflib.inject.ArrayConvert;
import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Dispo implements Initializable
{
	@FXML private TreeView factionTree;
	@FXML private TilePane tilePane;
	@FXML private VBox formBox;
	@FXML private CheckMenuItem coordCheck;
	@FXML private MenuItem saveItem;

	private Label[][] dispoLabels;
    private List<JFXTextField> formFields;

	private FatesDispoFile file;
	private DispoFaction selectedFaction;
	private DispoBlock selectedBlock;

    @Override
	public void initialize(URL url, ResourceBundle rb)
	{
		GuiData.getInstance().getWorkingStage().setOnCloseRequest(we -> close());

		try
		{
			file = new FatesDispoFile(FileData.getInstance().getWorkingFile(), "rw");
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

		tilePane.setPrefColumns(32);
		tilePane.setPrefRows(32);
		tilePane.setPrefTileHeight(20);
		tilePane.setPrefTileWidth(22);
		tilePane.setTileAlignment(Pos.TOP_CENTER);
		setupDispoGrid();
		populateTree();

		factionTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
			TreeItem<String> item = (TreeItem<String>) newValue;
			updateSelection(item);
        });
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

		populateForm();
	}

	@FXML private void close()
	{
		try
		{
			file.close();
			Stage stage = (Stage) formBox.getScene().getWindow();
			stage.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Stage stage = (Stage) formBox.getScene().getWindow();
			stage.close();
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

	@FXML private void save()
	{
		saveBlock();
	}

	@FXML private void addBlock()
	{
		if(selectedFaction != null)
		{
			TextInputDialog dialog = new TextInputDialog("Placeholder");
			dialog.setTitle("Add Spawn");
			dialog.setHeaderText("Enter Spawn PID");
			dialog.setContentText("Please enter a PID for the spawn:");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
				String str = result.get();
				for(DispoFaction f : file.getFactions())
				{
					if(f.getName().equals(str))
					{
						throwNameInUseDialog();
						return;
					}
				}
				selectedBlock = null;
				DispoBlock block = new DispoBlock(str);
				selectedFaction.addSpawn(block);
				populateTree();
				selectFaction(selectedFaction);
				clearFields();
			}
		}
	}

	@FXML private void removeBlock()
	{
		if(selectedFaction != null && selectedBlock != null)
		{
			selectedFaction.getSpawns().remove(selectedBlock);
			selectedBlock = null;
			populateTree();
			for(Object o : factionTree.getRoot().getChildren())
			{
				TreeItem<String> item = (TreeItem<String>) o;
				if(item.getValue().equals(selectedFaction.getName()))
				{
					factionTree.getSelectionModel().select(item);
					item.setExpanded(true);
					clearFields();
					break;
				}
			}
		}
	}

	@FXML private void addFaction()
	{
		TextInputDialog dialog = new TextInputDialog("Placeholder");
		dialog.setTitle("Add Faction");
		dialog.setHeaderText("Enter Faction Name");
		dialog.setContentText("Please enter an unused name:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			String str = result.get();
			for(DispoFaction f : file.getFactions())
			{
				for(DispoBlock b : f.getSpawns())
				{
					if(b.getPid().equals(str))
					{
						throwNameInUseDialog();
						return;
					}
				}
				if(f.getName().equals(str))
				{
					throwNameInUseDialog();
					return;
				}
			}
			selectedFaction = null;
			selectedBlock = null;
			DispoFaction faction = new DispoFaction(str);
			file.getFactions().add(faction);
			populateTree();
			clearFields();
		}
	}

	@FXML private void removeFaction()
	{
		if(selectedFaction != null)
		{
			file.getFactions().remove(selectedFaction);
			selectedFaction = null;
			selectedBlock = null;
			updateOccupiedCoords();
			populateTree();
			clearFields();
		}
	}

	@FXML private void toggleCoordTwo()
	{
		selectedBlock = null;
		selectedFaction = null;
		factionTree.getSelectionModel().select(factionTree.getRoot());
		clearFields();
		updateOccupiedCoords();
	}

	private void selectFaction(DispoFaction faction)
	{
		for(Object o : factionTree.getRoot().getChildren())
		{
			TreeItem<String> item = (TreeItem<String>) o;
			if(item.getValue().equals(faction.getName()))
			{
				factionTree.getSelectionModel().select(item);
				item.setExpanded(true);
				break;
			}
		}
	}

	private void updateSelection(TreeItem<String> item)
	{
		clearFields();
		if(item == null || factionTree.getRoot() == null)
			return;
		if(item.equals(factionTree.getRoot()))
			return;

		selectedFaction = null;
		selectedBlock = null;

		boolean isFaction = false;
		for(DispoFaction f : file.getFactions())
		{
			if(f.getName().equals(item.getValue()))
				isFaction = true;
		}
		if(isFaction)
		{
			updateOccupiedCoords(item.getValue());
			return;
		}

		// The selected item is a character block.
		TreeItem<String> factionItem = item.getParent();
		updateOccupiedCoords(factionItem.getValue());
		setSelectedSpawn(factionItem, item);
		updateFields();
	}

	private void updateFields()
	{
		formFields.get(0).setText(selectedBlock.getPid());
		formFields.get(1).setText(selectedBlock.getItems()[0]);
		formFields.get(2).setText(selectedBlock.getItems()[1]);
		formFields.get(3).setText(selectedBlock.getItems()[2]);
		formFields.get(4).setText(selectedBlock.getItems()[3]);
		formFields.get(5).setText(selectedBlock.getItems()[4]);
		formFields.get(6).setText(Long.toHexString(ArrayUtils.toInteger(selectedBlock.getItemBitflags()[0])) + "");
		formFields.get(7).setText(Long.toHexString(ArrayUtils.toInteger(selectedBlock.getItemBitflags()[1])) + "");
		formFields.get(8).setText(Long.toHexString(ArrayUtils.toInteger(selectedBlock.getItemBitflags()[2])) + "");
		formFields.get(9).setText(Long.toHexString(ArrayUtils.toInteger(selectedBlock.getItemBitflags()[3])) + "");
		formFields.get(10).setText(Long.toHexString(ArrayUtils.toInteger(selectedBlock.getItemBitflags()[4])) + "");
		formFields.get(11).setText(selectedBlock.getSkills()[0]);
		formFields.get(12).setText(selectedBlock.getSkills()[1]);
		formFields.get(13).setText(selectedBlock.getSkills()[2]);
		formFields.get(14).setText(selectedBlock.getSkills()[3]);
		formFields.get(15).setText(selectedBlock.getSkills()[4]);
		formFields.get(16).setText(selectedBlock.getAc());
		formFields.get(17).setText(selectedBlock.getAiPositionOne());
		formFields.get(18).setText(selectedBlock.getMi());
		formFields.get(19).setText(selectedBlock.getAiPositionTwo());
		formFields.get(20).setText(selectedBlock.getAt());
		formFields.get(21).setText(selectedBlock.getAiPositionThree());
		formFields.get(22).setText(selectedBlock.getMv());
		formFields.get(23).setText(selectedBlock.getAiPositionFour());
		formFields.get(24).setText(selectedBlock.getTeam() + "");
		formFields.get(25).setText(selectedBlock.getLevel() + "");
		formFields.get(26).setText(Long.toHexString(selectedBlock.getSkillFlag()) + "");
		formFields.get(27).setText(Long.toHexString(ArrayUtils.toInteger(selectedBlock.getSpawnBitflags())) + "");
	}

	private void populateTree()
	{
		TreeItem<String> rootItem = new TreeItem<String> (FileData.getInstance().getOriginal().getName());
		rootItem.setExpanded(true);
		for(DispoFaction f : file.getFactions())
		{
			TreeItem<String> faction = new TreeItem<String>(f.getName());
			for(DispoBlock b : f.getSpawns())
			{
				TreeItem<String> block = new TreeItem<String>(b.getPid());
				faction.getChildren().add(block);
			}
			rootItem.getChildren().add(faction);
		}

		factionTree.setRoot(rootItem);
		updateOccupiedCoords();
	}

	private void updateOccupiedCoords()
	{
		for(Node n : tilePane.getChildren())
			n.setId("dispoGrid");
		for(DispoFaction f : file.getFactions())
		{
			for(DispoBlock b : f.getSpawns())
			{
				int x;
				int y;
				if(coordCheck.isSelected())
				{
					x = b.getSecondCoord()[0];
					y = b.getSecondCoord()[1];
				}
				else
				{
					x = b.getFirstCoord()[0];
					y = b.getFirstCoord()[1];
				}
				if(x != -1 && y != -1)
                    dispoLabels[x][y].setId("occupiedCoord");
			}
		}
	}

	private void updateOccupiedCoords(String factionName)
	{
		for(Node n : tilePane.getChildren())
			n.setId("dispoGrid");
		for(DispoFaction f : file.getFactions())
		{
			if(factionName.equals(f.getName()))
				selectedFaction = f;
			for(DispoBlock b : f.getSpawns())
			{
				int x;
				int y;
				if(coordCheck.isSelected())
				{
					x = b.getSecondCoord()[0];
					y = b.getSecondCoord()[1];
				}
				else
				{
					x = b.getFirstCoord()[0];
					y = b.getFirstCoord()[1];
				}
				if(factionName.equals(f.getName())) {
                    if(x != -1 && y != -1)
                        dispoLabels[x][y].setId("selectedFaction");
                }
				else {
                    if(x != -1 && y != -1)
                        dispoLabels[x][y].setId("occupiedCoord");
                }
			}
		}
	}

	private void setupDispoGrid()
	{
		dispoLabels = new Label[32][32];
		for(int x = 0; x < 32; x++)
		{
			for(int y = 0; y < 32; y++)
			{
				Label label = new Label();
				label.setPrefSize(22, 20);
				label.setId("dispoGrid");
				dispoLabels[y][x] = label;
				tilePane.getChildren().add(label);

				final int xCoord = y;
				final int yCoord = x;
				label.setOnMouseClicked(event -> moveBlock(xCoord, yCoord));
			}
		}
	}

	private void setSelectedSpawn(TreeItem<String> faction, TreeItem<String> spawn)
	{
		int factionIndex = -1;
		for(DispoFaction f : file.getFactions())
		{
			if(f.getName().equals(faction.getValue()))
				factionIndex = file.getFactions().indexOf(f);
		};
		int blockIndex = faction.getChildren().indexOf(spawn);

		DispoBlock block = file.getFactions().get(factionIndex).getSpawns().get(blockIndex);
		if(block.getFirstCoord()[0] != -1 && block.getFirstCoord()[1] != -1)
		    dispoLabels[block.getFirstCoord()[0]][block.getFirstCoord()[1]].setId("selectedBlock");

		selectedBlock = block;
	}

	private void moveBlock(int x , int y)
	{
		if(selectedBlock != null && selectedFaction != null)
		{
			if(coordCheck.isSelected())
				selectedBlock.setSecondCoord(new byte[] { (byte) x, (byte) y });
			else
				selectedBlock.setFirstCoord(new byte[] { (byte) x, (byte) y });

			// Prompt the tree to reload the selection at the new coordinate.
			updateSelection((TreeItem<String>) factionTree.getSelectionModel().getSelectedItem());
		}
	}

	private void populateForm()
	{
        StringConverter<Long> hexFormatter = new StringConverter<Long>() {
            @Override
            public Long fromString(String string) {
                return Long.parseLong(string, 16);
            }

            @Override
            public String toString(Long object) {
                if (object == null)
                    return "";
                return Long.toHexString(object).toUpperCase();
            }
        };

        List<Label> formLabels = Arrays.asList(
                new Label("PID:"), new Label("Item 1:"),
                new Label("Item 2:"), new Label("Item 3:"),
                new Label("Item 4:"), new Label("Item 5:"),
                new Label("Item 1 Bitflags:"), new Label("Item 2 Bitflags:"),
                new Label("Item 3 Bitflags:"), new Label("Item 4 Bitflags:"),
                new Label("Item 5 Bitflags:"), new Label("Skill 1:"),
                new Label("Skill 2:"), new Label("Skill 3:"),
                new Label("Skill 4:"), new Label("Skill 5:"),
                new Label("AI AC:"), new Label("AC Parameter:"),
                new Label("AI MI:"), new Label("MI Parameter:"),
                new Label("AI AT:"), new Label("AT Parameter:"),
                new Label("AI MV:"), new Label("MV Parameter"),
                new Label("Team:"), new Label("Level:"),
                new Label("Skill Flag:"), new Label("Spawn Bitflags")
        );

		JFXButton saveButton = new JFXButton("Save");
		saveButton.setStyle("-fx-border-color: black;");
		formBox.widthProperty().addListener((observable, oldValue, newValue) -> saveButton.setPrefWidth(newValue.doubleValue()));
		saveButton.setOnAction(event -> saveBlock());
		formBox.getChildren().add(saveButton);

		formFields = new ArrayList<>();
		for(int x = 0; x < formLabels.size(); x++)
		{
			formFields.add(new JFXTextField());
			formLabels.get(x).setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
			formBox.getChildren().add(formLabels.get(x));
			formBox.getChildren().add(formFields.get(x));

			String title = formLabels.get(x).getText().toUpperCase();
			if(title.contains("FLAG") || title.contains("TEAM") || title.contains("LEVEL"))
			{
				formFields.get(x).setTextFormatter(new TextFormatter<>(hexFormatter));
				formFields.get(x).setText("");
			}
		}
	}

	private void saveBlock()
	{
		if(selectedBlock != null)
		{
			selectedBlock.setPid(formFields.get(0).getText());
			selectedBlock.setItem(formFields.get(1).getText(), 0);
			selectedBlock.setItem(formFields.get(2).getText(), 1);
			selectedBlock.setItem(formFields.get(3).getText(), 2);
			selectedBlock.setItem(formFields.get(4).getText(), 3);
			selectedBlock.setItem(formFields.get(5).getText(), 4);
			byte[][] bitflags = new byte[5][4];
			for(int x = 0; x < 5; x++)
				bitflags[x] = ArrayConvert.toByteArray(Integer.parseInt(formFields.get(6 + x).getText(), 16));
			selectedBlock.setItemBitflags(bitflags);
			selectedBlock.setSkill(formFields.get(11).getText(), 0);
			selectedBlock.setSkill(formFields.get(12).getText(), 1);
			selectedBlock.setSkill(formFields.get(13).getText(), 2);
			selectedBlock.setSkill(formFields.get(14).getText(), 3);
			selectedBlock.setSkill(formFields.get(15).getText(), 4);
			selectedBlock.setAc(formFields.get(16).getText());
			selectedBlock.setAiPositionOne(formFields.get(17).getText());
			selectedBlock.setMi(formFields.get(18).getText());
			selectedBlock.setAiPositionTwo(formFields.get(19).getText());
			selectedBlock.setAt(formFields.get(20).getText());
			selectedBlock.setAiPositionThree(formFields.get(21).getText());
			selectedBlock.setMv(formFields.get(22).getText());
			selectedBlock.setAiPositionFour(formFields.get(23).getText());
			selectedBlock.setTeam((byte) Integer.parseInt(formFields.get(24).getText(), 16));
			selectedBlock.setLevel((byte) Integer.parseInt(formFields.get(25).getText(), 16));
			selectedBlock.setSkillFlag(Integer.parseInt(formFields.get(26).getText(), 16));
			selectedBlock.setSpawnBitflags(ArrayConvert.toByteArray(Integer.parseInt(formFields.get(27).getText(), 16)));

			TreeItem<String> selected = (TreeItem<String>) factionTree.getSelectionModel().getSelectedItem();
			selected.setValue(selectedBlock.getPid());
		}
	}

	private void clearFields()
	{
		for(TextField t : formFields)
			t.clear();
	}

	private void throwNameInUseDialog()
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error Encountered");
		alert.setHeaderText("Name already in use.");
		alert.setContentText("The name you chose is currently in use somewhere else. Please choose a different name.");
		alert.showAndWait();
	}
}
