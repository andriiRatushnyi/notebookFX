package org.markdownwriterfx.util;

import java.util.prefs.Preferences;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A integer property that loads/saves its value from/to preferences.
 *
 * @author Karl Tauber
 */
public class PrefsIntegersProperty extends SimpleObjectProperty<Integer[]> {

    public PrefsIntegersProperty() {
    }

    public PrefsIntegersProperty(Preferences prefs, String key) {
        init(prefs, key);
    }

    public void init(Preferences prefs, String key) {
        set(Utils.getPrefsInts(prefs, key));
        addListener((ob, o, n) -> {
            Utils.putPrefsInts(prefs, key, get());
        });
    }
}
