package baobab.notebookfx.utils;

public class UtilEOF {

    public static String escape(String content) {
        content = content.replace("\\", "\\\\");
        content = content.replace("\"", "\\\"");
        content = content.replace("'", "\\'");
        content = content.replace(System.getProperty("line.separator"), "\\n");
        content = content.replace("\n", "\\n");
        content = content.replace("\r", "\\n");
        return content;
    }

}