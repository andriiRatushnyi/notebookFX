package baobab.notebookfx.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckImage {

    private static final String IMAGE_EXTENSIONS = "(^[\\s\\S]+(\\.(?i)(jpg|jpeg|png|gif|svg))$)";
    private static final Pattern PATTERN_EXTENSIONS = Pattern.compile(IMAGE_EXTENSIONS);

    public static boolean check(String imageName) {
        return PATTERN_EXTENSIONS.matcher(imageName).matches();
    }

    private static final String IMAGE_IDENTIFIER = "IMG#(.+?)#IMG";
    private static final Pattern PATTERN_IDENTIFIER = Pattern.compile(IMAGE_IDENTIFIER);

    public static String getImageIdentifier(String content) {
        Matcher matcher =  PATTERN_IDENTIFIER.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
