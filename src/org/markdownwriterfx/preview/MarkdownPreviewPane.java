package org.markdownwriterfx.preview;

import com.vladsch.flexmark.ast.Node;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.layout.BorderPane;
import org.markdownwriterfx.util.Range;

public class MarkdownPreviewPane {

    public enum Type {
        None, Web, Source
    };

    private final BorderPane pane = new BorderPane();
    private final WebViewPreview webViewPreview = new WebViewPreview();
    private final HtmlSourcePreview htmlSourcePreview = new HtmlSourcePreview();
    private final PreviewContext previewContext;

    private Renderer activeRenderer = new FlexmarkPreviewRenderer();
    private Preview activePreview;

    interface Renderer {

        void update(String markdownText, Node astRoot);

        String getHtml(boolean source);

        List<Range> findSequences(int startOffset, int endOffset);
    }

    public interface Preview {

        javafx.scene.Node getNode();

        void update(PreviewContext context, Renderer renderer);

        void scrollY(PreviewContext context, double value);

        void editorSelectionChanged(PreviewContext context, IndexRange range);
    }

    interface PreviewContext {

        Renderer getRenderer();

        String getMarkdownText();

        Node getMarkdownAST();

//        Path getPath();

        IndexRange getEditorSelection();
    }

    public MarkdownPreviewPane() {
        pane.getStyleClass().add("preview-pane");

        previewContext = new PreviewContext() {
            @Override
            public Renderer getRenderer() {
                return activeRenderer;
            }

            @Override
            public String getMarkdownText() {
                return markdownText.get();
            }

            @Override
            public Node getMarkdownAST() {
                return markdownAST.get();
            }

            @Override
            public IndexRange getEditorSelection() {
                return editorSelection.get();
            }
        };

//        path.addListener((observable, oldValue, newValue) -> update());
        markdownText.addListener((observable, oldValue, newValue) -> update());
        markdownAST.addListener((observable, oldValue, newValue) -> update());
        scrollY.addListener((observable, oldValue, newValue) -> scrollY());
        editorSelection.addListener((observable, oldValue, newValue) -> editorSelectionChanged());
    }

    public javafx.scene.Node getNode() {
        return pane;
    }

    public void setType(Type type) {
        Preview preview;
        switch (type) {
            case Web:
                preview = webViewPreview;
                break;
            case Source:
                preview = htmlSourcePreview;
                break;
            default:
                preview = null;
                break;
        }
        if (activePreview == preview) {
            return;
        }

        activePreview = preview;
        pane.setCenter((preview != null) ? preview.getNode() : null);

        update();
        scrollY();
    }

    private boolean updateRunLaterPending;

    private void update() {
        if (activePreview == null) {
            return;
        }

        // avoid too many (and useless) runLater() invocations
        if (updateRunLaterPending) {
            return;
        }
        updateRunLaterPending = true;

        Platform.runLater(() -> {
            updateRunLaterPending = false;

            activeRenderer.update(markdownText.get(), markdownAST.get()/*, path.get()*/);
            activePreview.update(previewContext, activeRenderer);
        });
    }

    private boolean scrollYrunLaterPending;

    private void scrollY() {
        if (activePreview == null) {
            return;
        }

        // avoid too many (and useless) runLater() invocations
        if (scrollYrunLaterPending) {
            return;
        }
        scrollYrunLaterPending = true;

        Platform.runLater(() -> {
            scrollYrunLaterPending = false;
            activePreview.scrollY(previewContext, scrollY.get());
        });
    }

    private boolean editorSelectionChangedRunLaterPending;

    private void editorSelectionChanged() {
        if (activePreview == null) {
            return;
        }

        // avoid too many (and useless) runLater() invocations
        if (editorSelectionChangedRunLaterPending) {
            return;
        }
        editorSelectionChangedRunLaterPending = true;

        Platform.runLater(() -> {
            editorSelectionChangedRunLaterPending = false;

            // use another runLater() to make sure that activePreview.editorSelectionChanged()
            // is invoked after activePreview.update(), so that it can work on already updated text
            Platform.runLater(
                    () -> activePreview.editorSelectionChanged(previewContext, editorSelection.get())
            );
        });
    }

    // 'markdownText' property
    private final SimpleStringProperty markdownText = new SimpleStringProperty();

    public SimpleStringProperty markdownTextProperty() {
        return markdownText;
    }

    // 'markdownAST' property
    private final ObjectProperty<Node> markdownAST = new SimpleObjectProperty<>();

    public ObjectProperty<Node> markdownASTProperty() {
        return markdownAST;
    }

    // 'scrollY' property
    private final DoubleProperty scrollY = new SimpleDoubleProperty();

    public DoubleProperty scrollYProperty() {
        return scrollY;
    }

    // 'editorSelection' property
    private final ObjectProperty<IndexRange> editorSelection = new SimpleObjectProperty<>();

    public ObjectProperty<IndexRange> editorSelectionProperty() {
        return editorSelection;
    }
}
