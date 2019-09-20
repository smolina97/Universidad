package application.data;

import application.Main;

import java.util.prefs.Preferences;

public class PrefsSingleton {
    private static PrefsSingleton instance;

    private static final String COMPRESSION_ID = "COMP_FILES";
    private static final String SORTING_ID = "SORT_MODULES";
    private static final String MODULE_PATH_ID = "MODULE_PATH";

    private Preferences prefs;
    private boolean compressionEnabled;
    private boolean copyRaw;
    private String modulePath;

    private PrefsSingleton() {
        prefs = Preferences.userRoot().node(Main.class.getName());

        compressionEnabled = prefs.getBoolean(COMPRESSION_ID, true);
        copyRaw = prefs.getBoolean(SORTING_ID, true);
        modulePath = prefs.get(MODULE_PATH_ID, "");
    }

    public static PrefsSingleton getInstance() {
        if(instance == null)
            instance = new PrefsSingleton();
        return instance;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public void setCompressionEnabled(boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
        prefs.putBoolean(COMPRESSION_ID, compressionEnabled);
    }

    public boolean isCopyRaw() {
        return copyRaw;
    }

    public void setCopyRaw(boolean copyRaw) {
        this.copyRaw = copyRaw;
        prefs.putBoolean(SORTING_ID, compressionEnabled);
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
        prefs.put(MODULE_PATH_ID, modulePath);
    }
}
