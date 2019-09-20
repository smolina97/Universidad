package fefeditor.gui.controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import fefeditor.bin.blocks.TileBlock;
import fefeditor.bin.formats.FatesTerrainFile;
import fefeditor.common.FileDialogs;
import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Terrain implements Initializable
{
    @FXML private JFXListView<String> tileList;
    @FXML private TilePane tilePane;
    @FXML private VBox formBox;
    @FXML private MenuItem saveItem;

    private Label[][] tileLabels;

    private FatesTerrainFile file;

    private List<JFXTextField> coreFields = new ArrayList<>();
    private List<JFXTextField> blockFields = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        GuiData.getInstance().getWorkingStage().setOnCloseRequest(we -> close());

        try
        {
            file = new FatesTerrainFile(FileData.getInstance().getWorkingFile(), "rw");
            for(TileBlock t : file.getTiles()) {
                tileList.getItems().add(t.getTid());
            }
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
        setupGrid();

        populateForm();

        tileList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> updateFields(newValue.intValue()));

        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
    }

    @FXML private void close()
    {
        Stage stage = (Stage) formBox.getScene().getWindow();
        stage.close();
    }

    @FXML private void export()
    {
        try
        {
            file.compile();
            FileDialogs.saveStandardBin(GuiData.getInstance().getWorkingStage(), file);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML private void save()
    {
        file.setMapModel(coreFields.get(0).getText());
        file.setMapSizeX(Byte.parseByte(coreFields.get(1).getText(), 16));
        file.setMapSizeY(Byte.parseByte(coreFields.get(2).getText(), 16));
        file.setBorderSizeX(Byte.parseByte(coreFields.get(3).getText(), 16));
        file.setBorderSizeY(Byte.parseByte(coreFields.get(4).getText(), 16));

        if(tileList.getSelectionModel().getSelectedIndex() == -1)
            return;
        TileBlock t = file.getTiles().get(tileList.getSelectionModel().getSelectedIndex());
        t.setTid(blockFields.get(0).getText());
        t.setmTid(blockFields.get(1).getText());

        byte[] changeIds = new byte[3];
        changeIds[0] = Byte.parseByte(blockFields.get(2).getText(), 16);
        changeIds[1] = Byte.parseByte(blockFields.get(3).getText(), 16);
        changeIds[2] = Byte.parseByte(blockFields.get(4).getText(), 16);
        t.setChangeIds(changeIds);

        t.setDefenseBonus(Byte.parseByte(blockFields.get(5).getText(), 16));
        t.setAvoidBonus(Byte.parseByte(blockFields.get(6).getText(), 16));
        t.setDefenseBonus(Byte.parseByte(blockFields.get(7).getText(), 16));
        t.setEffect(blockFields.get(8).getText());

        int selectedIndex = tileList.getSelectionModel().getSelectedIndex();
        clearFields();
        tileList.getItems().clear();
        for(TileBlock ti : file.getTiles())
            tileList.getItems().add(ti.getTid());
        tileList.getSelectionModel().select(selectedIndex);
    }

    @FXML private void addTile()
    {
        TileBlock t = new TileBlock(file.getTiles().get(0));
        t.setPlacementNumber((byte) (file.getTiles().size() - 1));
        file.getTiles().add(t);
        tileList.getItems().add(t.getTid());
    }

    private void setupGrid()
    {
        tileLabels = new Label[32][32];
        for(int x = 0; x < 32; x++)
        {
            for(int y = 0; y < 32; y++)
            {
                Label label = new Label();
                label.setPrefSize(22, 20);
                label.setText(file.getMap()[x][y] + "");
                label.setId(label.getText().equals("0") ? "inactiveTile" : "activeTile");
                tileLabels[y][x] = label;
                tilePane.getChildren().add(label);

                final int coordX = y;
                final int coordY = x;
                label.setOnMouseClicked(event -> setTile(coordX, coordY));
            }
        }
    }

    private void updateFields(int index)
    {
        if(index == -1)
            return;
        clearFields();
        TileBlock t = file.getTiles().get(index);
        blockFields.get(0).setText(t.getTid());
        blockFields.get(1).setText(t.getmTid());
        blockFields.get(2).setText(Integer.toHexString(t.getChangeIds()[0]));
        blockFields.get(3).setText(Integer.toHexString(t.getChangeIds()[1]));
        blockFields.get(4).setText(Integer.toHexString(t.getChangeIds()[2]));
        blockFields.get(5).setText(Integer.toHexString(t.getDefenseBonus()));
        blockFields.get(6).setText(Integer.toHexString(t.getAvoidBonus()));
        blockFields.get(7).setText(Integer.toHexString(t.getHealingBonus()));
        blockFields.get(8).setText(t.getEffect());
    }

    private void clearFields()
    {
        for(TextField t : blockFields)
            t.clear();
    }

    private void setTile(int x, int y)
    {
        if(tileList.getSelectionModel().getSelectedIndex() == -1)
            return;
        byte value = file.getTiles().get(tileList.getSelectionModel().getSelectedIndex()).getPlacementNumber();
        file.setTile(x, y, value);
        tileLabels[x][y].setText(Integer.toHexString(value));
        if(tileLabels[x][y].getText().equals("0"))
            tileLabels[x][y].setId("inactiveTile");
        else
            tileLabels[x][y].setId("activeTile");
    }

    private void populateForm() {
        StringConverter<Byte> hexFormatter = new StringConverter<Byte>() {
            @Override
            public Byte fromString(String string) {
                return Byte.parseByte(string, 16);
            }

            @Override
            public String toString(Byte object) {
                if (object == null)
                    return "";
                return Integer.toHexString(object);
            }
        };

        String[] labels = { "Map Model:", "Map Size X:", "Map Size Y:", "Border Size X:", "Border Size Y:" };
        for (String label1 : labels) {
            Label label = new Label(label1);
            label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
            formBox.getChildren().add(label);

            JFXTextField field = new JFXTextField();
            formBox.getChildren().add(field);
            coreFields.add(field);
        }
        coreFields.get(1).setTextFormatter(new TextFormatter<>(hexFormatter));
        coreFields.get(2).setTextFormatter(new TextFormatter<>(hexFormatter));
        coreFields.get(3).setTextFormatter(new TextFormatter<>(hexFormatter));
        coreFields.get(4).setTextFormatter(new TextFormatter<>(hexFormatter));
        formBox.getChildren().add(new Label(""));

        String[] blockLabels = { "TID:", "MTID:", "Change ID 1:", "Change ID 2:", "Change Id 3:",
                                 "Defense Bonus:", "Avoid Bonus:", "Healing Bonus:", "Effect:"};
        for (String blockLabel : blockLabels) {
            Label label = new Label(blockLabel);
            label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
            formBox.getChildren().add(label);

            JFXTextField field = new JFXTextField();
            formBox.getChildren().add(field);
            blockFields.add(field);
        }
        blockFields.get(2).setTextFormatter(new TextFormatter<>(hexFormatter));
        blockFields.get(3).setTextFormatter(new TextFormatter<>(hexFormatter));
        blockFields.get(4).setTextFormatter(new TextFormatter<>(hexFormatter));
        blockFields.get(5).setTextFormatter(new TextFormatter<>(hexFormatter));
        blockFields.get(6).setTextFormatter(new TextFormatter<>(hexFormatter));
        blockFields.get(7).setTextFormatter(new TextFormatter<>(hexFormatter));

        coreFields.get(0).setText(file.getMapModel());
        coreFields.get(1).setText(Integer.toHexString(file.getMapSizeX()));
        coreFields.get(2).setText(Integer.toHexString(file.getMapSizeY()));
        coreFields.get(3).setText(Integer.toHexString(file.getBorderSizeX()));
        coreFields.get(4).setText(Integer.toHexString(file.getBorderSizeY()));
    }
}
