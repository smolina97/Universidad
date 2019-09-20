package fefeditor.gui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import fefeditor.Main;
import fefeditor.common.io.CompressionUtils;
import fefeditor.common.io.IOUtils;
import fefeditor.common.io.MessageBinUtils;
import fefeditor.data.FileData;
import fefeditor.data.GuiData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Dialogue implements Initializable
{
    @FXML private TextArea valueBox;
    @FXML private JFXTextField pidField;
    @FXML private JFXListView<String> dialogueList;
    @FXML private JFXButton saveButton;
    @FXML private AnchorPane pane;
    @FXML private Label pidLabel;
    @FXML private ProgressIndicator progress;

    private List<Path> filePaths = new ArrayList<Path>();
    private List<String> prefixes = new ArrayList<String>();
    private List<String> suffixes = new ArrayList<String>();
    private HashMap<String, List<String>> fileMap = new HashMap<>();

    private String currentPid;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        addEventHandlers();

        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(Main.class.getResourceAsStream("/data/xml/Dialogue.xml"));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getDocumentElement().getElementsByTagName("Types").item(0).getChildNodes();
            for(int x = 0; x < nList.getLength(); x++)
            {
                if(nList.item(x).getNodeType() == Node.ELEMENT_NODE)
                {
                    Node node = nList.item(x);
                    dialogueList.getItems().add(node.getAttributes().getNamedItem("name").getNodeValue());
                    prefixes.add(node.getAttributes().getNamedItem("prefix").getNodeValue());
                    suffixes.add(node.getAttributes().getNamedItem("suffix").getNodeValue());
                    filePaths.add(Paths.get(FileData.getInstance().getWorkingFile() + "\\"
                            + node.getAttributes().getNamedItem("path").getNodeValue()));
                }
            }

            for (Path p : filePaths) {
                if (!fileMap.keySet().contains(p.toString())) {
                    byte[] bytes = CompressionUtils.decompress(p.toFile());
                    String[] lines = MessageBinUtils.extractMessageArchive(bytes);
                    List<String> list = new ArrayList<>();
                    list.addAll(Arrays.asList(lines));
                    fileMap.put(p.toString(), list);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Decompression Error");
            alert.setHeaderText("Unable to decompress file.");
            alert.setContentText("Dialogue Editor was unable to properly decompress one of your files.");
            alert.showAndWait();
        }
    }

    @FXML private void export()
    {
        List<String> temp = new ArrayList<>();
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(GuiData.getInstance().getWorkingStage());
        if(file != null)
        {
            try
            {
                Thread t = new Thread(() -> {
                    for(Path p : filePaths)
                    {
                        if(!temp.contains(p.toString()))
                        {
                            try
                            {
                                progress.setVisible(true);
                                String[] lines = new String[fileMap.get(p.toString()).size()];
                                byte[] out = MessageBinUtils.makeMessageArchive(fileMap.get(p.toString()).toArray(lines));
                                Files.write(p, CompressionUtils.compress(out));
                                IOUtils.copyFolder(FileData.getInstance().getWorkingFile(), file);
                                progress.setVisible(false);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                t.start();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @FXML private void save()
    {
        if(currentPid == null || currentPid.equals("") || dialogueList.getSelectionModel().getSelectedIndex() == -1)
            return;
        int selected = dialogueList.getSelectionModel().getSelectedIndex();
        int dialogueLine = getDialogueLine(selected);
        String line = prefixes.get(selected) + currentPid + suffixes.get(selected) + ": " + valueBox.getText();
        fileMap.get(filePaths.get(selected).toString()).set(dialogueLine, line);
    }

    @FXML private void updateValues()
    {
        valueBox.clear();
        dialogueList.getSelectionModel().clearSelection();
        currentPid = pidField.getText();
        pidLabel.setText("Current PID: " + currentPid);
        pidField.clear();
    }

    private void addEventHandlers()
    {
        GuiData.getInstance().getWorkingStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try
                {
                    Path directory = Paths.get(FileData.getInstance().getWorkingFile().getCanonicalPath());
                    Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        dialogueList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int dialogueLine = getDialogueLine(newValue.intValue());
                if(dialogueLine == -1)
                    return;
                if(dialogueLine == fileMap.get(filePaths.get(newValue.intValue()).toString()).size())
                {
                    String line = prefixes.get(newValue.intValue()) + currentPid + suffixes.get(newValue.intValue())
                            + ": Placeholder";
                    fileMap.get(filePaths.get(newValue.intValue()).toString()).add(line);
                }

                String line = fileMap.get(filePaths.get(newValue.intValue()).toString()).get(dialogueLine);
                String[] split = line.split(": ");
                String value = "";
                for(int x = 1; x < split.length; x++)
                    value += split[x];
                valueBox.setText(value);
            }
        });
    }

    private int getDialogueLine(int index)
    {
        if(currentPid == null || currentPid.equals("") || index == -1)
            return -1;
        Path p = filePaths.get(index);
        List<String> lines = fileMap.get(p.toString());

        String search = prefixes.get(index) + currentPid + suffixes.get(index);
        for(int x = 0; x < lines.size(); x++)
        {
            if(lines.get(x).startsWith(search))
                return x;
        }

        // Indicate that a new line should be added to the file.
        return lines.size();
    }

    public void addAccelerators()
    {
        pane.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), () -> {
            save();
        });
    }
}
