package application.utils;

import application.model.AccessType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.FormatStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class ControlGenerator {
    public static Spinner<Integer> generateHexSpinner(AccessType accessType) {
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory valueFactory;
        if(accessType == AccessType.BYTE) {
            valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        else if(accessType == AccessType.HALF) {
            valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Short.MIN_VALUE, Short.MAX_VALUE);
        }
        else {
            valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        valueFactory.setConverter(new HexStringConverter());
        spinner.setValueFactory(valueFactory);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(accessType == AccessType.BYTE) {
                String hex = String.format("%02x", Integer.toUnsignedLong(newValue)).toUpperCase();
                if(hex.length() > 2)
                    hex = hex.substring(hex.length() - 2);
                spinner.getEditor().setText("0x" + hex);
            }
            else if(accessType == AccessType.HALF) {
                String hex = String.format("%04x", Integer.toUnsignedLong(newValue)).toUpperCase();
                if(hex.length() > 4)
                    hex = hex.substring(hex.length() - 4);
                spinner.getEditor().setText("0x" + hex);
            }
            else
                spinner.getEditor().setText("0x" + String.format("%08x", Integer.toUnsignedLong(newValue)).toUpperCase());
        });
        spinner.getValueFactory().setValue(0);
        spinner.setEditable(true);
        return spinner;
    }

    public static Spinner<Integer> generateIntegerSpinner(boolean signed) {
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE);
        spinner.setValueFactory(valueFactory);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(!signed)
                spinner.getEditor().setText(Integer.toUnsignedLong(newValue) + "");
        });
        spinner.getValueFactory().setValue(0);
        spinner.setEditable(true);
        return spinner;
    }
}
