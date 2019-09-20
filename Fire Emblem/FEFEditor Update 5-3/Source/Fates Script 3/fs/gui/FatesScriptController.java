package fs.gui;

import feflib.script.ScriptCompiler;
import feflib.script.ScriptDecompiler;
import fs.data.MainData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.function.IntFunction;
import java.util.regex.Matcher;


public class FatesScriptController implements Initializable {

    @FXML
    private TabPane tabPane;
    private boolean setStyle = false;

    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    @FXML
    private void beginDragDrop(DragEvent event) {
        if (event.getGestureSource() != tabPane) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    @FXML
    private void openFile() throws Exception {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter cmbFilter = new FileChooser.ExtensionFilter("Script file (*.cmb)", "*.cmb");
        FileChooser.ExtensionFilter fscriptFilter = new FileChooser.ExtensionFilter("Uncompiled Script file (*.fscript)",
                "*.fscript");
        chooser.getExtensionFilters().add(cmbFilter);
        chooser.getExtensionFilters().add(fscriptFilter);
        File file = chooser.showOpenDialog(MainData.getInstance().getStage());
        if (file != null) {
            createTab(file);
        }
    }

    @FXML
    private void saveFile() throws Exception {
        if (tabPane.getSelectionModel().getSelectedItem() != null) {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Uncompiled Script file (*.fscript)",
                    "*.fscript");
            chooser.getExtensionFilters().add(extFilter);
            chooser.setInitialFileName(tabPane.getSelectionModel().getSelectedItem().getText());
            File file = chooser.showSaveDialog(MainData.getInstance().getStage());
            if (file != null) {
                PrintWriter writer = new PrintWriter(file);
                CodeArea area = (CodeArea) tabPane.getSelectionModel().getSelectedItem().getContent().lookup("#scriptArea");
                writer.write(area.getText());
                writer.close();
            }
        }
    }

    @FXML
    private void compileAllTabs() throws Exception {
        if (tabPane.getTabs().size() > 0) {
            DirectoryChooser chooser = new DirectoryChooser();
            File file = chooser.showDialog(MainData.getInstance().getStage());
            if (file != null) {
                for (Tab tab : tabPane.getTabs()) {
                    ScriptCompiler compiler = new ScriptCompiler(file.getName());
                    CodeArea script = (CodeArea) tab.getContent().lookup("#scriptArea");
                    compiler.compile(file.toPath(), script.getText());
                }
            }
        }
    }

    @FXML
    private void createNewScriptTab(DragEvent event) throws Exception {
        Dragboard board = event.getDragboard();
        for (File f : board.getFiles())
            createTab(f);
        event.consume();
    }

    @FXML
    private void closeTab() {
        if (tabPane.getSelectionModel().getSelectedIndex() != -1) {
            tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    private void compileCurrentTab() throws Exception {
        if (tabPane.getSelectionModel().getSelectedItem() != null) {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Script files (*.cmb)",
                    "*.cmb");
            chooser.getExtensionFilters().add(extFilter);
            chooser.setInitialFileName(tabPane.getSelectionModel().getSelectedItem().getText());

            File file = chooser.showSaveDialog(MainData.getInstance().getStage());

            if (file != null) {
                ScriptCompiler compiler = new ScriptCompiler(file.getName());
                CodeArea script = (CodeArea) tabPane.getSelectionModel().getSelectedItem().getContent().lookup("#scriptArea");
                compiler.compile(file.toPath(), script.getText());
            }
        }
    }

    private void createTab(File input) throws Exception {
        Parent root = FXMLLoader.load(this.getClass().getResource("layout/ScriptTab.fxml"));
        if (!setStyle) {
            MainData.getInstance().getStage().getScene().getStylesheets().add(this.getClass().getResource(
                    "highlighting.css").toExternalForm());
            setStyle = true;
        }
        CodeArea currentScript = (CodeArea) root.lookup("#scriptArea");
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(currentScript);
        IntFunction<Node> numberFactory = LineNumberFactory.get(currentScript);
        currentScript.setParagraphGraphicFactory(numberFactory);
        AnchorPane pane = (AnchorPane) root.lookup("#anchorPane");
        pane.getChildren().add(vsPane);
        MainData.getInstance().getStage().getScene().widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            if (tabPane.getSelectionModel().getSelectedIndex() != -1) {
                try {
                    pane.setPrefWidth(MainData.getInstance().getStage().getScene().getWidth());
                    currentScript.setPrefWidth(MainData.getInstance().getStage().getScene().getWidth());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        MainData.getInstance().getStage().getScene().heightProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            if (tabPane.getSelectionModel().getSelectedIndex() != -1) {
                try {
                    pane.setPrefHeight(MainData.getInstance().getStage().getScene().getHeight() - 100);
                    currentScript.setPrefHeight(MainData.getInstance().getStage().getScene().getHeight() - 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        currentScript.textProperty().addListener((observable, oldValue, newValue) -> {
        });
        currentScript.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> {
                    try {
                        currentScript.setStyleSpans(0, computeHighlighting(currentScript.getText()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        if (this.getFileExtension(input).equals("cmb")) {
            ScriptDecompiler decompiler = new ScriptDecompiler();
            currentScript.appendText(decompiler.decompile(input.toPath()));
            currentScript.autosize();
        } else if (this.getFileExtension(input).equals("fscript")) {
            currentScript.appendText(readFileAsString(input.getAbsolutePath()));
        }

        Tab tab = new Tab();
        tab.setContent(root);
        tab.setText(input.getName());
        tabPane.getTabs().add(tab);

        pane.setPrefWidth(MainData.getInstance().getStage().getScene().getWidth());
        currentScript.setPrefWidth(MainData.getInstance().getStage().getScene().getWidth());
        pane.setPrefHeight(MainData.getInstance().getStage().getScene().getHeight() - 100);
        currentScript.setPrefHeight(MainData.getInstance().getStage().getScene().getHeight() - 100);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) throws Exception {
        Matcher matcher = MainData.getInstance().getPATTERN().matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("INDICATOR") != null ? "indicator" :
                                    matcher.group("RESTRICTED") != null ? "restricted" :
                                            matcher.group("PAREN") != null ? "paren" :
                                                    matcher.group("BRACE") != null ? "brace" :
                                                            matcher.group("BRACKET") != null ? "bracket" :
                                                                    matcher.group("STRING") != null ? "string" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private static String readFileAsString(String fileName) throws java.io.IOException {
        try (java.io.InputStream is = new java.io.FileInputStream(fileName)) {
            final int bufsize = 4096;
            int available = is.available();
            byte[] data = new byte[available < bufsize ? bufsize : available];
            int used = 0;
            while (true) {
                if (data.length - used < bufsize) {
                    byte[] newData = new byte[data.length << 1];
                    System.arraycopy(data, 0, newData, 0, used);
                    data = newData;
                }
                int got = is.read(data, used, data.length - used);
                if (got <= 0) break;
                used += got;
            }
            return new String(data, 0, used);
        }
    }
}
