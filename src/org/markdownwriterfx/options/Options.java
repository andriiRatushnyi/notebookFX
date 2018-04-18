package org.markdownwriterfx.options;

import java.util.List;
import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Font;
import org.markdownwriterfx.util.PrefsBooleanProperty;
import org.markdownwriterfx.util.PrefsIntegerProperty;
import org.markdownwriterfx.util.PrefsStringProperty;
import org.markdownwriterfx.util.PrefsStringsProperty;

public class Options {

    public static final String[] DEF_FONT_FAMILIES = {
        //"Fira Code",
        "Consolas",
        "DejaVu Sans Mono",
        "Lucida Sans Typewriter",
        "Lucida Console"
    };

    public static final int DEF_FONT_SIZE = 12;
    public static final int MIN_FONT_SIZE = 8;
    public static final int MAX_FONT_SIZE = 36;
    public static final String DEF_MARKDOWN_FILE_EXTENSIONS = "*.md,*.markdown,*.txt";
    public static final String emphasisMarker = "_";
    public static final String strongEmphasisMarker = "**";
    public static final String bulletListMarker = "*";

    public static void load(Preferences options) {
        fontFamily.init(options, "fontFamily", null, value -> safeFontFamily(value));
        fontSize.init(options, "fontSize", DEF_FONT_SIZE);
        markdownExtensions.init(options, "markdownExtensions");
        // TODO rewrite after add extension dialogs
        setMarkdownExtensions(MarkdownExtensions.ids());
        
        showLineNo.init(options, "showLineNo", true);
        showWhitespace.init(options, "showWhitespace", false);
    }

    /**
     * Check whether font family is null or invalid (family not available on
     * system) and search for an available family.
     */
    private static String safeFontFamily(String fontFamily) {
        List<String> fontFamilies = Font.getFamilies();
        if (fontFamily != null && fontFamilies.contains(fontFamily)) {
            return fontFamily;
        }

        for (String family : DEF_FONT_FAMILIES) {
            if (fontFamilies.contains(family)) {
                return family;
            }
        }

        return "Monospaced";
    }

    // 'fontFamily' property
    private static final PrefsStringProperty fontFamily = new PrefsStringProperty();

    public static String getFontFamily() {
        return fontFamily.get();
    }

    public static void setFontFamily(String fontFamily) {
        Options.fontFamily.set(fontFamily);
    }

    public static StringProperty fontFamilyProperty() {
        return fontFamily;
    }

    // 'fontSize' property
    private static final PrefsIntegerProperty fontSize = new PrefsIntegerProperty();

    public static int getFontSize() {
        return fontSize.get();
    }

    public static void setFontSize(int fontSize) {
        Options.fontSize.set(Math.min(Math.max(fontSize, MIN_FONT_SIZE), MAX_FONT_SIZE));
    }

    public static IntegerProperty fontSizeProperty() {
        return fontSize;
    }

    // 'markdownExtensions' property
    private static final PrefsStringsProperty markdownExtensions = new PrefsStringsProperty();

    public static String[] getMarkdownExtensions() {
        return markdownExtensions.get();
    }

    public static void setMarkdownExtensions(String[] markdownExtensions) {
        Options.markdownExtensions.set(markdownExtensions);
    }

    public static ObjectProperty<String[]> markdownExtensionsProperty() {
        return markdownExtensions;
    }

    // 'showLineNo' property
    private static final PrefsBooleanProperty showLineNo = new PrefsBooleanProperty();

    public static boolean isShowLineNo() {
        return showLineNo.get();
    }

    public static void setShowLineNo(boolean showLineNo) {
        Options.showLineNo.set(showLineNo);
    }

    public static BooleanProperty showLineNoProperty() {
        return showLineNo;
    }

    // 'showWhitespace' property
    private static final PrefsBooleanProperty showWhitespace = new PrefsBooleanProperty();

    public static boolean isShowWhitespace() {
        return showWhitespace.get();
    }

    public static void setShowWhitespace(boolean showWhitespace) {
        Options.showWhitespace.set(showWhitespace);
    }

    public static BooleanProperty showWhitespaceProperty() {
        return showWhitespace;
    }

}
