package org.markdownwriterfx.util;

import java.util.prefs.Preferences;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A integer property that loads/saves its value from/to preferences.
 *
 * @author Karl Tauber
 */
public class PrefsLongsProperty extends SimpleObjectProperty<Long[]> {

    public PrefsLongsProperty() {
    }

    public PrefsLongsProperty(Preferences prefs, String key) {
        init(prefs, key);
    }

    public void init(Preferences prefs, String key) {
        set(Utils.getPrefsLongs(prefs, key));
        addListener((ob, o, n) -> {
            Utils.putPrefsLongs(prefs, key, get());
        });
    }
}
