package org.markdownwriterfx.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author Karl Tauber
 */
public class Messages {

    private static final String BUNDLE_NAME = "org.markdownwriterfx.messages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String get(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    public static String get(String key, Object... args) {
        return MessageFormat.format(get(key), args);
    }
}
