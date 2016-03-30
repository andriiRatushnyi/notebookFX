package baobab.notebookfx.utils;

public class UtilHtml {

    private static final String CSS_MARKDOWN = UtilHtml.class.getResource("/css/markdownpad-github.css").toExternalForm();

    private static final String CSS_HIGHLITE = UtilHtml.class.getResource("/css/themes/github.css").toExternalForm();

    private static final String JS_HIGHLITE = UtilHtml.class.getResource("/js/highlight.min.js").toExternalForm();

    private static final String HEADER = "<html><head>\n"
            + "<meta charset=\"UTF-8\">\n"
            + "<link rel=\"stylesheet\" href=\"" + CSS_MARKDOWN + "\">\n"
            + "<link rel=\"stylesheet\" href=\"" + CSS_HIGHLITE + "\">\n"
            + "<script src=\"" + JS_HIGHLITE + "\"></script>\n"
            + "<script>hljs.initHighlightingOnLoad();</script>\n"
            + "</head><body>\n";

    private static final String FOOTER = "<script type=\"text/javascript\" src=\"https://getfirebug.com/firebug-lite.js\"></script></body></html>\n";

    public static String parse(String value) {
        return HEADER + UtilPegDownProcessor.parse(value) + FOOTER;
    }

}
