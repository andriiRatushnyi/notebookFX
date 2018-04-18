package org.markdownwriterfx.preview;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.NodeVisitor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javafx.concurrent.Worker.State;
import javafx.scene.control.IndexRange;
import javafx.scene.web.WebView;
import org.markdownwriterfx.preview.MarkdownPreviewPane.PreviewContext;
import org.markdownwriterfx.preview.MarkdownPreviewPane.Renderer;

class WebViewPreview implements MarkdownPreviewPane.Preview {

    private static final String DEBUG = "<script type=\"text/javascript\" src=\"https://getfirebug.com/firebug-lite.js\"></script></body></html>\n";
    private static final HashMap<String, String> prismLangDependenciesMap = new HashMap<>();

    private WebView webView;
    private final ArrayList<Runnable> runWhenLoadedList = new ArrayList<>();
    private int lastScrollX;
    private int lastScrollY;
    private IndexRange lastEditorSelection;

    WebViewPreview() {
    }

    private void createNodes() {
        webView = new WebView();
        webView.setFocusTraversable(false);

        // disable WebView default drag and drop handler to allow dropping markdown files
        webView.setOnDragEntered(null);
        webView.setOnDragExited(null);
        webView.setOnDragOver(null);
        webView.setOnDragDropped(null);
        webView.setOnDragDetected(null);
        webView.setOnDragDone(null);

        webView.getEngine().getLoadWorker().stateProperty().addListener((ob, o, n) -> {
            if (n == State.SUCCEEDED && !runWhenLoadedList.isEmpty()) {
                ArrayList<Runnable> runnables = new ArrayList<>(runWhenLoadedList);
                runWhenLoadedList.clear();

                runnables.forEach(runnable -> runnable.run());
            }
        });
    }

    private void runWhenLoaded(Runnable runnable) {
        if (webView.getEngine().getLoadWorker().isRunning()) {
            runWhenLoadedList.add(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public javafx.scene.Node getNode() {
        if (webView == null) {
            createNodes();
        }
        return webView;
    }

    @Override
    public void update(PreviewContext context, Renderer renderer) {
        if (!webView.getEngine().getLoadWorker().isRunning()) {
            // get window.scrollX and window.scrollY from web engine,
            // but only if no worker is running (in this case the result would be zero)
            Object scrollXobj = webView.getEngine().executeScript("window.scrollX");
            Object scrollYobj = webView.getEngine().executeScript("window.scrollY");
            lastScrollX = (scrollXobj instanceof Number) ? ((Number) scrollXobj).intValue() : 0;
            lastScrollY = (scrollYobj instanceof Number) ? ((Number) scrollYobj).intValue() : 0;
        }
        lastEditorSelection = context.getEditorSelection();

//        Path path = context.getPath();
//        String base = (path != null)
//                ? ("<base href=\"" + path.getParent().toUri().toString() + "\">\n")
//                : "";
        String scrollScript = (lastScrollX > 0 || lastScrollY > 0)
                ? ("  onload='window.scrollTo(" + lastScrollX + ", " + lastScrollY + ");'")
                : "";

        webView.getEngine().loadContent(
                "<!DOCTYPE html>\n"
                //+ "<html debug=\"true\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<link rel=\"stylesheet\" href=\"" + getClass().getResource("markdownpad-github.css") + "\">\n"
                + "<style>\n"
                + ":root {\n"
                + "  --main-bg-color: #eee;\n"
                + "  --antlength: 15px;\n"
                + "  --antwidth: 2px;\n"
                + "  --antcolor: lightgray;\n"
                + "}\n"
                + "@keyframes marching-ants {\n"
                + "  0%   {background-position: 0 0, var(--antlength) 100%, 0 var(--antlength), 100% 0;}\n"
                + "  100% {background-position: var(--antlength) 0, 0 100%, 0 0, 100% var(--antlength);}\n"
                + "}\n"
                + ".mwfx-editor-selection {\n"
                + "  background-color: var(--main-bg-color);\n"
                + "  box-shadow: inset 0 0 0 1px var(--antcolor), 0 0 0 1px var(--antcolor);\n"
                + "  background-image: linear-gradient(90deg, var(--antcolor) 50%, transparent 50%),\n"
                + "    linear-gradient(90deg, var(--antcolor) 50%, transparent 50%),\n"
                + "    linear-gradient(0, var(--antcolor) 50%, transparent 50%),\n"
                + "    linear-gradient(0, var(--antcolor) 50%, transparent 50%);\n"
                + "  background-repeat: repeat-x, repeat-x, repeat-y, repeat-y;\n"
                + "  background-size: var(--antlength) var(--antwidth), var(--antlength) var(--antwidth), var(--antwidth) var(--antlength), var(--antwidth) var(--antlength);\n"
                + "  animation: marching-ants 2000ms infinite linear;\n"
                + "}\n"
                + "</style>\n"
                + "<script src=\"" + getClass().getResource("preview.js") + "\"></script>\n"
                + prismSyntaxHighlighting(context.getMarkdownAST())
                //+ base
                + "</head>\n"
                + "<body" + scrollScript + ">\n"
                + renderer.getHtml(false)
                + "<script>" + highlightNodesAt(lastEditorSelection) + "</script>\n"
                //+ DEBUG
                + "</body>\n"
                + "</html>");
    }

    @Override
    public void scrollY(PreviewContext context, double value) {
        runWhenLoaded(() -> webView.getEngine().executeScript("preview.scrollTo(" + value + ");"));
    }

    @Override
    public void editorSelectionChanged(PreviewContext context, IndexRange range) {
        if (range.equals(lastEditorSelection)) {
            return;
        }
        lastEditorSelection = range;

        runWhenLoaded(() -> webView.getEngine().executeScript(highlightNodesAt(range)));
    }

    private String highlightNodesAt(IndexRange range) {
        return "preview.highlightNodesAt(" + range.getEnd() + ")";
    }

    private String prismSyntaxHighlighting(Node astRoot) {
        initPrismLangDependencies();

        // check whether markdown contains fenced code blocks and remember languages
        ArrayList<String> languages = new ArrayList<>();
        NodeVisitor visitor = new NodeVisitor(Collections.emptyList()) {
            @Override
            public void visit(Node node) {
                if (node instanceof FencedCodeBlock) {
                    String language = ((FencedCodeBlock) node).getInfo().toString();
                    if (language.contains(language)) {
                        languages.add(language);
                    }

                    // dependencies
                    while ((language = prismLangDependenciesMap.get(language)) != null) {
                        if (language.contains(language)) {
                            languages.add(0, language); // dependencies must be loaded first
                        }
                    }
                } else {
                    visitChildren(node);
                }
            }
        };
        visitor.visit(astRoot);

        if (languages.isEmpty()) {
            return "";
        }

        // build HTML (only load used languages)
        // Note: not using Prism Autoloader plugin because it lazy loads/highlights, which causes flicker
        //       during fast typing; it also does not work with "alias" languages (e.g. js, html, xml, svg, ...)
        StringBuilder buf = new StringBuilder();
        buf.append("<link rel=\"stylesheet\" href=\"").append(getClass().getResource("prism/prism.css")).append("\">\n");
        buf.append("<script src=\"").append(getClass().getResource("prism/prism-core.min.js")).append("\"></script>\n");
        for (String language : languages) {
            URL url = getClass().getResource("prism/components/prism-" + language + ".min.js");
            if (url != null) {
                buf.append("<script src=\"").append(url).append("\"></script>\n");
            }
        }
        return buf.toString();
    }

    /**
     * load and parse prism/lang_dependencies.txt
     */
    private static void initPrismLangDependencies() {
        if (!prismLangDependenciesMap.isEmpty()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                WebViewPreview.class.getResourceAsStream("prism/lang_dependencies.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("{")) {
                    continue;
                }

                line = line.replaceAll("(\\[.+),(.+\\])", "$1;$2");
                line = trimDelim(line, "{", "}");
                for (String str : line.split(",")) {
                    String[] parts = str.split(":");
                    if (parts[1].startsWith("[")) {
                        continue; // not supported
                    }
                    String key = trimDelim(parts[0], "\"", "\"");
                    String value = trimDelim(parts[1], "\"", "\"");
                    prismLangDependenciesMap.put(key, value);
                }
            }
        } catch (IOException ex) {
            // ignore
        }
    }

    private static String trimDelim(String str, String leadingDelim, String trailingDelim) {
        str = str.trim();
        if (!str.startsWith(leadingDelim) || !str.endsWith(trailingDelim)) {
            throw new IllegalArgumentException(str);
        }
        return str.substring(leadingDelim.length(), str.length() - trailingDelim.length());
    }
}
