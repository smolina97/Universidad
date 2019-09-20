package application.gui;

import application.Main;
import application.controls.HexField;
import application.data.PrefsSingleton;
import application.model.*;
import application.model.inject.InjectableFile;
import application.utils.ControlGenerator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

public class ModuleFrame extends NightmareFrame {
    private AnchorPane anchorPane;
    private VBox moduleBox;
    private Module module;
    private ComboBox entryBox;
    private List<Node> moduleComponents = new ArrayList<>();
    private InjectableFile file;

    ModuleFrame(Module module, ComboBox<String> entryBox) {
        super(module, entryBox);
        try {
            this.module = module;
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("gui/fxml/module.fxml"));
            anchorPane = fxmlLoader.load();
            moduleBox = (VBox) fxmlLoader.getNamespace().get("moduleBox");
            buildModuleBox();

            this.entryBox = entryBox;
            if (module.getEntryList() != null)
                entryBox.getItems().addAll(parseAssociatedList(module.getEntryList().getAbsolutePath(), false));
            for (int x = entryBox.getItems().size(); x < module.getDefaultEntryCount(); x++) {
                entryBox.getItems().add("0x" + Long.toHexString(x).toUpperCase());
            }
            if (module.getDefaultEntryCount() < entryBox.getItems().size()) {
                entryBox.getItems().remove(module.getDefaultEntryCount(), entryBox.getItems().size());
            }
            entryBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> updateComponents());
            file = new InjectableFile(TargetFile.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildModuleBox() {
        for (EntryComponent e : module.getComponentList()) {
            AnchorPane pane = new AnchorPane();
            pane.setPrefHeight(10);
            Label label = new Label(e.getName() + ": ");
            label.setFont(Font.font("System", FontWeight.BOLD, 15));
            label.setLayoutX(0);
            label.setLayoutY(pane.getPrefHeight() / 2);
            pane.getChildren().add(label);

            Node n = null;
            switch (e.getEntryType()) {
                case HEXA:
                    HexField field = new HexField(e.getBitCount() / 8, true);
                    n = field;
                    field.setPrefWidth(e.getBitCount() / 8 * 35);
                    field.setLayoutX(moduleBox.getPrefWidth() - field.getPrefWidth());
                    field.setLayoutY(label.getLayoutY());
                    field.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (entryBox.getSelectionModel().getSelectedIndex() == -1)
                            return;
                        TargetFile.putBytes(e.getAddress(entryBox.getSelectionModel().getSelectedIndex()), field.getValue());
                    });
                    break;
                case NEHU:
                    Spinner<Integer> hexSpinner = ControlGenerator.generateHexSpinner(e.getAccessType(0));
                    n = hexSpinner;
                    hexSpinner.setLayoutX(moduleBox.getPrefWidth() - hexSpinner.getPrefWidth());
                    hexSpinner.setLayoutY(label.getLayoutY());
                    hexSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                            processSpinnerChange(hexSpinner, e));
                    hexSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue) {
                            processSpinnerChange(hexSpinner, e);
                        }
                    });
                    break;
                case NEDS:
                    Spinner<Integer> signedSpinner = ControlGenerator.generateIntegerSpinner(true);
                    n = signedSpinner;
                    signedSpinner.setLayoutX(moduleBox.getPrefWidth() - signedSpinner.getPrefWidth());
                    signedSpinner.setLayoutY(label.getLayoutY());
                    signedSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                            processSpinnerChange(signedSpinner, e));
                    signedSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue) {
                            processSpinnerChange(signedSpinner, e);
                        }
                    });
                    break;
                case NEDU:
                    Spinner<Integer> byteSpinner = ControlGenerator.generateIntegerSpinner(false);
                    n = byteSpinner;
                    byteSpinner.setLayoutX(moduleBox.getPrefWidth() - byteSpinner.getPrefWidth());
                    byteSpinner.setLayoutY(label.getLayoutY());
                    byteSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                            processSpinnerChange(byteSpinner, e));
                    byteSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if(!newValue) {
                            processSpinnerChange(byteSpinner, e);
                        }
                    });
                    break;
                case NDHU:
                    ComboBox<String> comboBox = new ComboBox<>();
                    n = comboBox;
                    comboBox.setPrefWidth(200);
                    comboBox.getItems().addAll(parseAssociatedList(e.getAssociatedListPath(), true));
                    comboBox.setLayoutX(moduleBox.getPrefWidth() - comboBox.getPrefWidth());
                    comboBox.setLayoutY(label.getLayoutY());
                    comboBox.valueProperty().addListener((observable, oldValue, newValue) ->
                            processComboBoxChange(comboBox, e));
                    break;
                case NDDU:
                    ComboBox<String> decimalBox = new ComboBox<>();
                    n = decimalBox;
                    decimalBox.setPrefWidth(200);
                    decimalBox.getItems().addAll(parseAssociatedList(e.getAssociatedListPath(), true));
                    decimalBox.setLayoutX(moduleBox.getPrefWidth() - decimalBox.getPrefWidth());
                    decimalBox.setLayoutY(label.getLayoutY());
                    decimalBox.valueProperty().addListener((observable, oldValue, newValue) ->
                            processComboBoxChange(decimalBox, e));
                    break;
                case STRP:
                    TextField labelField = new TextField();
                    n = labelField;
                    labelField.setPrefWidth(200);
                    labelField.setLayoutX(moduleBox.getPrefWidth() - labelField.getPrefWidth());
                    labelField.setLayoutY(label.getLayoutY() - 5);
                    labelField.setOnAction(event ->
                            updatePointerField(labelField, e));
                    labelField.focusedProperty().addListener((observable, oldValue, newValue) ->
                            updatePointerField(labelField, e));
                    break;
            }
            pane.getChildren().add(n);
            AnchorPane.setRightAnchor(n, 20.0);
            moduleComponents.add(n);
            moduleBox.getChildren().add(pane);
        }
    }

    /**
     * Parse all entries from a list associated with a specific
     * drop-down. This method will also remove the first entry
     * since the first entry is always the number of entries in
     * the list. The number's still necessary for compatibility
     * with the NMM format, but the Files API doesn't need it
     * for anything.
     *
     * @param in          Path to associated file.
     * @param removeFirst Indicates whether or not to remove the first entry from the list.
     * @return A list of all entries in the associated file.
     */
    private List<String> parseAssociatedList(String in, boolean removeFirst) {
        List<String> associatedList = new ArrayList<>();
        try {
            FileInputStream input = new FileInputStream(new File(in));
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            InputStreamReader reader = new InputStreamReader(input, decoder);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while (line != null) {
                associatedList.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            if (removeFirst && associatedList.size() > 0)
                associatedList.remove(0);
        } catch (IOException e) {
            e.printStackTrace();
            return associatedList;
        }
        return associatedList;
    }

    private void updateComponents() {
        int index = entryBox.getSelectionModel().getSelectedIndex();
        if (index == -1)
            return;
        for (int x = 0; x < module.getComponentList().size(); x++) {
            EntryComponent e = module.getComponentList().get(x);
            Node node = moduleComponents.get(x);
            int address = e.getAddress(index);
            AccessType accessType = e.getAccessType(index);
            if (node instanceof HexField) { // HEXA
                HexField field = (HexField) node;
                byte[] values = TargetFile.getBytes(address, e.getBitCount() / 8);
                field.setValue(values);
            } else if (node instanceof Spinner) { // NEHU, NEDU
                Spinner<Integer> spinner = (Spinner<Integer>) node;
                if (accessType == AccessType.WORD)
                    spinner.getValueFactory().setValue(TargetFile.getInt(address));
                else if (accessType == AccessType.HALF)
                    spinner.getValueFactory().setValue((int) TargetFile.getShort(address));
                else {
                    spinner.getValueFactory().setValue((int) TargetFile.getByte(address));
                }
            } else if (node instanceof ComboBox) { // NDHU, NDDU
                ComboBox<String> box = (ComboBox<String>) node;
                int value;
                if (accessType == AccessType.WORD) {
                    value = TargetFile.getInt(address);
                } else if (accessType == AccessType.HALF) {
                    value = TargetFile.getShort(address) & 0xFFFF;
                } else if (accessType == AccessType.BYTE) {
                    value = TargetFile.getByte(address) & 0xFF;
                } else { // TODO: Better error handling please...
                    continue;
                }
                for (int y = 0; y < box.getItems().size(); y++) {
                    int target = Integer.decode(box.getItems().get(y).split(" ")[0]);
                    if (target == value) {
                        box.getSelectionModel().select(y);
                        break;
                    }
                }
            } else if (node instanceof TextField) { // STRP
                TextField field = (TextField) node;
                int value = 0;
                if (accessType == AccessType.WORD) {
                    value = TargetFile.getInt(address);
                } else if (accessType == AccessType.HALF) {
                    value = TargetFile.getShort(address) & 0xFFFF;
                } else if (accessType == AccessType.BYTE) {
                    value = TargetFile.getByte(address) & 0xFF;
                }
                if (value == 0) {
                    field.setText("NULL");
                    continue;
                }
                try {
                    field.setText(TargetFile.getString(value + 0x20));
                } catch (UnsupportedEncodingException e1) {
                    field.setText("ERROR");
                    e1.printStackTrace();
                }
            }
        }
    }

    void copyData(int source, int dest) {
        for (EntryComponent e : module.getComponentList()) {
            if (PrefsSingleton.getInstance().isCopyRaw()) {
                if (e.getEntryType() == EntryType.STRP)
                    continue;
            }
            switch (e.getAccessType(source)) {
                case BYTE:
                    TargetFile.putByte(e.getAddress(dest), TargetFile.getByte(e.getAddress(source)));
                    break;
                case HALF:
                    TargetFile.putShort(e.getAddress(dest), TargetFile.getShort(e.getAddress(source)));
                    break;
                case WORD:
                    TargetFile.putInt(e.getAddress(dest), TargetFile.getInt(e.getAddress(source)));
                    break;
            }
        }
    }

    private void processSpinnerChange(Spinner<Integer> spinner, EntryComponent e) {
        int index = entryBox.getSelectionModel().getSelectedIndex();
        if (index == -1)
            return;
        int value = Integer.decode(spinner.getEditor().getText());
        if (e.getAccessType(index) == AccessType.BYTE)
            TargetFile.putByte(e.getAddress(index), (byte) value);
        else if (e.getAccessType(index) == AccessType.HALF)
            TargetFile.putShort(e.getAddress(index), (short) value);
        else
            TargetFile.putInt(e.getAddress(index), value);
    }

    private void processComboBoxChange(ComboBox<String> comboBox, EntryComponent e) {
        int index = entryBox.getSelectionModel().getSelectedIndex();
        int value = Integer.decode(comboBox.getSelectionModel().getSelectedItem().split(" ")[0]);
        if (e.getAccessType(index) == AccessType.BYTE)
            TargetFile.putByte(e.getAddress(index), (byte) value);
        else if (e.getAccessType(index) == AccessType.HALF)
            TargetFile.putShort(e.getAddress(index), (short) value);
        else
            TargetFile.putInt(e.getAddress(index), value);
    }

    private void updatePointerField(TextField field, EntryComponent e) {
        int index = entryBox.getSelectionModel().getSelectedIndex();
        if (index == -1)
            return;
        int offset = e.getAddress(index) - 0x20;
        if (file.containsPointerOne(offset)) {
            file.repoint(field.getText(), offset);
            TargetFile.setData(file.toBin());
        } else {
            file.addPointer(offset, field.getText());
            TargetFile.setData(file.toBin());
        }
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }
}
