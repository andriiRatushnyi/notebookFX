
package org.markdownwriterfx.util;

import java.util.prefs.Preferences;
import javafx.beans.property.SimpleLongProperty;

/**
 * A integer property that loads/saves its value from/to preferences.
 *
 * @author Karl Tauber
 */
public class PrefsLongProperty extends SimpleLongProperty {

    public PrefsLongProperty() {
    }

    public PrefsLongProperty(Preferences prefs, String key, long def) {
        init(prefs, key, def);
    }

    public void init(Preferences prefs, String key, long def) {
        set(prefs.getLong(key, def));
        addListener((ob, o, n) -> {
            Utils.putPrefsLong(prefs, key, get(), def);
        });
    }
}
