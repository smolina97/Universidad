package application.gui;

import application.Main;
import application.controls.HexField;
import application.model.*;
import application.model.inject.InjectableFile;
import application.model.inject.InjectionData;
import application.utils.ControlGenerator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

class InjectionFrame extends NightmareFrame {
    private AnchorPane anchorPane;
    private VBox moduleBox;
    private Module module;
    private InjectionData injectionData;
    private InjectableFile file;
    private int entryCount;

    private List<Pair<Node, EntryComponent>> nodes;
    private List<Node> associatedNodes;

    InjectionFrame(Module module, ComboBox<String> entryBox) {
        super(module, entryBox);
        try {
            associatedNodes = new ArrayList<>();
            this.module = module;
            injectionData = new InjectionData(module.getEntrySize());
            injectionData.setPointerTwo(module.isPointerTwo());
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("gui/fxml/module.fxml"));
            anchorPane = fxmlLoader.load();
            moduleBox = (VBox) fxmlLoader.getNamespace().get("moduleBox");
            buildModuleBox();

            file = new InjectableFile(TargetFile.getData());
            entryCount = module.getDefaultEntryCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    AnchorPane getAnchorPane() {
        return anchorPane;
    }

    private void buildModuleBox() {
        nodes = new ArrayList<>();
        for(EntryComponent e : module.getComponentList()) {
            AnchorPane pane = new AnchorPane();
            pane.setPrefHeight(10);
            Label label = new Label(e.getName() + ": ");
            label.setFont(Font.font("System", FontWeight.BOLD, 15));
            pane.getChildren().add(label);
            label.setLayoutX(0);
            label.setLayoutY(pane.getPrefHeight() / 2);

            switch(e.getEntryType()) {
                case TEXT:
                    TextField textField = new TextField("NULL");
                    pane.getChildren().add(textField);
                    textField.setLayoutX(moduleBox.getPrefWidth() - textField.getPrefWidth());
                    textField.setLayoutY(label.getLayoutY());
                    AnchorPane.setRightAnchor(textField, 20.0);
                    if(e.isPointer()) {
                        injectionData.getLabels().add(textField.getText());
                        int labelIndex = injectionData.getLabels().size() - 1;
                        textField.textProperty().addListener((observable, oldValue, newValue) ->
                                injectionData.getLabels().set(labelIndex, newValue));
                    }
                    if(e.getDefaultValue() != null)
                        textField.setText((String) e.getDefaultValue());
                    break;
                case HEXA:
                    HexField field = new HexField(e.getBitCount() / 8, true);
                    field.setPrefWidth(e.getBitCount() / 8 * 30);
                    pane.getChildren().add(field);
                    field.setLayoutX(moduleBox.getPrefWidth() - field.getPrefWidth());
                    field.setLayoutY(label.getLayoutY());
                    AnchorPane.setRightAnchor(field, 20.0);
                    field.textProperty().addListener((observable, oldValue, newValue) ->
                            injectionData.putBytes(e.getOffset(), field.getValue()));
                    if(e.getDefaultValue() != null)
                        field.setValue((byte[]) e.getDefaultValue());
                    break;
                case NEHU:
                    Spinner<Integer> hexSpinner = ControlGenerator.generateHexSpinner(e.getAccessType(0));
                    pane.getChildren().add(hexSpinner);
                    hexSpinner.setLayoutX(moduleBox.getPrefWidth() - hexSpinner.getPrefWidth());
                    hexSpinner.setLayoutY(label.getLayoutY());
                    AnchorPane.setRightAnchor(hexSpinner, 20.0);
                    hexSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if(e.getAccessType(0) == AccessType.BYTE)
                            injectionData.putByte(e.getOffset(), hexSpinner.getValue().byteValue());
                        else if(e.getAccessType(0) == AccessType.HALF)
                            injectionData.putShort(e.getOffset(), hexSpinner.getValue().shortValue());
                        else
                            injectionData.putInt(e.getOffset(), hexSpinner.getValue());
                    });
                    if(e.getDefaultValue() != null)
                        hexSpinner.getValueFactory().setValue((int) e.getDefaultValue());
                    break;
                case NEDS:
                    Spinner<Integer> signedSpinner = ControlGenerator.generateIntegerSpinner(true);
                    pane.getChildren().add(signedSpinner);
                    signedSpinner.setLayoutX(moduleBox.getPrefWidth() - signedSpinner.getPrefWidth());
                    signedSpinner.setLayoutY(label.getLayoutY());
                    AnchorPane.setRightAnchor(signedSpinner, 20.0);
                    signedSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if(e.getAccessType(0) == AccessType.BYTE)
                            injectionData.putByte(e.getOffset(), signedSpinner.getValue().byteValue());
                        else if(e.getAccessType(0) == AccessType.HALF)
                            injectionData.putShort(e.getOffset(), signedSpinner.getValue().shortValue());
                        else
                            injectionData.putInt(e.getOffset(), signedSpinner.getValue());
                    });
                    if(e.getDefaultValue() != null)
                        signedSpinner.getValueFactory().setValue((int) e.getDefaultValue());
                    break;
                case NEDU:
                    Spinner<Integer> byteSpinner = ControlGenerator.generateIntegerSpinner(false);
                    pane.getChildren().add(byteSpinner);
                    byteSpinner.setLayoutX(moduleBox.getPrefWidth() - byteSpinner.getPrefWidth());
                    byteSpinner.setLayoutY(label.getLayoutY());
                    AnchorPane.setRightAnchor(byteSpinner, 20.0);
                    byteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if(e.getAccessType(0) == AccessType.BYTE)
                            injectionData.putByte(e.getOffset(), byteSpinner.getValue().byteValue());
                        else if(e.getAccessType(0) == AccessType.HALF)
                            injectionData.putShort(e.getOffset(), byteSpinner.getValue().shortValue());
                        else
                            injectionData.putInt(e.getOffset(), byteSpinner.getValue());
                    });
                    if(e.getDefaultValue() != null)
                        byteSpinner.getValueFactory().setValue((int) e.getDefaultValue());
                    break;
                case NDHU:
                    ComboBox<String> comboBox = new ComboBox<>();
                    comboBox.setPrefWidth(200);
                    comboBox.getItems().addAll(parseAssociatedList(e.getAssociatedListPath()));
                    pane.getChildren().add(comboBox);
                    comboBox.setLayoutX(moduleBox.getPrefWidth() - comboBox.getPrefWidth());
                    comboBox.setLayoutY(label.getLayoutY());
                    AnchorPane.setRightAnchor(comboBox, 20.0);
                    comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                        int value = Integer.decode(comboBox.getSelectionModel().getSelectedItem().split(" ")[0]);
                        if(e.getAccessType(0) == AccessType.BYTE)
                            injectionData.putByte(e.getOffset(), (byte) value);
                        else if(e.getAccessType(0) == AccessType.HALF)
                            injectionData.putShort(e.getOffset(), (short) value);
                        else
                            injectionData.putInt(e.getOffset(), value);
                    });
                    if(e.getDefaultValue() != null)
                        comboBox.getSelectionModel().select((int) e.getDefaultValue());
                    comboBox.getSelectionModel().select(0);
                    break;
                case NDDU:
                    ComboBox<String> decimalBox = new ComboBox<>();
                    decimalBox.setPrefWidth(200);
                    decimalBox.getItems().addAll(parseAssociatedList(e.getAssociatedListPath()));
                    pane.getChildren().add(decimalBox);
                    decimalBox.setLayoutX(moduleBox.getPrefWidth() - decimalBox.getPrefWidth());
                    decimalBox.setLayoutY(label.getLayoutY());
                    AnchorPane.setRightAnchor(decimalBox, 20.0);
                    decimalBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                        int value = Integer.decode(decimalBox.getSelectionModel().getSelectedItem().split(" ")[0]);
                        if(e.getAccessType(0) == AccessType.BYTE)
                            injectionData.putByte(e.getOffset(), (byte) value);
                        else if(e.getAccessType(0) == AccessType.HALF)
                            injectionData.putShort(e.getOffset(), (short) value);
                        else
                            injectionData.putInt(e.getOffset(), value);
                    });
                    if(e.getDefaultValue() != null)
                        decimalBox.getSelectionModel().select((int) e.getDefaultValue());
                    decimalBox.getSelectionModel().select(0);
                    break;
            }
            moduleBox.getChildren().add(pane);

            if(e.isPointer()) {
                associatedNodes.add(pane.getChildren().get(pane.getChildren().size() - 1));
                injectionData.getPointers().add(e.getOffset());
                if(injectionData.getPointers().size() > injectionData.getLabels().size())
                    injectionData.getLabels().add("NULL");
            }
            nodes.add(new Pair<>(pane.getChildren().get(pane.getChildren().size() - 1), e));
        }

        Pane pane = new Pane();
        pane.setPrefHeight(10);
        moduleBox.getChildren().add(pane);

        Button button = new Button("Inject");
        button.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        button.setPrefWidth(moduleBox.getPrefWidth());
        anchorPane.widthProperty().addListener((observable, oldValue, newValue) -> button.setPrefWidth(newValue.doubleValue() - 30));
        button.setOnAction(event -> inject());
        moduleBox.getChildren().add(button);
    }

    /**
     * Parse all entries from a list associated with a specific
     * drop-down. This method will also remove the first entry
     * since the first entry is always the number of entries in
     * the list. The number's still necessary for compatibility
     * with the NMM format, but the Files API doesn't need it
     * for anything.
     *
     * @param in Path to associated file.
     * @return A list of all entries in the associated file.
     */
    private List<String> parseAssociatedList(String in) {
        List<String> associatedList = new ArrayList<>();
        try {
            FileInputStream input = new FileInputStream(new File(in));
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            InputStreamReader reader = new InputStreamReader(input, decoder);
            BufferedReader bufferedReader = new BufferedReader( reader );
            String line = bufferedReader.readLine();
            while( line != null ) {
                associatedList.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            if(associatedList.size() > 0)
                associatedList.remove(0);
        } catch (IOException e) {
            e.printStackTrace();
            return associatedList;
        }
        return associatedList;
    }

    private void inject() {
        int injectAddress = module.getBaseAddress() + entryCount * module.getEntrySize() - 0x20;

        // Copy the label and pointer list so that we can restore them after injection.
        List<String> savedLabels = new ArrayList<>();
        List<Integer> savedPointers = new ArrayList<>();
        savedLabels.addAll(injectionData.getLabels());
        savedPointers.addAll(injectionData.getPointers());

        for(int x = 0; x < injectionData.getLabels().size(); x++) {
            if(associatedNodes.get(x) instanceof TextField) {
                if(injectionData.getLabels().get(x).equals("NULL")) {
                    associatedNodes.remove(x);
                    injectionData.getLabels().remove(x);
                    injectionData.getPointers().remove(x);
                    x--;
                }
            }
        }
        file.inject(injectionData, injectAddress);
        for(String s : injectionData.getLabels()) {
            System.out.println(s);
        }
        TargetFile.setData(file.toBin());
        entryCount++;
        switch (module.getCountLength()) {
            case -1:
                break;
            case 1:
                TargetFile.putByte(module.getCountAddress(), (byte) entryCount);
                break;
            case 2:
                TargetFile.putShort(module.getCountAddress(), (short) entryCount);
                break;
            case 4:
                TargetFile.putInt(module.getCountAddress(), entryCount);
                break;
        }

        injectionData.setLabels(savedLabels);
        injectionData.setPointers(savedPointers);

        // Reset controls.
        for(Pair<Node, EntryComponent> p : nodes) {
            if(p.getFirst() instanceof HexField) {
                HexField field = (HexField) p.getFirst();
                if(p.getSecond().getDefaultValue() != null) {
                    field.setValue((byte[])p.getSecond().getDefaultValue());
                }
            }
            else if(p.getFirst() instanceof TextField) {
                TextField field = (TextField) p.getFirst();
                if(p.getSecond().getDefaultValue() != null) {
                    field.setText((String) p.getSecond().getDefaultValue());
                }
                else {
                    field.setText("NULL");
                }
            }
            else if(p.getFirst() instanceof ComboBox) {
                ComboBox box = (ComboBox) p.getFirst();
                if(p.getSecond().getDefaultValue() != null) {
                    box.getSelectionModel().select(p.getSecond().getDefaultValue());
                }
                else {
                    box.getSelectionModel().select(0);
                }
            }
            else if(p.getFirst() instanceof Spinner) {
                Spinner spinner = (Spinner) p.getFirst();
                if(p.getSecond().getDefaultValue() != null) {
                    spinner.getValueFactory().setValue(p.getSecond().getDefaultValue());
                }
                else {
                    spinner.getValueFactory().setValue(0);
                }
            }
        }
    }
}
