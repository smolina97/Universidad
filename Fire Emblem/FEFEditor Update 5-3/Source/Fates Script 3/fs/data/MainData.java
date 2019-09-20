package fs.data;

import javafx.stage.Stage;

import java.util.regex.Pattern;

public class MainData {
    private static MainData instance;

    private Stage stage;

    private final String[] KEYWORDS = new String[]{
            "Header", "Subheader", "Event", "storein", "call",
            "int", "string", "byte", "end", "short", "checkval",
            "raw", "null", "routine", "coord"
    };

    private final String[] INDICATORS = new String[]{
            "pass", "fail", "specialCheck", "goto", "unknownCheck",
            "comparePrevious", "exists"
    };

    private final String[] RESTRICTED = new String[]{
            "omit", "followFailure", "reduce"
    };

    private final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private final String INDICATOR_PATTERN = "\\b(" + String.join("|", INDICATORS) + ")\\b";
    private final String RESTRICTED_PATTERN = "\\b(" + String.join("|", RESTRICTED) + ")\\b";
    private final String PAREN_PATTERN = "[()]";
    private final String BRACE_PATTERN = "[{}]";
    private final String BRACKET_PATTERN = "[\\[]]";
    private final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

    private final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<INDICATOR>" + INDICATOR_PATTERN + ")"
                    + "|(?<RESTRICTED>" + RESTRICTED_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
    );

    private MainData() { }

    public static MainData getInstance() throws Exception {
        if (instance == null)
            instance = new MainData();
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Pattern getPATTERN() {
        return PATTERN;
    }
}
