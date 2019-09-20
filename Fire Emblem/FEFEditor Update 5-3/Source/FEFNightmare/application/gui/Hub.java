package application.gui;

import application.Main;
import application.data.Gui;
import application.data.PrefsSingleton;
import application.model.Module;
import application.model.TargetFile;
import application.utils.FileUtils;
import application.utils.FxUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Hub implements Initializable {
    @FXML
    private ListView<String> moduleList;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox<String> entryBox;

    @FXML
    private MenuItem openModuleItem;
    @FXML
    private MenuItem saveItem;
    @FXML
    private MenuItem saveAsItem;
    @FXML
    private MenuItem closeItem;
    @FXML
    private MenuItem copyToItem;
    @FXML
    private RadioMenuItem sortByNameItem;
    @FXML
    private RadioMenuItem sortByCategoryItem;

    private HashMap<String, Module> modules = new HashMap<>();
    private List<String> categories = new ArrayList<>();
    private NightmareFrame frame;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add modules from the module path in the config.
        refreshModuleList();

        // Initialize listeners.
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            if (frame != null)
                frame.getAnchorPane().setPrefWidth(newValue.doubleValue());
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            if (frame != null)
                frame.getAnchorPane().setPrefHeight(newValue.doubleValue());
        };
        scrollPane.widthProperty().addListener(widthListener);
        scrollPane.heightProperty().addListener(heightListener);

        moduleList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                entryBox.getSelectionModel().clearSelection();
                entryBox.getItems().clear();
                if (copyToItem.isDisable())
                    copyToItem.setDisable(false);
                Module module = new Module(modules.get(moduleList.getSelectionModel().getSelectedItem())
                        .getModuleFile(), true);
                if (module.isInjector()) {
                    frame = new InjectionFrame(module, entryBox);
                } else {
                    frame = new ModuleFrame(module, entryBox);
                }
                scrollPane.setContent(frame.getAnchorPane());
                frame.getAnchorPane().setPrefWidth(anchorPane.getWidth());
            }
        });
    }

    @FXML
    private void open() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(Gui.getInstance().getStage());

        if (file != null) {
            if (TargetFile.isOpen())
                TargetFile.closeFile();
            else
                toggleButtons();
            TargetFile.open(file);
        }
    }

    @FXML
    private void save() {
        if (TargetFile.isOpen()) {
            TargetFile.save();
        }
    }

    @FXML
    private void saveAs() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(Gui.getInstance().getStage());

        if (file != null) {
            TargetFile.save(file);
        }
    }

    @FXML
    private void close() {
        toggleButtons();
        TargetFile.closeFile();
        scrollPane.setContent(null);
    }

    @FXML
    private void openAbout() {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            Parent root = FXMLLoader.load(Main.class.getResource("gui/fxml/about.fxml"));
            Scene scene = new Scene(root, 350, 220);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void openModule() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Nightmare Modules (*.nmm)", "*.nmm"));
        File file = chooser.showOpenDialog(Gui.getInstance().getStage());

        if (file != null) {
            entryBox.getSelectionModel().clearSelection();
            entryBox.getItems().clear();
            Module module = new Module(file, true);
            if (module.isInjector()) {
                frame = new InjectionFrame(module, entryBox);
            } else {
                frame = new ModuleFrame(module, entryBox);
            }
            scrollPane.setContent(frame.getAnchorPane());
            frame.getAnchorPane().setPrefWidth(scrollPane.getPrefWidth());
            frame.getAnchorPane().setPrefHeight(scrollPane.getPrefHeight());
        }
    }

    @FXML
    private void openOptions() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Options");
            Parent root = FXMLLoader.load(Main.class.getResource("gui/fxml/options.fxml"));
            Scene scene = new Scene(root, 390, 175);
            scene.getStylesheets().add(Main.class.getResource("gui/jmetro/JMetroLightTheme.css").toExternalForm());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("assets/fefnightmare.png")));
            stage.setOnCloseRequest(e -> refreshModuleList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void copyTo() {
        if (entryBox.getSelectionModel().getSelectedIndex() == -1)
            return;
        List<String> choices = new ArrayList<>();
        choices.addAll(entryBox.getItems());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(entryBox.getItems().get(0), choices);
        dialog.setTitle("Select Block");
        dialog.setHeaderText("Copy current block data to...");
        dialog.setContentText("Select Block:");

        Optional<String> result = dialog.showAndWait();
        ModuleFrame frame = (ModuleFrame) this.frame;
        result.ifPresent(str -> frame.copyData(entryBox.getSelectionModel().getSelectedIndex(), choices.indexOf(str)));
    }

    @FXML
    private void sortModules() {
        if (sortByNameItem.isSelected()) {
            Collections.sort(moduleList.getItems());
        } else if (sortByCategoryItem.isSelected()) {
            moduleList.getItems().clear();
            for (String s : categories) {
                for (String m : modules.keySet()) {
                    if (modules.get(m).getCategory().equals(s))
                        moduleList.getItems().add(m);
                }
            }
        }
    }

    @FXML
    private void filter() {
        if(categories.size() == 0)
            return;
        List<String> choices = new ArrayList<>();
        choices.addAll(categories);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(categories.get(0), choices);
        dialog.setTitle("Filter by Category");
        dialog.setHeaderText("Select a category to view.");
        dialog.setContentText("Select Category:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(str -> {
            moduleList.getItems().clear();
            for (String m : modules.keySet()) {
                if (modules.get(m).getCategory().equals(str))
                    moduleList.getItems().add(m);
            }
        });
    }

    public void toggleButtons() {
        closeItem.setDisable(!closeItem.isDisable());
        saveItem.setDisable(!saveItem.isDisable());
        saveAsItem.setDisable(!saveAsItem.isDisable());
        moduleList.setDisable(!moduleList.isDisable());
        openModuleItem.setDisable(!openModuleItem.isDisable());
    }

    private void refreshModuleList() {
        moduleList.getItems().clear();
        modules.clear();
        for (File f : FileUtils.listf(PrefsSingleton.getInstance().getModulePath())) {
            if (moduleList.getItems().contains(f.getName())) {
                moduleList.getItems().add(f.getParent() + "\\" + f.getName());
                modules.put(f.getParent() + "\\" + f.getName(), new Module(f, false));
            } else {
                moduleList.getItems().add(f.getName());
                modules.put(f.getName(), new Module(f, false));
            }
        }
        if (PrefsSingleton.getInstance().isCopyRaw()) {
            Collections.sort(moduleList.getItems());
        }
        listCategories();
        sortModules();
    }

    private void listCategories() {
        categories.clear();
        for (String s : moduleList.getItems()) {
            String category = modules.get(s).getCategory();
            if (!categories.contains(category) && !category.equals("NULL")) {
                categories.add(category);
            }
        }
        categories.add("NULL");
        Collections.sort(categories);
    }
}
