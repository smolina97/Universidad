package application.utils;

import javafx.util.StringConverter;

public class HexStringConverter extends StringConverter {

    @Override
    public String toString(Object input) {
        int i = (int) input;
        return "" + i; // Let the spinner determine how to convert based off of access type.
    }

    @Override
    public Integer fromString(String string) {
        if(string.startsWith("0x"))
            return Integer.decode(string);
        else {
            try {
                return Integer.parseInt(string, 16);
            } catch(NumberFormatException ex) {
                return 0;
            }
        }
    }
}
