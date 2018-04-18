package baobab.notebookfx.controllers;

import baobab.notebookfx.NotebookFX;
import baobab.notebookfx.controls.NoteTab;
import baobab.notebookfx.controls.cells.CheckTreeViewCell;
import baobab.notebookfx.dialogs.TextInputDialog;
import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Source;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.models.enums.TypeSource;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.CheckFile;
import baobab.notebookfx.utils.SpringFX;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import com.vladsch.flexmark.convert.html.FlexmarkHtmlParser;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.util.options.MutableDataSet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.inject.Inject;
import org.controlsfx.control.CheckTreeView;
import org.fxmisc.undo.UndoManager;
import org.markdownwriterfx.editor.MarkdownEditorPane;
import org.markdownwriterfx.options.MarkdownExtensions;
import org.markdownwriterfx.preview.MarkdownPreviewPane;
import org.markdownwriterfx.util.PrefsBooleanProperty;
import org.markdownwriterfx.util.PrefsDoubleProperty;
import org.markdownwriterfx.util.PrefsEnumProperty;
import org.markdownwriterfx.util.PrefsIntegerProperty;
import org.springframework.stereotype.Component;

@Component
public class EditController implements Initializable, NoteTab.ICheckTreeView {

    @Inject
    ContentManager contentManager;

    private Content content;

    @FXML
    private CheckMenuItem editorMenuItem;

    @FXML
    private CheckMenuItem previewMenuItem;

    @FXML
    private CheckMenuItem sideBarMenuItem;

    @FXML
    private TextField titleText;

    @FXML
    private SplitPane sideBarSpliter;

    @FXML
    private SplitPane editorsSpliter;

    @FXML
    private SplitPane rightBarSpliter;

    @FXML
    private AnchorPane tagAnchorPane;

    @FXML
    private TitledPane tagTitledPane;

    @FXML
    private TabPane tagsTabPane;

    @FXML
    private AnchorPane imageAnchorPane;

    @FXML
    private TitledPane imageTitledPane;

    @FXML
    private TilePane imagesTilePane;

    @FXML
    private Button saveBtn;

    @FXML
    private Button addPictureBtn;

    @FXML
    private TitledPane sourceTitledPane;

    @FXML
    private TableView<Source> sourceTable;

    @FXML
    private TableColumn<Source, String> sourceColumn;

    @FXML
    private TableColumn<Source, Void> delColumn;

    private MarkdownEditorPane markdownEditorPane;

    private MarkdownPreviewPane markdownPreviewPane;

    private StringProperty result;

    private IntegerProperty menu;

    private EventHandler<ActionEvent> menuEventHandler;

    private SetChangeListener<baobab.notebookfx.models.Image> databaseImageChangeListener;

    private Tag rootTag;

    private ContextMenu contextMenu;

    private Set<Tag> selectedTags;

    private Preferences editView;

    private static final double SPLIT_PANE_SIDEBAR_ITEM = 0.33;
    private static final double SPLIT_PANE_DIVIDER = 0.5;
    private static final double SPLIT_PANE_SIDEBAR_DIVIDER = 0.67;

    // 'splitPanePreviewPosition' property
    private static final PrefsDoubleProperty splitPanePreviewPosition = new PrefsDoubleProperty();

    public static Double getSplitPanePreviewPosition() {
        return splitPanePreviewPosition.get();
    }

    public static void setSplitPanePreviewPosition(double splitPanePosition) {
        splitPanePreviewPosition.set(splitPanePosition);
    }

    public static DoubleProperty splitPanePreviewPosition() {
        return splitPanePreviewPosition;
    }
    // 'splitPaneSidebarPosition' property
    private static final PrefsDoubleProperty splitPaneSidebarPosition = new PrefsDoubleProperty();

    public static Double getSplitPaneSidebarPosition() {
        return splitPaneSidebarPosition.get();
    }

    public static void setSplitPaneSidebarPosition(double splitPanePosition) {
        splitPaneSidebarPosition.set(splitPanePosition);
    }

    public static DoubleProperty splitPaneSidebarPosition() {
        return splitPaneSidebarPosition;
    }
    // 'splitPaneTagPosition' property
    private static final PrefsDoubleProperty splitPaneTagPosition = new PrefsDoubleProperty();

    public static Double getSplitPaneTagPosition() {
        return splitPaneTagPosition.get();
    }

    public static void setSplitPaneTagPosition(double splitPanePosition) {
        splitPaneTagPosition.set(splitPanePosition);
    }

    public static DoubleProperty splitPaneTagPosition() {
        return splitPaneTagPosition;
    }
    // 'splitPaneImagePosition' property
    private static final PrefsDoubleProperty splitPaneImagePosition = new PrefsDoubleProperty();

    public static Double getSplitPaneImagePosition() {
        return splitPaneImagePosition.get();
    }

    public static void setSplitPaneImagePosition(double splitPanePosition) {
        splitPaneImagePosition.set(splitPanePosition);
    }

    public static DoubleProperty splitPaneImagePosition() {
        return splitPaneImagePosition;
    }
    // 'splitPanePreviewOrientation' property
    private static final PrefsEnumProperty<Orientation> splitPanePreviewOrientation = new PrefsEnumProperty<>();

    public static Orientation getSplitPanePreviewOrientation() {
        return splitPanePreviewOrientation.get();
    }

    public static void setSplitPanePreviewOrientation(Orientation splitPaneOrientation) {
        splitPanePreviewOrientation.set(splitPaneOrientation);
    }

    public static PrefsEnumProperty<Orientation> splitPanePreviewOrientation() {
        return splitPanePreviewOrientation;
    }
    // 'menuProp' property
    private static final PrefsIntegerProperty menuProp = new PrefsIntegerProperty();

    public static Integer getMenuProp() {
        return menuProp.get();
    }

    public static void setMenuProp(Integer splitPaneOrientation) {
        menuProp.set(splitPaneOrientation);
    }

    public static PrefsIntegerProperty menuProp() {
        return menuProp;
    }

    // 'showTagTabPane' property
    private static final PrefsBooleanProperty showTagTabPane = new PrefsBooleanProperty();

    public static Boolean getShowTagTabPane() {
        return showTagTabPane.get();
    }

    public static void setShowTagTabPane(boolean show) {
        showTagTabPane.set(show);
    }

    public static BooleanProperty showTagTabPane() {
        return showTagTabPane;
    }
    // 'showImageTabPane' property
    private static final PrefsBooleanProperty showImageTabPane = new PrefsBooleanProperty();

    public static Boolean getShowImageTabPane() {
        return showImageTabPane.get();
    }

    public static void setShowImageTabPane(boolean show) {
        showImageTabPane.set(show);
    }

    public static BooleanProperty showImageTabPane() {
        return showImageTabPane;
    }
    // 'showSourceTabPane' property
    private static final PrefsBooleanProperty showSourceTabPane = new PrefsBooleanProperty();

    public static Boolean getShowSourceTabPane() {
        return showSourceTabPane.get();
    }

    public static void setShowSourceTabPane(boolean show) {
        showSourceTabPane.set(show);
    }

    public static BooleanProperty showSourceTabPane() {
        return showSourceTabPane;
    }

    @FXML
    private void handlerBack(ActionEvent event) {
        destroy();
        Scene scene = ((Node) event.getSource()).getScene();
        Parent root = SpringFX.getInstance().load("/fxml/Index.fxml");
        scene.setRoot(root);
    }

    @FXML
    private void handlerSave(ActionEvent event) {
        content.setTags(selectedTags);
        content.setContent(markdownEditorPane.getMarkdownText());

        content.getSources().stream()
                .filter(source -> source.getId() == null)
                .forEach(contentManager::saveSource);

        contentManager.saveContent(content);
    }

    @FXML
    private void handlerSaveNew(ActionEvent event) {
        Content contentNew = new Content();
        contentNew.setTitle(content.getTitle());
        contentNew.setContent(markdownEditorPane.getMarkdownText());
        contentNew = contentManager.saveContent(contentNew);

        Set<baobab.notebookfx.models.Image> images = new HashSet<>();
        content.getImages().stream().forEach(images::add);
        contentNew.setImages(images);
        contentNew.setTags(selectedTags);

        List<Source> sources = content.getSources().stream()
                .map(item -> {
                    Source newSource = new Source();
                    newSource.setName(item.getName());
                    newSource.setType(item.getType());
                    return contentManager.saveSource(newSource);
                }).collect(toList());

        contentNew.setSources(sources);

        // unbind
        content.imagesProperty().get().removeListener(databaseImageChangeListener);
        titleText.textProperty().unbindBidirectional(content.titleProperty());
        result.unbindBidirectional(content.contentProperty());

        content = contentManager.saveContent(contentNew);

        // bind
        titleText.textProperty().bindBidirectional(content.titleProperty());
        result.bindBidirectional(content.contentProperty());
        content.imagesProperty().get().addListener(databaseImageChangeListener);
        createSource();
    }

    @FXML
    private void sourceDirHandler(ActionEvent event) {
        String homeDir = System.getProperty("user.home");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory");

        File homeDirectory = new File(homeDir);
        directoryChooser.setInitialDirectory(homeDirectory);

        Node source = (Node) event.getSource();
        Window primaryStage = source.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            Source sourceItem = new Source();
            sourceItem.setType(TypeSource.Directory);
            sourceItem.setName(selectedDirectory.getAbsolutePath());

            content.getSources().add(sourceItem);
        }
    }

    @FXML
    private void sourceFileHandler(ActionEvent event) {
        String homeDir = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        File homeDirectory = new File(homeDir);
        fileChooser.setInitialDirectory(homeDirectory);

        Node source = (Node) event.getSource();
        Window primaryStage = source.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            Source sourceItem = new Source();
            sourceItem.setType(TypeSource.File);
            sourceItem.setName(selectedFile.getAbsolutePath());

            content.getSources().add(sourceItem);
        }
    }

    public void initData(Long pageId) {
        createSideBar();
        createContent(pageId);
        createTagTree();
        createImageManager();
        createSource();
        createMenuItems();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedTags = new HashSet<>();
        editView = NotebookFX.getEditView();
        result = new SimpleStringProperty();
        menuProp.init(editView, "menuProp", 1);
        menu = new SimpleIntegerProperty(1);
    }

    private void createTagTree() {
        rootTag = contentManager.findRootTree();
//        rootTag.getChildren().forEach(item -> {
//            NoteTab tab = createCheckTreeView(item);
//            tagsTabPane.getTabs().add(tab);
//        });

        rootTag.getChildren().stream()
                .map(this::createCheckTreeView)
                .forEach(tagsTabPane.getTabs()::add);
    }

    private void createContent(Long pageId) {
        content = contentManager.getContent(pageId);

        titleText.textProperty().bindBidirectional(content.titleProperty());

        result.bindBidirectional(content.contentProperty());

        databaseImageChangeListener = (SetChangeListener.Change<? extends baobab.notebookfx.models.Image> change) -> {
            if (change.wasAdded()) {
                contentManager.saveContent(content);
            } else if (change.wasRemoved()) {
                contentManager.saveContent(content);
                contentManager.deleteImages();
            }
        };

        content.imagesProperty().get().addListener(databaseImageChangeListener);

        createEditor();
    }

    private void createEditor() {
        // load file and create UI when the tab becomes visible the first time
        markdownEditorPane = new MarkdownEditorPane();
        markdownPreviewPane = new MarkdownPreviewPane();

        // load
        markdownEditorPane.setMarkdown(result.get());
        markdownEditorPane.getUndoManager().mark();

        // clear undo history after first load
        markdownEditorPane.getUndoManager().forgetHistory();

        // bind preview to editor
        markdownPreviewPane.markdownTextProperty().bind(markdownEditorPane.markdownTextProperty());
        markdownPreviewPane.markdownASTProperty().bind(markdownEditorPane.markdownASTProperty());
        markdownPreviewPane.editorSelectionProperty().bind(markdownEditorPane.selectionProperty());
        markdownPreviewPane.scrollYProperty().bind(markdownEditorPane.scrollYProperty());

        // bind the editor undo manager to the properties
        UndoManager<?> undoManager = markdownEditorPane.getUndoManager();
        modified.bind(Bindings.not(undoManager.atMarkedPositionProperty()));
        canUndo.bind(undoManager.undoAvailableProperty());
        canRedo.bind(undoManager.redoAvailableProperty());

        editorsSpliter.getItems().addAll(markdownEditorPane.getNode(), markdownPreviewPane.getNode());
        splitPanePreviewOrientation.init(editView, "splitPanePreviewOrientation", Orientation.VERTICAL);
        editorsSpliter.setOrientation(getSplitPanePreviewOrientation());

        editorsSpliter.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode.equals(T) && keyEvent.isShortcutDown()) {
                editorsSpliter.setOrientation(
                        editorsSpliter.getOrientation().equals(Orientation.VERTICAL)
                        ? Orientation.HORIZONTAL : Orientation.VERTICAL
                );
                splitPanePreviewOrientation.set(editorsSpliter.getOrientation());

                keyEvent.consume();
            }
        });

        // preferences
        splitPanePreviewPosition.init(editView, "splitPanePreviewPosition", SPLIT_PANE_DIVIDER);
        splitPaneSidebarPosition.init(editView, "splitPaneSidebarPosition", SPLIT_PANE_SIDEBAR_DIVIDER);
        // 
        if (menu.get() == 3 || menu.get() == 7) {
            editorsSpliter.setDividerPositions(getSplitPanePreviewPosition());
        }
        if (menu.get() >= 5) {
            sideBarSpliter.setDividerPositions(getSplitPaneSidebarPosition());
        }

        editorsSpliter.getDividers()
                .get(0)
                .positionProperty()
                .addListener((obs, o, n) -> {
                    try {
                        String str = String.format("%.3f", n);
                        float value = NumberFormat.getInstance().parse(str).floatValue();
                        if (value > 0.01 && value < .99) {
                            setSplitPanePreviewPosition(value);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(EditController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

        sideBarSpliter.getDividers()
                .get(0)
                .positionProperty()
                .addListener((obs, o, n) -> {
                    try {
                        String str = String.format("%.3f", n);
                        float value = NumberFormat.getInstance().parse(str).floatValue();
                        if (value > 0.01 && value < .99) {
                            setSplitPaneSidebarPosition(value);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(EditController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

        updatePreviewType();
        markdownEditorPane.requestFocus();

        // update 'editor' property
        editor.set(markdownEditorPane);
    }

    private boolean updatePreviewTypePending;

    private void updatePreviewType() {
        if (markdownPreviewPane == null) {
            return;
        }

        // avoid too many (and useless) runLater() invocations
        if (updatePreviewTypePending) {
            return;
        }
        updatePreviewTypePending = true;

        Platform.runLater(() -> {
            updatePreviewTypePending = false;

            MarkdownPreviewPane.Type previewType = getPreviewType();

            markdownPreviewPane.setType(previewType);
        });
    }

    private MarkdownPreviewPane.Type getPreviewType() {
        MarkdownPreviewPane.Type previewType = MarkdownPreviewPane.Type.Web;
        // TODO toolbar
//        if (fileEditorTabPane.previewVisible.get()) {
//            previewType = mod_MarkdownPreviewPane.Type.Web;
//        } else if (fileEditorTabPane.htmlSourceVisible.get()) {
//            previewType = mod_MarkdownPreviewPane.Type.Source;
//        }
        return previewType;
    }

    @Override
    public NoteTab createCheckTreeView(Tag tag) {
        NoteTab noteTab = new NoteTab(tag.getName(), this);
        CheckBoxTreeItem<Tag> root = recursiveCheckBoxTreeItem(tag);
        root.setIndependent(true);
        root.setExpanded(true);

        CheckTreeView<Tag> checkTreeView = new CheckTreeView<>(root);
        checkTreeView.setShowRoot(false);
        checkTreeView.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener.Change<? extends TreeItem<Tag>> change) -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            selectedTags.addAll(change.getList().stream()
                                    //.map(treeItem -> treeItem.getValue())
                                    .map(TreeItem<Tag>::getValue)
                                    .collect(toSet()));
                        }
                        if (change.wasRemoved()) {
                            selectedTags.removeAll(change.getRemoved().stream()
                                    //.map(treeItem -> treeItem.getValue())
                                    .map(TreeItem<Tag>::getValue)
                                    .collect(toSet()));
                        }
                    }
                    if (checkTreeView.getCheckModel().getCheckedItems().toArray().length > 0) {
                        noteTab.setSelected(true);
                    } else {
                        noteTab.setSelected(false);
                    }
                });
        checkTreeView.setCellFactory(new CheckTreeViewCell());

        ContextMenu treeItemContextMenu = new ContextMenu();

        checkTreeView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            private CheckBoxTreeItem<Tag> treeItem;

            @Override
            public void handle(ContextMenuEvent event) {
                treeItemContextMenu.getItems().clear();
                CheckTreeView checkTreeView = (CheckTreeView) event.getSource();

                treeItem = (CheckBoxTreeItem<Tag>) checkTreeView.getSelectionModel().getSelectedItem();

                if (treeItem != null) {
                    if (treeItem.previousSibling() != null) {
                        MenuItem moveUp = new MenuItem("Move Up");
                        moveUp.setOnAction(eventMoveUp -> {
                            TreeItem<Tag> parentNode = (TreeItem<Tag>) treeItem.getParent();
                            ObservableList<TreeItem<Tag>> children = parentNode.getChildren();

                            int index = children.indexOf(treeItem);

                            Collections.swap(children, index, --index);

                            contentManager.moveUpTag(treeItem.getValue());

                            sort(children);
                            checkTreeView.getSelectionModel().clearSelection();
                            checkTreeView.getSelectionModel().select(treeItem);
                        });
                        treeItemContextMenu.getItems().add(moveUp);
                    }

                    if (treeItem.nextSibling() != null) {
                        MenuItem moveDown = new MenuItem("Move Down");
                        moveDown.setOnAction(eventModeDown -> {
                            TreeItem<Tag> parentNode = (TreeItem<Tag>) treeItem.getParent();
                            ObservableList<TreeItem<Tag>> children = parentNode.getChildren();

                            int index = children.indexOf(treeItem);

                            Collections.swap(children, index, ++index);

                            contentManager.moveDownTag(treeItem.getValue());
                            // render tree on the screen
                            sort(children);
                            checkTreeView.getSelectionModel().select(treeItem);
                        });
                        treeItemContextMenu.getItems().add(moveDown);
                    }

                    if (treeItem.previousSibling() != null || treeItem.nextSibling() != null) {
                        treeItemContextMenu.getItems().add(new SeparatorMenuItem());
                    }

                    MenuItem edit = new MenuItem("Edit");
                    edit.setOnAction(eventEdit -> {
                        TextInputDialog.create("Edit tag",
                                "Edit Tag Dialog",
                                "Please enter tag name:",
                                treeItem.getValue().getName()
                        ).ifPresent(tagName -> {
                            if (tagName.trim().equals("")) {
                                return;
                            }
                            Tag tag = treeItem.getValue();
                            tag.setName(tagName);
                            sort(treeItem.getParent().getChildren());
                            checkTreeView.getSelectionModel().select(treeItem);
                            contentManager.saveTag(tag);
                        });
                    });
                    treeItemContextMenu.getItems().add(edit);

                    MenuItem addNestedItem = new MenuItem("Add Nested Item");
                    addNestedItem.setOnAction(this::handle);
                    treeItemContextMenu.getItems().add(addNestedItem);

                    if (treeItem.isLeaf()) {
                        treeItemContextMenu.getItems().add(new SeparatorMenuItem());
                        MenuItem delete = new MenuItem("Delete");
                        delete.setOnAction(eventDelete -> {
                            contentManager.deleteTag(treeItem.getValue());
                            treeItem.getParent().getChildren().remove(treeItem);
                            checkTreeView.getSelectionModel().clearSelection();
                        });
                        treeItemContextMenu.getItems().add(delete);
                    }
                } else {
                    MenuItem addNestedItem = new MenuItem("Add Nested Item");
                    addNestedItem.setOnAction(this::handle);
                    treeItemContextMenu.getItems().add(addNestedItem);
                }
                treeItemContextMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
            }

            private void sort(ObservableList<TreeItem<Tag>> children) {
                ObservableList<TreeItem<Tag>> collection = FXCollections.observableArrayList(children);
                //Collections.sort(collection, (TreeItem<Tag> o1, TreeItem<Tag> o2) -> Double.compare(((Tag) o1.getValue()), ((Tag) o2.getValue())));
                children.setAll(collection);
            }

            private void handle(ActionEvent e) {
                TextInputDialog.create("Add new nested tag",
                        "Add New Nested Tag Dialog",
                        "Please enter tag name:",
                        ""
                ).ifPresent(tagName -> {
                    if (tagName.trim().equals("")) {
                        return;
                    }
                    CheckBoxTreeItem<Tag> treeIt = (treeItem != null) ? treeItem : (CheckBoxTreeItem<Tag>) checkTreeView.getRoot();

                    Tag parentTag = treeIt.getValue();

                    Tag tag = new Tag();
                    tag.setName(tagName);
                    tag.setParent(parentTag);
                    tag.setChildren(new LinkedList<>());

                    tag = contentManager.saveTag(tag, parentTag.getChildren().size());

                    CheckBoxTreeItem<Tag> treeItemNested = new CheckBoxTreeItem<>(tag);
                    treeItemNested.setIndependent(true);

                    treeIt.getChildren().add(treeItemNested);
                    checkTreeView.getSelectionModel().select(treeItemNested);
                });
            }
        });
        noteTab.getCheckBox().setOnAction(event -> checkTreeView.getCheckModel().clearChecks());
        noteTab.setContent(checkTreeView);

        return noteTab;
    }

    private CheckBoxTreeItem<Tag> recursiveCheckBoxTreeItem(Tag tag) {
        CheckBoxTreeItem<Tag> checkBoxTreeItem = new CheckBoxTreeItem<>(tag);
        checkBoxTreeItem.setIndependent(true);

        boolean isCheckBoxSelected = content.getTags().stream()
                .map(Tag::getId)
                .anyMatch(tag.getId()::equals);
//                .anyMatch(item -> tag.getId().equals(item.getId()));

        if (isCheckBoxSelected) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> checkBoxTreeItem.setSelected(true));
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1000l);
        }

        tag.getChildren().stream()
                .map(this::recursiveCheckBoxTreeItem)
                .forEach(checkBoxTreeItem.getChildren()::add);
//                .forEach(item -> checkBoxTreeItem.getChildren().add(recursiveCheckBoxTreeItem(item)));

        return checkBoxTreeItem;
    }

    private void createMenuItems() {
        // Add Ctrl+S for save data
        // https://stackoverflow.com/questions/40682141/how-to-set-set-keyboard-shortcuts-in-javafx-from-controller
        EventHandler<KeyEvent> eventHandler = keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode.equals(S) && keyEvent.isShortcutDown()) {
                saveBtn.fire();
                keyEvent.consume();
            }
        };
        // Add Ctrl+M for convert html to markdown
        EventHandler<KeyEvent> convertHandler = keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode.equals(M) && keyEvent.isShortcutDown()) {
                String html = markdownEditorPane.getMarkdown();
                String markdown = FlexmarkHtmlParser.parse(html);
                markdownEditorPane.setMarkdown(markdown);
                keyEvent.consume();
            }
        };
        // Add Ctrl+P for convert html to markdown
        EventHandler<KeyEvent> exportPDFHandler = keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode.equals(P) && keyEvent.isShortcutDown()) {
                String homeDir = System.getProperty("user.home");

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Directory For Save PDF");

                Window primaryStage = titleText.getScene().getWindow();

                File selectedDirectory = directoryChooser.showDialog(primaryStage);

                if (selectedDirectory != null) {
                    String fileNamePDf = titleText.getText().replaceAll("[^\\p{L}\\-]+", "") + ".pdf";
                    
                    MutableDataSet options = new MutableDataSet();
                    options.set(Parser.EXTENSIONS, MarkdownExtensions.getExtensions());
                    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                    
                    String html = renderer.render(markdownPreviewPane.markdownASTProperty().get());

                    Path pathPDF = Paths.get(selectedDirectory.getAbsolutePath(), fileNamePDf);
                    // TODO: Not working with image file from database, problem in base64 encoding
                    // need change for proper base64 encoding
                    PdfConverterExtension.exportToPdf(pathPDF.toString(), html, "", options);
                }
                keyEvent.consume();
            }
        };
        saveBtn.sceneProperty().addListener((obs, o, n) -> {
            if (o != null) {
                o.removeEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
                o.removeEventFilter(KeyEvent.KEY_PRESSED, convertHandler);
                o.removeEventFilter(KeyEvent.KEY_PRESSED, exportPDFHandler);
            }
            if (n != null) {
                n.addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
                n.addEventFilter(KeyEvent.KEY_PRESSED, convertHandler);
                n.addEventFilter(KeyEvent.KEY_PRESSED, exportPDFHandler);
            }
        });

        // set up data
        editorMenuItem.setUserData(1);
        previewMenuItem.setUserData(2);
        sideBarMenuItem.setUserData(4);

        menu.addListener((obs, o, n) -> {
            menuProp.set(n.intValue());
            double[] menuPosition = new double[]{1.0, 1.0};

            editorMenuItem.setSelected(false);
            previewMenuItem.setSelected(false);
            sideBarMenuItem.setSelected(false);

            switch (n.intValue()) {
                case 1: // Editor
                    editorMenuItem.setSelected(true);
                    break;
                case 2: // Preview
                    menuPosition[0] = .0;
                    previewMenuItem.setSelected(true);
                    break;
                case 3: // Editor & Preview
                    menuPosition[0] = getSplitPanePreviewPosition();
                    editorMenuItem.setSelected(true);
                    previewMenuItem.setSelected(true);
                    break;
                case 5: // Editor & Sidebar
                    menuPosition[1] = getSplitPaneSidebarPosition();
                    editorMenuItem.setSelected(true);
                    sideBarMenuItem.setSelected(true);
                    break;
                case 6: // Preview & Sidebar
                    menuPosition[0] = .0;
                    menuPosition[1] = getSplitPaneSidebarPosition();
                    previewMenuItem.setSelected(true);
                    sideBarMenuItem.setSelected(true);
                    break;
                case 7: // Editor, Preview & Sidebar
                    menuPosition[0] = getSplitPanePreviewPosition();
                    menuPosition[1] = getSplitPaneSidebarPosition();
                    editorMenuItem.setSelected(true);
                    previewMenuItem.setSelected(true);
                    sideBarMenuItem.setSelected(true);
                    break;
            }
            editorsSpliter.setDividerPositions(menuPosition[0]);
            sideBarSpliter.setDividerPositions(menuPosition[1]);
        });
        menu.set(getMenuProp());

        editorMenuItem.disableProperty().bind(previewMenuItem.selectedProperty().not());
        previewMenuItem.disableProperty().bind(editorMenuItem.selectedProperty().not());
        // menu items
        menuEventHandler = actionEvent -> {
            CheckMenuItem menuItem = (CheckMenuItem) actionEvent.getTarget();
            int value = (Integer) menuItem.getUserData();
            menu.set(menuItem.isSelected() ? menu.get() + value : menu.get() - value);
            setMenuProp(menu.get());
        };

        editorMenuItem.setOnAction(menuEventHandler);
        previewMenuItem.setOnAction(menuEventHandler);
        sideBarMenuItem.setOnAction(menuEventHandler);

        rightBarSpliter.managedProperty().bindBidirectional(rightBarSpliter.visibleProperty());
        rightBarSpliter.visibleProperty().bindBidirectional(sideBarMenuItem.selectedProperty());

        result = new SimpleStringProperty();
    }

    private void addButtonImage(File file, TilePane tilePane) {
        Button btn = new Button();

        final ProgressIndicator progressIndicator = new ProgressIndicator(0);
        btn.setGraphic(progressIndicator);

        tilePane.getChildren().add(btn);

        final Task task = new Task<ImageView>() {
            {
                setOnSucceeded(workerStateEvent -> {
                    btn.setGraphic(getValue());

                    baobab.notebookfx.models.Image image = contentManager
                            .saveImage((baobab.notebookfx.models.Image) getValue().getUserData());

                    content.imagesProperty().get().add(image);

                    btn.setUserData(image);
                    btn.setPadding(Insets.EMPTY);
                    btn.setTooltip(new Tooltip("id: " + image.getId().toString()));
                });
            }

            @Override
            protected ImageView call() {
                BufferedImage read = null;
                baobab.notebookfx.models.Image img = new baobab.notebookfx.models.Image();
                try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(file)) {
                    Iterator iteretor = ImageIO.getImageReaders(imageInputStream);
                    if (!iteretor.hasNext()) {
                        return null;
                    }
                    ImageReader reader = (ImageReader) iteretor.next();
                    reader.addIIOReadProgressListener(new IIOReadProgressListener() {
                        @Override
                        public void sequenceStarted(ImageReader source, int minIndex) {
                            // System.out.println("sequence started " + source + ": " + minIndex);
                        }

                        @Override
                        public void sequenceComplete(ImageReader source) {
                            // System.out.println("sequence complete " + source);
                        }

                        @Override
                        public void imageStarted(ImageReader source, int imageIndex) {
                            // System.out.println("image #" + imageIndex + " started " + source);
                            updateProgress(0, 100);
                        }

                        @Override
                        public void imageProgress(ImageReader source, float percentageDone) {
                            // System.out.println("image progress " + source + ": " + percentageDone + "%");
                            updateProgress(percentageDone, 100);
                        }

                        @Override
                        public void imageComplete(ImageReader source) {
                            // System.out.println("image complete " + source);
                        }

                        @Override
                        public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnailIndex) {
                            // System.out.println("thumbnail progress " + source + ", " + thumbnailIndex + " of " + imageIndex);
                        }

                        @Override
                        public void thumbnailProgress(ImageReader source, float percentageDone) {
                            // System.out.println("thumbnail started " + source + ": " + percentageDone + "%");
                        }

                        @Override
                        public void thumbnailComplete(ImageReader source) {
                            // System.out.println("thumbnail complete " + source);
                        }

                        @Override
                        public void readAborted(ImageReader source) {
                            // System.out.println("read aborted " + source);
                        }
                    });
                    reader.setInput(imageInputStream, true);
                    read = reader.read(0);
                    // Create the byte array to hold the data
                    byte[] byteImage;
                    String type = Files.probeContentType(file.toPath());

                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        ImageIO.write(read, type.replace("image/", ""), out);
                        out.flush();
                        byteImage = out.toByteArray();
                    }
                    img.setContent(byteImage);
                    img.setType(type);
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }

                ImageView imageView = new ImageView();
                Image image = SwingFXUtils.toFXImage(read, null);
                imageView.setFitWidth(100);
                imageView.setFitHeight(image.getHeight() * 100 / image.getWidth());
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
                clip.setArcWidth(5);
                clip.setArcHeight(5);

                imageView.setClip(clip);
                imageView.setUserData(img);

                imageView.setImage(image);
                return imageView;
            }
        };
        btn.disableProperty().bind(task.runningProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());
        new Thread(task).start();

        btn.setOnAction(new ThumbnailBtnActionEvent());
        btn.setOnContextMenuRequested(new ThumbnailBtnContextMenuEventHandler());
    }

    private void createImageManager() {
        contextMenu = new ContextMenu();
        MenuItem contextMenuDelete = new MenuItem("Delete");
        contextMenuDelete.setOnAction((ActionEvent event) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Delete Image");
            alert.setContentText("Are you want delete image?");

            Optional<ButtonType> resultAlert = alert.showAndWait();
            if (resultAlert.get() == ButtonType.OK) {
                Button currentButton = (Button) ((MenuItem) event.getTarget()).getUserData();
                baobab.notebookfx.models.Image image = (baobab.notebookfx.models.Image) currentButton.getUserData();

                content.imagesProperty().get()
                        .remove(content.getImages()
                                .stream()
                                .filter(imageItem -> Objects.equals(image.getId(), imageItem.getId()))
                                .findFirst().get());
                ((TilePane) currentButton.getParent()).getChildren().remove(currentButton);
            }
        }
        );
        contextMenu.getItems().add(contextMenuDelete);

        content.getImages()
                .stream()
                .forEach(imageItem -> {
                    Button btn = new Button();

                    final Task task = new Task<ImageView>() {
                        {
                            setOnSucceeded(workerStateEvent -> {
                                btn.setGraphic(getValue());
                                btn.setUserData(imageItem);
                                btn.setPadding(Insets.EMPTY);
                                btn.setTooltip(new Tooltip("id: " + imageItem.getId().toString()));
                            });
                        }

                        @Override
                        protected ImageView call() {
                            ImageView imageView = new ImageView();
                            try {
                                byte[] byteImage = imageItem.getContent();
                                ByteArrayInputStream in = new ByteArrayInputStream(byteImage);
                                BufferedImage read = ImageIO.read(in);

                                Image image = SwingFXUtils.toFXImage(read, null);
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(image.getHeight() * 100 / image.getWidth());
                                imageView.setPreserveRatio(true);
                                imageView.setSmooth(true);

                                Rectangle clip = new Rectangle(
                                        imageView.getFitWidth(), imageView.getFitHeight()
                                );
                                clip.setArcWidth(5);
                                clip.setArcHeight(5);

                                imageView.setClip(clip);
                                imageView.setImage(image);
                            } catch (IOException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            }
                            return imageView;
                        }
                    };
                    btn.disableProperty().bind(task.runningProperty());
                    new Thread(task).start();

                    btn.setOnAction(new ThumbnailBtnActionEvent());
                    btn.setOnContextMenuRequested(new ThumbnailBtnContextMenuEventHandler());

                    imagesTilePane.getChildren().add(btn);
                });

        addPictureBtn.setOnAction(event -> {
            FileChooser choose = new FileChooser();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
            FileChooser.ExtensionFilter extFilterGIF = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.gif");
            choose.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterGIF);

            Stage stage = (Stage) ((Button) event.getTarget()).getScene().getWindow();
            List<File> listFile = choose.showOpenMultipleDialog(stage);
            if (listFile != null) {
                listFile.stream()
                        .filter(CheckFile::isFileImage)
                        .forEach(file -> addButtonImage(file, imagesTilePane));
            }
        });

        imagesTilePane.setOnDragOver(dragEvent -> {
            final Dragboard dragboard = dragEvent.getDragboard();

            boolean isAccepted = dragboard.getFiles().stream()
                    .allMatch(CheckFile::isFileImage);
//                    .noneMatch(!CheckImage::check);

            if (dragboard.hasFiles() && isAccepted) {
                imagesTilePane.setStyle(
                        "-fx-effect: innershadow(three-pass-box, green, 10, 0, 0, 0);"
                        + "-fx-background-color: #cfcfcf;"
                );
                dragEvent.acceptTransferModes(TransferMode.COPY);
            } else {
                dragEvent.consume();
            }
        });

        imagesTilePane.setOnDragDropped(dragEvent -> {
            final Dragboard db = dragEvent.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                db.getFiles()
                        .stream()
                        .forEach(file -> Platform.runLater(() -> addButtonImage(file, imagesTilePane)));
            }
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });

        imagesTilePane.setOnDragExited(event -> imagesTilePane.setStyle("-fx-border-color: #C6C6C6;"));
    }

    private void createSource() {
        sourceTable.setItems(content.sourcesProperty().get());
        // hide header
        sourceTable.skinProperty().addListener((ObservableValue<? extends Skin<?>> obs, Skin<?> oldSkin, Skin<?> newSkin) -> {
            TableHeaderRow headerRow = ((TableViewSkinBase) newSkin).getTableHeaderRow();
            headerRow.setMinHeight(0);
            headerRow.setPrefHeight(0);
            headerRow.setMaxHeight(0);
            headerRow.setVisible(false);
        });
        sourceColumn.setStyle("-fx-alignment: center-left;");
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sourceColumn.setCellFactory(tableColumn -> new CenteredOverrunTableCell("..."));
        delColumn.setCellFactory(tableColumn -> new ActionTableCell<>(integer -> {
            content.sourcesProperty().get().remove(integer.intValue());
        }));

    }

    private void createSideBar() {
        tagTitledPane.setUserData(tagTitledPane.getHeight());
        imageTitledPane.setUserData(imageTitledPane.getHeight());

        // preference
        splitPaneTagPosition.init(editView, "splitPaneTagPosition", SPLIT_PANE_SIDEBAR_ITEM);
        splitPaneImagePosition.init(editView, "splitPaneImagePosition", SPLIT_PANE_SIDEBAR_DIVIDER);
        rightBarSpliter.setDividerPositions(getSplitPaneTagPosition(), getSplitPaneImagePosition());

        rightBarSpliter.getDividers()
                .get(0)
                .positionProperty()
                .addListener((obs, o, n) -> {
                    try {
                        String str = String.format("%.3f", n);
                        float value = NumberFormat.getInstance().parse(str).floatValue();
                        setSplitPaneTagPosition(value);
                    } catch (ParseException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                });

        rightBarSpliter.getDividers()
                .get(1)
                .positionProperty()
                .addListener((obs, o, n) -> {
                    try {
                        String str = String.format("%.3f", n);
                        float value = NumberFormat.getInstance().parse(str).floatValue();
                        setSplitPaneImagePosition(value);
                    } catch (ParseException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                });

        // preference
        showTagTabPane.init(editView, "showTagTabPane", true);
        tagTitledPane.setExpanded(getShowTagTabPane());

        tagTitledPane.expandedProperty()
                .addListener((obs, o, n) -> {
                    if (n) {
                        double tagPaneSize = (double) tagTitledPane.getUserData();
                        rightBarSpliter.setDividerPosition(0, tagPaneSize / rightBarSpliter.getHeight());
                        tagAnchorPane.setMaxHeight(Double.MAX_VALUE);
                        tagAnchorPane.setMinHeight(tagPaneSize);
                        tagAnchorPane.setMinHeight(0);
                    } else {
                        tagTitledPane.setUserData(tagTitledPane.getHeight());
                        tagAnchorPane.setMaxHeight(26);
                        rightBarSpliter.setDividerPosition(0, (26 / rightBarSpliter.getHeight()));
                    }
                    setShowTagTabPane(n);
                });

        // preference
        showImageTabPane.init(editView, "showImageTabPane", true);
        imageTitledPane.setExpanded(getShowImageTabPane());

        imageTitledPane.expandedProperty()
                .addListener((obs, o, n) -> {
                    if (n) {
                        double imagePaneSize = (double) imageTitledPane.getUserData();
                        double tagPaneSize = (double) tagTitledPane.getUserData();
                        rightBarSpliter.setDividerPosition(1, (tagPaneSize + imagePaneSize) / rightBarSpliter.getHeight());
                        imageAnchorPane.setMaxHeight(Double.MAX_VALUE);
                        imageAnchorPane.setMinHeight(imagePaneSize);
                        imageAnchorPane.setMinHeight(0);
                    } else {
                        imageTitledPane.setUserData(imageTitledPane.getHeight());
                        imageAnchorPane.setMaxHeight(26);
                        rightBarSpliter.setDividerPosition(1, rightBarSpliter.getDividerPositions()[0] + (26 / rightBarSpliter.getHeight()));
                    }
                    setShowImageTabPane(n);
                });

        // preference
        showSourceTabPane.init(editView, "showSourceTabPane", true);
        sourceTitledPane.setExpanded(getShowSourceTabPane());

        sourceTitledPane.expandedProperty()
                .addListener((obs, o, n) -> setShowSourceTabPane(n));
    }

    private class ThumbnailBtnContextMenuEventHandler implements EventHandler<ContextMenuEvent> {

        @Override
        public void handle(ContextMenuEvent event) {
            Button btn = (Button) event.getTarget();
            contextMenu.getItems().get(0).setUserData(btn);
            contextMenu.show(btn, event.getScreenX(), event.getScreenY());
        }

    }

    private class ThumbnailBtnActionEvent implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            baobab.notebookfx.models.Image image = (baobab.notebookfx.models.Image) ((Button) event.getTarget()).getUserData();
            markdownEditorPane.getSmartEdit().insertDBImage(image.getId());
            // TODO toolbar plantUml buttons extensions
//            markdownEditorPane.getSmartEdit().insertPlantUmlActivityDiagram();
//            markdownEditorPane.getSmartEdit().insertPlantUmlClassDiagram();
//            markdownEditorPane.getSmartEdit().insertPlantUmlDotDiagram();
//            markdownEditorPane.getSmartEdit().insertPlantUmlObjectDiagram();
//            markdownEditorPane.getSmartEdit().insertPlantUmlSequenceDiagram();
//            markdownEditorPane.getSmartEdit().insertPlantUmlUseCaseDiagram();
        }

    }

    // 'editor' property
    private final ObjectProperty<MarkdownEditorPane> editor = new SimpleObjectProperty<>();

    ReadOnlyObjectProperty<MarkdownEditorPane> editorProperty() {
        return editor;
    }

    // 'modified' property
    private final ReadOnlyBooleanWrapper modified = new ReadOnlyBooleanWrapper();

    boolean isModified() {
        return modified.get();
    }

    ReadOnlyBooleanProperty modifiedProperty() {
        return modified.getReadOnlyProperty();
    }

    // 'canUndo' property
    private final BooleanProperty canUndo = new SimpleBooleanProperty();

    BooleanProperty canUndoProperty() {
        return canUndo;
    }

    // 'canRedo' property
    private final BooleanProperty canRedo = new SimpleBooleanProperty();

    BooleanProperty canRedoProperty() {
        return canRedo;
    }

    class CenteredOverrunTableCell extends TableCell<Source, String> {

        private Image imageFile = new Image(getClass().getResource("/img/document_16.png").toExternalForm());
        private ImageView imageViewFile = new ImageView(imageFile);

        private Image imageFolder = new Image(getClass().getResource("/img/folder_16.png").toExternalForm());
        private ImageView imageViewFolder = new ImageView(imageFolder);
        private Source source;

        public CenteredOverrunTableCell() {
            this(null);
        }

        public CenteredOverrunTableCell(String ellipsisString) {
            super();
            setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
            if (ellipsisString != null) {
                setEllipsisString(ellipsisString);
            }
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            source = (Source) getTableRow().getItem();

            if (item == null || source == null) {
                setTooltip(null);
                setText(null);
                setGraphic(null);
            } else {
                TypeSource typeSource = source.getType();

                Tooltip tooltip = new Tooltip();
                tooltip.setText(getItem());
                setTooltip(tooltip);
                setText(item);

                if (typeSource.equals(TypeSource.File)) {
                    setGraphic(imageViewFile);
                } else {
                    setGraphic(imageViewFolder);
                }
            }
        }
    }

    class ActionTableCell<Source> extends TableCell<Source, Void> {

        private Image image = new Image(getClass().getResource("/img/delete_16.png").toExternalForm());
        private ImageView imageView = new ImageView(image);

        private Button button;

        public ActionTableCell(Consumer<Integer> action) {
            button = new Button();
            button.getStyleClass().add("delete");
            button.setGraphic(imageView);
            button.setOnAction(actionEvent -> action.accept(getIndex()));
            setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            setGraphic(null);
            if (!empty) {
                setGraphic(button);
            }
        }
    }

    public void destroy() {
        content = null;
        editorMenuItem = null;
        previewMenuItem = null;
        sideBarMenuItem = null;
        titleText = null;
        sideBarSpliter = null;
        editorsSpliter = null;
        rightBarSpliter = null;
        tagAnchorPane = null;
        tagTitledPane = null;
        tagsTabPane = null;
        imageAnchorPane = null;
        imageTitledPane = null;
        imagesTilePane = null;
        saveBtn = null;
        addPictureBtn = null;
        sourceTitledPane = null;
        sourceTable = null;
        sourceColumn = null;
        delColumn = null;
        markdownEditorPane = null;
        markdownPreviewPane = null;
        result = null;
        menu = null;
        menuEventHandler = null;
        databaseImageChangeListener = null;
        rootTag = null;
        contextMenu = null;
        selectedTags = null;
        editView = null;

        System.gc();
        //UtilMemory.report();
    }
}
