package baobab.notebookfx.controllers;

import baobab.notebookfx.NotebookFX;
import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Source;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.CheckFile;
import baobab.notebookfx.utils.OS;
import baobab.notebookfx.utils.SpringFX;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.NodeVisitor;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javax.inject.Inject;
import netscape.javascript.JSObject;
import org.markdownwriterfx.options.MarkdownExtensions;
import org.markdownwriterfx.util.PrefsDoubleProperty;
import org.springframework.stereotype.Component;

@Component
public class ViewController implements Initializable {

    private static final String DEBUG = "<script type=\"text/javascript\" src=\"https://getfirebug.com/firebug-lite.js\"></script></body></html>\n";
    private static final HashMap<String, String> prismLangDependenciesMap = new HashMap<>();

    private static final Pattern FILE_PATTERN = Pattern.compile(
            "\\bfile://([-\\p{L}0-9+&@/%?=~_|!:,.;]*[-\\p{L}0-9+&@/%=~_|])\\s*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @Inject
    ContentManager contentManager;

    @FXML
    private BorderPane borderPane;

    @FXML
    private WebView webView;

    @FXML
    private Label title;

    private Content content;

    private WebView sourcePreviewPane;

    private TreeView<File> tree = new TreeView<>();

    private HashMap<File, TreeItem<File>> sources = new HashMap<>();

    private static final double SPLIT_PANE_DIVIDER = 0.5;

    @FXML
    private void handlerBack(ActionEvent event) throws IOException {
        Scene scene = ((Node) event.getSource()).getScene();
        Parent root = (Parent) SpringFX.getInstance().load("/fxml/Index.fxml");
        scene.setRoot(root);
    }

    @FXML
    private void handlerEdit(ActionEvent event) {
        try {
            Scene scene = ((Node) event.getSource()).getScene();
            FXMLLoader loader = SpringFX.getInstance().loader("/fxml/Edit.fxml");

            Parent root = (Parent) loader.load();
            EditController controller = loader.<EditController>getController();
            controller.initData(content.getId());

            scene.setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initData(Long pageId) {
        content = contentManager.getContent(pageId);

        content.setViewCount(content.getViewCount() + 1);
        contentManager.saveContent(content);

        title.setText(content.getTitle());
        title.setTooltip(new Tooltip(content.getTitle()));

        String contentBody = content.getContent();

        // Get all exists source file
        Set<File> fileSource = new HashSet<>();
        Matcher matcher = FILE_PATTERN.matcher(contentBody);
        while (matcher.find()) {
            String fileName = matcher.group(1);
            File file = new File(fileName);
            if (file.isFile() && file.exists()) {
                fileSource.add(file);
            }
        }

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, MarkdownExtensions.getExtensions());

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Document document = parser.parse(contentBody);
        String html = renderer.render(document);

        webView.getEngine().loadContent(
                "<!DOCTYPE html>\n"
                //+ "<html debug=\"true\">\n"
                + "<html>\n"
                + "<head>\n"
                + "<link rel=\"stylesheet\" href=\"" + getClass().getResource("/org/markdownwriterfx/preview/markdownpad-github.css") + "\">\n"
                + prismSyntaxHighlighting(document)
                + "<link rel=\"stylesheet\" href=\"" + getClass().getResource("/org/markdownwriterfx/preview/prism/plugins/prism-autolinker.css") + "\">\n"
                + "<script src=\"" + getClass().getResource("/org/markdownwriterfx/preview/prism/plugins/prism-autolinker.js") + "\"></script>\n"
                + "</head>\n"
                + "<body>\n"
                + html
                + "<script>"
                + "function link(fileArr) {\n"
                + "    [].slice.call(document.querySelectorAll(\"a[href]\"))\n"
                + "        .forEach(function(link) {\n"
                + "            var urlparams = link.href.split(\"#\");\n"
                + "            fileArr.forEach(function(urlStr) {\n"
                + "                if(urlparams[0].includes(urlStr)) {\n"
                + "                    link.href = \"#\";\n"
                + "                    link.onclick = function (event) {\n"
                + "                        java.createFileView(urlStr, (urlparams[1] ? urlparams[1]: null));\n"
                + "                        event.preventDefault();\n"
                + "                    };\n"
                + "                    link.dataset.java = \"true\";\n"
                + "                }\n"
                + "            });\n"
                + "            if(!link.dataset.java) {\n"
                + "                var parent = link.parentNode;\n"
                + "                while (link.firstChild) parent.insertBefore(link.firstChild, link);\n"
                + "                parent.removeChild(link);\n"
                + "            }\n"
                + "        });\n"
                + "};\n"
                + "</script>\n"
                //+ DEBUG
                + "</body>\n"
                + "</html>");

        if (!content.getSources().isEmpty()) {
            createFileView(fileSource);
        }

        webView.getEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                // java -> web
                StringJoiner strJoin = new StringJoiner("\",\"", "link([\"", "\"]);");
                sources.keySet()
                        .stream()
                        .map(File::getAbsolutePath)
                        .forEach(strJoin::add);

                webView.getEngine().executeScript(strJoin.toString());

                // web -> java
                JSObject win = (JSObject) webView.getEngine().executeScript("window");
                win.setMember("java", this);
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        webView.setFocusTraversable(false);

        // disable WebView default drag and drop handler to allow dropping markdown files
        webView.setOnDragEntered(null);
        webView.setOnDragExited(null);
        webView.setOnDragOver(null);
        webView.setOnDragDropped(null);
        webView.setOnDragDetected(null);
        webView.setOnDragDone(null);
    }

    // method from WebViewPreview.java
    private String prismSyntaxHighlighting(com.vladsch.flexmark.ast.Node astRoot) {
        initPrismLangDependencies();

        // check whether markdown contains fenced code blocks and remember languages
        ArrayList<String> languages = new ArrayList<>();
        NodeVisitor visitor = new NodeVisitor(Collections.emptyList()) {
            @Override
            public void visit(com.vladsch.flexmark.ast.Node node) {
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
        buf.append("<link rel=\"stylesheet\" href=\"").append(getClass().getResource("/org/markdownwriterfx/preview/prism/prism.css")).append("\">\n");
        buf.append("<script src=\"").append(getClass().getResource("/org/markdownwriterfx/preview/prism/prism-core.min.js")).append("\"></script>\n");
        languages.stream()
                .map(language -> getClass().getResource("/org/markdownwriterfx/preview/prism/components/prism-" + language + ".min.js"))
                .filter(url -> url != null)
                .forEachOrdered(url -> buf.append("<script src=\"").append(url).append("\"></script>\n"));
        return buf.toString();
    }

    /**
     * load and parse prism/lang_dependencies.txt
     */
    private void initPrismLangDependencies() {
        if (!prismLangDependenciesMap.isEmpty()) {
            return;
        }

        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream("/org/markdownwriterfx/preview/prism/lang_dependencies.txt")
                ))) {
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

    private void createFileView(Set<File> fileSource) {
        WebView webView = (WebView) borderPane.getCenter();

        TreeItem<File> root = new TreeItem<>(new File("."));

        root.getChildren().addAll(
                content.getSources().stream()
                        .map(Source::getName)
                        .map(File::new)
                        .map(file -> createTree(file, fileSource))
                        .collect(toList())
        );

        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tree.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener.Change<? extends TreeItem<File>> change) -> {
                    while (change.next()) {
                        (change.getList().stream()
                                .map(TreeItem<File>::getValue)
                                .findFirst())
                                .filter(File::isFile)
                                .ifPresent(this::createFileView);
                    }
                });
        tree.setRoot(root);
        tree.setShowRoot(false);
        tree.setCellFactory(treeView -> new TreeCell<File>() {
            private ImageView extended = new ImageView(getClass().getResource("/img/folder-open_16.png").toExternalForm());
            private ImageView fileFocused = new ImageView(getClass().getResource("/img/text-x-script_16.png").toExternalForm());

            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                    if (item.isDirectory() && getTreeItem().isExpanded()) {
                        setGraphic(extended);
                    } else {
                        setGraphic(getTreeItem().getGraphic());
                    }
                } else {
                    setText("");
                    setGraphic(null);
                }
            }

            // Приклад використання вибраного елементу в списку
            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);

                if (getTreeItem() != null) {
                    if (selected && getTreeItem().isLeaf()) {
                        setGraphic(fileFocused);
                    } else {
                        setGraphic(getTreeItem().getGraphic());
                    }
                }
            }
        });

        ContextMenu treeItemContextMenu = new ContextMenu();

        tree.setOnContextMenuRequested(contextMenuEvent -> {
            treeItemContextMenu.getItems().clear();
            TreeView<File> treeView = (TreeView<File>) contextMenuEvent.getSource();
            TreeItem<File> selectedItem = treeView.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                MenuItem openFolder = new MenuItem("Open Folder");
                openFolder.setOnAction(actionEvent -> {
                    // test open directory
                    File file = selectedItem.getValue();
                    if (file.isFile()) {
                        file = file.getParentFile();
                    }

                    try {
                        if (OS.isWindows() && Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(file);
                        }
                        if (OS.isUnix()) {
                            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "xdg-open " + file.getAbsolutePath());
                            Process process = processBuilder.start();
                            if (process.waitFor() != 0) {
                                System.out.println("Error command executed!");
                            }
                        }
                        if (OS.isMac()) {
                            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "open " + file.getAbsolutePath());
                            Process process = processBuilder.start();
                            if (process.waitFor() != 0) {
                                System.out.println("Error command executed!");
                            }
                        }
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                treeItemContextMenu.getItems().add(openFolder);
                treeItemContextMenu.show((Node) contextMenuEvent.getTarget(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            }
        });

        sourcePreviewPane = new WebView();

        Preferences viewOption = NotebookFX.getViewView();

        SplitPane fileSplitPane = new SplitPane(tree, sourcePreviewPane);
        splitPaneFilePosition.init(viewOption, "splitPaneFilePosition", SPLIT_PANE_DIVIDER);
        fileSplitPane.setDividerPositions(getSplitPaneFilePosition());

        fileSplitPane.getDividers()
                .get(0)
                .positionProperty()
                .addListener((obs, o, n) -> setSplitPaneFilePosition(n.doubleValue()));

        SplitPane mainSplitPane = new SplitPane(webView, fileSplitPane);
        splitPaneSourcePosition.init(viewOption, "splitPaneSourcePosition", SPLIT_PANE_DIVIDER);
        mainSplitPane.setDividerPositions(getSplitPaneSourcePosition());

        mainSplitPane.getDividers()
                .get(0)
                .positionProperty()
                .addListener((obs, o, n) -> setSplitPaneSourcePosition(n.doubleValue()));

        mainSplitPane.setOrientation(Orientation.VERTICAL);

        borderPane.setCenter(mainSplitPane);
    }

    // 'splitPaneFilePosition' property
    private static final PrefsDoubleProperty splitPaneFilePosition = new PrefsDoubleProperty();

    public static Double getSplitPaneFilePosition() {
        return splitPaneFilePosition.get();
    }

    public static void setSplitPaneFilePosition(double splitPanePosition) {
        splitPaneFilePosition.set(splitPanePosition);
    }

    public static DoubleProperty splitPaneFilePosition() {
        return splitPaneFilePosition;
    }
    // 'splitPaneSourcePosition' property
    private static final PrefsDoubleProperty splitPaneSourcePosition = new PrefsDoubleProperty();

    public static Double getSplitPaneSourcePosition() {
        return splitPaneSourcePosition.get();
    }

    public static void setSplitPaneSourcePosition(double splitPanePosition) {
        splitPaneSourcePosition.set(splitPanePosition);
    }

    public static DoubleProperty splitPaneSourcePosition() {
        return splitPaneSourcePosition;
    }

    private TreeItem<File> createTree(File file, Set<File> fileSource) {
        TreeItem<File> item = new TreeItem<>(file);
        if (fileSource.contains(file)) {
            sources.put(file, item);
        }
        File[] childs = file.listFiles();
        if (childs != null) {
            Arrays.sort(childs, (f1, f2) -> {
                if (f1.isDirectory() && !f2.isDirectory()) {
                    // Directory before non-directory
                    return -1;
                } else if (!f1.isDirectory() && f2.isDirectory()) {
                    // Non-directory after directory
                    return 1;
                } else {
                    // Alphabetic order otherwise
                    return f1.compareTo(f2);
                }
            });
            for (File child : childs) {
                item.getChildren().add(createTree(child, fileSource));
            }
            item.setGraphic(new ImageView(getClass().getResource("/img/folder_16.png").toExternalForm()));
        } else { // file icon
            item.setGraphic(new ImageView(getClass().getResource("/img/text-x-generic_16.png").toExternalForm()));
        }

        return item;
    }

    public void createFileView(String fileName, String params) {
        File file = new File(fileName);
        if (file.exists() && sources.containsKey(file)) {
            TreeItem<File> item = sources.get(file);
            TreeItem<File> parent = item.getParent();
            while (parent != null) {
                parent.setExpanded(true);
                parent = parent.getParent();
            }
            int row = tree.getRow(item);
            tree.getSelectionModel().clearAndSelect(row);
            tree.scrollTo(tree.getRow(item));
            //System.out.println("Params:" + params);
            createFileView(file, params);
        }
    }

    private void createFileView(File file) {
        createFileView(file, null);
    }

    private void createFileView(File file, String params) {
        String htmlContent = "Binary Type";
        String language = CheckFile.isTextFile(file);
        
        try {
            if (!language.isEmpty()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                htmlContent = new String(bytes, Charset.defaultCharset());
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        StringBuilder buf = new StringBuilder();
        buf.append("```");
        buf.append(language).append("\n"); // language
        buf.append(htmlContent).append("\n");
        buf.append("```");

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, MarkdownExtensions.getExtensions());

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Document document = parser.parse(buf.toString());
        String html = renderer.render(document);

        if (params != null && !params.isEmpty()) {
            html = html.replaceFirst("<pre", "<pre data-line=\"" + params + "\"");
        }

        sourcePreviewPane.getEngine().loadContent(
                "<!DOCTYPE html>\n"
                //+ "<html debug=\"true\">\n"
                + "<html>\n"
                + "<head>\n"
                + prismSyntaxHighlighting(document)
                + "<link rel=\"stylesheet\" href=\"" + getClass().getResource("/org/markdownwriterfx/preview/prism/plugins/prism-line-numbers.css") + "\">\n"
                + "<script src=\"" + getClass().getResource("/org/markdownwriterfx/preview/prism/plugins/prism-line-numbers.js") + "\"></script>\n"
                + "<link rel=\"stylesheet\" href=\"" + getClass().getResource("/org/markdownwriterfx/preview/prism/plugins/prism-line-highlight.css") + "\">\n"
                + "<script src=\"" + getClass().getResource("/org/markdownwriterfx/preview/prism/plugins/prism-line-highlight.js") + "\"></script>\n"
                + "<style>\n"
                + "    body { margin: 0;}\n"
                + "    pre { display: inline-block; margin-top: 0; margin-bottom: 0; overflow: visible !important;}\n"
                + "    pre[data-line] { margin-top: -16px; padding-bottom: 0px !important; overflow: hidden;}\n"
                + "</style>\n"
                + "</head>\n"
                + "<body>\n"
                + html.replaceFirst("<pre", "<pre class=\"line-numbers\"")
                + "<script>"
                + "window.addEventListener(\"load\", function(event) {\n"
                + "    window.scroll(0, document.querySelector(\"div[aria-hidden]\").offsetTop - 16);\n"
                + "});"
                + "</script>\n"
                //+ DEBUG
                + "</body>\n"
                + "</html>");

    }

}
