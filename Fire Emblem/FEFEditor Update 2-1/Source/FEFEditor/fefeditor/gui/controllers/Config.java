package fefeditor.gui.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import fefeditor.Main;
import fefeditor.common.io.BinFile;
import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Config implements Initializable
{
    private static final int PREP_MUSIC_POINTER_OFFSET = 0xB0;
    private static final int MUSIC_POINTER_OFFSET = 0xA8;
    private static final int CHANNEL_FLAG_OFFSET = 0xB5;

    @FXML private JFXComboBox<String> prepBox;
    @FXML private JFXComboBox<String> mapBox;
    @FXML private JFXCheckBox channelCheck;

    private List<String> musicLabels;

    private BinFile file;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/data/text/music.txt")));
            List<String> temp = new ArrayList<>();
            String str;
            while((str = reader.readLine()) != null)
                temp.add(str);
            musicLabels = new ArrayList<>();
            for(String s : temp)
            {
                String[] split = s.split(": ");
                musicLabels.add(split[0]);
                prepBox.getItems().add(split[1]);
                mapBox.getItems().add(split[1]);
            }

            file = new BinFile(FileData.getInstance().getWorkingFile(), "rw");

            file.seek(PREP_MUSIC_POINTER_OFFSET);
            String prepLabel = file.readStringFromPointer();
            file.seek(MUSIC_POINTER_OFFSET);
            String mapMusic = file.readStringFromPointer();

            prepBox.getSelectionModel().select(getMusicIndex(prepLabel));
            mapBox.getSelectionModel().select(getMusicIndex(mapMusic));

            file.seek(CHANNEL_FLAG_OFFSET);
            if(file.readByte() == 0x1)
                channelCheck.setSelected(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML private void export()
    {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Bin File (*.bin)", "*.bin");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files", "*");
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showSaveDialog(GuiData.getInstance().getStage());

        if(file != null)
        {
            try
            {
                Path path = Paths.get(file.getCanonicalPath());
                Files.write(path, this.file.toByteArray());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @FXML private void save()
    {
        try
        {
            file.seek(MUSIC_POINTER_OFFSET);
            file.setLength(file.readLittleInt() + 0x20);
            file.seek(file.length());
            file.writeString(musicLabels.get(mapBox.getSelectionModel().getSelectedIndex()));
            file.seek(PREP_MUSIC_POINTER_OFFSET);
            file.writeLittleInt((int)(file.length() - 0x20));
            file.seek(file.length());
            file.writeString(musicLabels.get(prepBox.getSelectionModel().getSelectedIndex()));
            file.seek(CHANNEL_FLAG_OFFSET);
            if(channelCheck.isSelected())
                file.writeByte(0x1);
            else
                file.writeByte(0x0);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private int getMusicIndex(String label)
    {
        for(int x = 0; x < musicLabels.size(); x++)
        {
            if(label.equals(musicLabels.get(x)))
                return x;
        }
        return -1;
    }
}
