package org.markdownwriterfx.util;

import java.util.prefs.Preferences;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A double property that loads/saves its value from/to preferences.
 */
public class PrefsDoubleProperty extends SimpleDoubleProperty {

    public PrefsDoubleProperty() {
    }

    public PrefsDoubleProperty(Preferences prefs, String key, double def) {
        init(prefs, key, def);
    }

    public void init(Preferences prefs, String key, double def) {
        set(prefs.getDouble(key, def));
        addListener((ob, o, n) -> {
            Utils.putPrefsDouble(prefs, key, get(), def);
        });
    }
}
