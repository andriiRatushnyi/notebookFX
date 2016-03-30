package baobab.notebookfx.controllers;

import baobab.notebookfx.controls.BaobTab;
import baobab.notebookfx.controls.cells.CheckTreeViewCell;
import baobab.notebookfx.controls.handlers.edit.AlertEventHandler;
import baobab.notebookfx.controls.handlers.edit.ContextMenuDeleteEventHandler;
import baobab.notebookfx.controls.handlers.edit.MenuChangeListener;
import baobab.notebookfx.controls.handlers.edit.MenuEventHandler;
import baobab.notebookfx.controls.handlers.edit.ThumbnailBtnActionEvent;
import baobab.notebookfx.controls.handlers.edit.ThumbnailBtnContextMenuEventHandler;
import baobab.notebookfx.controls.handlers.edit.WebEditorEventHandler;
import baobab.notebookfx.controls.listeners.edit.DatabaseImageChangeListener;
import baobab.notebookfx.controls.listeners.edit.ResultChangeListener;
import baobab.notebookfx.controls.listeners.edit.WebEditorEngineChangeListener;
import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.CheckImage;
import baobab.notebookfx.utils.SpringFXLoader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakSetChangeListener;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.inject.Inject;
import org.controlsfx.control.CheckTreeView;
import org.springframework.stereotype.Component;

@Component
public class EditController implements Initializable {

    @Inject
    ContentManager contentManager;

    private Content content;

    @FXML
    private SplitPane sideBarSpliter;
    @FXML
    private SplitPane editorsSpliter;
    @FXML
    private TextField titleText;
    @FXML
    private CheckMenuItem editorMenuItem;
    @FXML
    private CheckMenuItem previewMenuItem;
    @FXML
    private CheckMenuItem sideBarMenuItem;
    @FXML
    private CheckMenuItem sourceMenuItem;
    @FXML
    private TabPane tagsTabPane;
    @FXML
    private TilePane imagesTilePane;
    @FXML
    private Button addPictureBtn;
    @FXML
    private WebView webEditor;
    @FXML
    private WebView webView;

    private WebEngine webEditorEngine;

    private WebEditorEngineChangeListener webEditorEngineChangeListener;
    private WeakChangeListener weakWebEditorEngineChangeListener;

    private WebEditorEventHandler webEditorEventHandler;
    private WeakEventHandler weakWebEditorEventHandler;

    private final AlertEventHandler alertEventHandler = new AlertEventHandler();

    private ResultChangeListener resultChangeListener;
    private WeakChangeListener weakResultChangeListener;

    private StringProperty result;

    private IntegerProperty menu;

    private MenuEventHandler menuEventHandler;
    private WeakEventHandler weakMenuEventHandler;

    private MenuChangeListener menuChangeListener;
    private WeakChangeListener weakMenuChangeListener;

    private DatabaseImageChangeListener batabaseImageChangeListener;
    private WeakSetChangeListener weakDatabaseImageChangeListener;

    private ObservableList<Tag> tagList;

    private Set<CheckBoxTreeItem<Tag>> checkBoxTreeList;

    private ContextMenu contextMenu;

    private final String aceEditorURL = getClass().getResource("/AceEditor.html").toExternalForm();

    @FXML
    private void handlerBack(ActionEvent event) {
        try {
            destroy();
            Scene scene = ((Node) event.getSource()).getScene();
            Parent root = SpringFXLoader.getInstance().loader("/fxml/Index.fxml").load();
            scene.setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(EditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handlerSave(ActionEvent event) {
        content.setTags(getSelectedTags());
        contentManager.saveContent(content);
    }

    @FXML
    private void handlerSaveNew(ActionEvent event) {
        Content contentNew = new Content();
        contentNew.setTitle(content.getTitle());
        contentNew.setContent(content.getContent());
        contentNew = contentManager.saveContent(contentNew);

        Set<baobab.notebookfx.models.Image> images = new HashSet<>();
        content.getImages().stream().forEach(images::add);
        contentNew.setImages(images);
        contentNew.setTags(getSelectedTags());

        // unbind
        content.imagesProperty().get().removeListener(weakDatabaseImageChangeListener);
        titleText.textProperty().unbindBidirectional(content.titleProperty());
        result.unbindBidirectional(content.contentProperty());

        content = contentManager.saveContent(contentNew);

        // bind
        titleText.textProperty().bindBidirectional(content.titleProperty());
        result.bindBidirectional(content.contentProperty());
        content.imagesProperty().get().addListener(weakDatabaseImageChangeListener);
    }

    public void initData(Long pageId) {
        createContent(pageId);
        createImageManager();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result = new SimpleStringProperty();
        menu = new SimpleIntegerProperty(1);

        webEditorEngine = webEditor.getEngine();

        webEditorEventHandler = new WebEditorEventHandler(webEditorEngine);
        weakWebEditorEventHandler = new WeakEventHandler(webEditorEventHandler);
        webEditor.addEventHandler(KeyEvent.KEY_PRESSED, weakWebEditorEventHandler);
        webEditorEngine.setOnAlert(alertEventHandler);

        resultChangeListener = new ResultChangeListener(webView.getEngine());
        weakResultChangeListener = new WeakChangeListener(resultChangeListener);
        result.addListener(weakResultChangeListener);

        // menu items
        menuEventHandler = new MenuEventHandler(menu);
        weakMenuEventHandler = new WeakEventHandler(menuEventHandler);

        menuChangeListener = new MenuChangeListener(editorsSpliter, sideBarSpliter);
        weakMenuChangeListener = new WeakChangeListener(menuChangeListener);

        createMenuItems();

        // tags
        checkBoxTreeList = new HashSet<>();

        tagList = FXCollections.observableArrayList(contentManager.findTags());

        tagList.stream()
                .filter(item -> item.getParentId() == 0)
                .forEach(item -> tagsTabPane.getTabs().add(createCheckTreeView(item)));
    }

    private void createContent(Long pageId) {
        content = contentManager.getContent(pageId);

        titleText.textProperty().bindBidirectional(content.titleProperty());

        checkBoxTreeList.stream()
                .forEach(checkBoxItem -> content.getTags().stream()
                        .filter(tag -> tag.getId().equals(checkBoxItem.getValue().getId()))
                        .forEach(item -> checkBoxItem.setSelected(true)));

        result.bindBidirectional(content.contentProperty());

        batabaseImageChangeListener = new DatabaseImageChangeListener(content);
        weakDatabaseImageChangeListener = new WeakSetChangeListener(batabaseImageChangeListener);

        content.imagesProperty().get().addListener(weakDatabaseImageChangeListener);

        webEditorEngineChangeListener = new WebEditorEngineChangeListener(webEditorEngine, result, content.getContent());
        weakWebEditorEngineChangeListener = new WeakChangeListener(webEditorEngineChangeListener);

        webEditorEngine.getLoadWorker().stateProperty().addListener(weakWebEditorEngineChangeListener);
        webEditorEngine.load(aceEditorURL);
    }

    public BaobTab createCheckTreeView(Tag tag) {
        BaobTab baobTab = new BaobTab(tag.getName(), this);
        CheckBoxTreeItem<Tag> root = recursiveCheckBoxTreeItem(tag);
        root.setIndependent(true);
        root.setExpanded(true);

        CheckTreeView<Tag> checkTreeView = new CheckTreeView<>(root);
        checkTreeView.setShowRoot(false);
        checkTreeView.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener.Change<? extends TreeItem<Tag>> t) -> {
                    if (checkTreeView.getCheckModel().getCheckedItems().toArray().length > 0) {
                        baobTab.setSelected(true);
                    } else {
                        baobTab.setSelected(false);
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
                            treeItem.previousSibling().getValue().setSort(treeItem.previousSibling().getValue().getSort() + 1);
                            treeItem.getValue().setSort(treeItem.getValue().getSort() - 1);
                            TreeItem<Tag> parentNode = (TreeItem<Tag>) treeItem.getParent();
                            ObservableList<TreeItem<Tag>> children = parentNode.getChildren();

                            contentManager.saveTag(treeItem.previousSibling().getValue());
                            contentManager.saveTag(treeItem.getValue());
                            sort(children);
                            checkTreeView.getSelectionModel().clearSelection();
                            checkTreeView.getSelectionModel().select(treeItem);
                        });
                        treeItemContextMenu.getItems().add(moveUp);
                    }

                    if (treeItem.nextSibling() != null) {
                        MenuItem moveDown = new MenuItem("Move Down");
                        moveDown.setOnAction(eventModeDown -> {
                            treeItem.nextSibling().getValue().setSort(treeItem.nextSibling().getValue().getSort() - 1);
                            treeItem.getValue().setSort(treeItem.getValue().getSort() + 1);
                            TreeItem<Tag> parentNode = (TreeItem<Tag>) treeItem.getParent();
                            ObservableList<TreeItem<Tag>> children = parentNode.getChildren();

                            contentManager.saveTag(treeItem.nextSibling().getValue());
                            contentManager.saveTag(treeItem.getValue());
                            sort(children);
                            checkTreeView.getSelectionModel().select(treeItem);
                        });
                        treeItemContextMenu.getItems().add(moveDown);
                    }

                    if (treeItem.previousSibling() != null || treeItem.nextSibling() != null) {
                        treeItemContextMenu.getItems().add(new SeparatorMenuItem());
                    }

                    if (treeItem != null) {
                        MenuItem edit = new MenuItem("Edit");
                        edit.setOnAction(eventEdit -> {
                            TextInputDialog dialog = new TextInputDialog(treeItem.getValue().getName());
                            dialog.setTitle("Edit tag");
                            dialog.setHeaderText("Edit Tag Dialog");
                            dialog.setContentText("Please enter tag name:");
                            Optional<String> result = dialog.showAndWait();
                            result.ifPresent(tagName -> {
                                Tag tag = treeItem.getValue();
                                tag.setName(tagName);

                                sort(treeItem.getParent().getChildren());

                                checkTreeView.getSelectionModel().select(treeItem);
                                contentManager.saveTag(tag);
                            });
                        });
                        treeItemContextMenu.getItems().add(edit);
                    }

                    MenuItem addNestedItem = new MenuItem("Add Nested Item");
                    addNestedItem.setOnAction(this::handle);
                    treeItemContextMenu.getItems().add(addNestedItem);

                    if (treeItem.isLeaf()) {
                        treeItemContextMenu.getItems().add(new SeparatorMenuItem());
                        MenuItem delete = new MenuItem("Delete");
                        delete.setOnAction(eventDelete -> {
                            checkTreeView.getSelectionModel().clearSelection();
                            Tag tag = treeItem.getValue();
                            treeItem.getParent().getChildren().remove(treeItem);
                            // remove relationship with other entities Content
                            tag.getContents().stream()
                                    .forEach(contentItem -> {
                                        contentItem.getTags().remove(tag);
                                        contentManager.saveContent(contentItem);
                                    });
                            contentManager.deleteTag(tag);
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
                Collections.sort(collection, (TreeItem<Tag> o1, TreeItem<Tag> o2)
                        -> Double.compare(((Tag) o1.getValue()).getSort(), ((Tag) o2.getValue()).getSort()));
                children.setAll(collection);
            }

            private void handle(ActionEvent e) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add new nested tag");
                dialog.setHeaderText("Add New Nested Tag Dialog");
                dialog.setContentText("Please enter tag name:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent( tagName -> {
                    CheckBoxTreeItem<Tag> treeIt = (treeItem != null) ? treeItem : (CheckBoxTreeItem<Tag>) checkTreeView.getRoot();
                    int parentId = treeIt.getValue().getId().intValue();
                    int sort = treeIt.getChildren().size();

                    Tag tag = new Tag();
                    tag.setName(tagName);
                    tag.setParentId(parentId);
                    tag.setSort(sort);

                    tag = contentManager.saveTag(tag);

                    CheckBoxTreeItem<Tag> treeItemNested = new CheckBoxTreeItem<>(tag);
                    treeItemNested.setIndependent(true);

                    checkBoxTreeList.add(treeItemNested);
                    treeIt.getChildren().add(treeItemNested);
                    checkTreeView.getSelectionModel().select(treeItemNested);
                });
            }
        });
        baobTab.getCheckBox().setOnAction(event -> checkTreeView.getCheckModel().clearChecks());
        baobTab.setContent(checkTreeView);

        return baobTab;
    }

    private CheckBoxTreeItem<Tag> recursiveCheckBoxTreeItem(Tag tag) {
        CheckBoxTreeItem<Tag> checkBoxTreeItem = new CheckBoxTreeItem<>(tag);
        checkBoxTreeItem.setIndependent(true);
        checkBoxTreeList.add(checkBoxTreeItem);

        tagList.stream()
                .filter(item -> item.getParentId() == tag.getId())
                .forEach(item -> checkBoxTreeItem.getChildren().add(recursiveCheckBoxTreeItem(item)));

        return checkBoxTreeItem;
    }

    private void createMenuItems() {
        // set up data
        editorMenuItem.setUserData(1);
        previewMenuItem.setUserData(2);
        sourceMenuItem.setUserData(4);
        sideBarMenuItem.setUserData(8);

        BooleanProperty obs = new SimpleBooleanProperty(false);
        ObservableBooleanValue previewBool = previewMenuItem.selectedProperty().isEqualTo(obs);
        ObservableBooleanValue sourceBool = sourceMenuItem.selectedProperty().isEqualTo(obs);

        editorMenuItem.disableProperty().bind(
                previewMenuItem.selectedProperty().isEqualTo(obs).and(sourceBool));
        previewMenuItem.disableProperty().bind(
                editorMenuItem.selectedProperty().isEqualTo(obs).and(sourceBool));
        sourceMenuItem.disableProperty().bind(
                editorMenuItem.selectedProperty().isEqualTo(obs).and(previewBool));

        editorMenuItem.setOnAction(weakMenuEventHandler);
        previewMenuItem.setOnAction(weakMenuEventHandler);
        sourceMenuItem.setOnAction(weakMenuEventHandler);
        sideBarMenuItem.setOnAction(weakMenuEventHandler);

        menu.addListener(weakMenuChangeListener);
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
                } catch (IOException e) {
                    e.printStackTrace();
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

        btn.setOnAction(new ThumbnailBtnActionEvent(webEditorEngine));
        btn.setOnContextMenuRequested(new ThumbnailBtnContextMenuEventHandler(contextMenu));
    }

    private Set<Tag> getSelectedTags() {
        Set<Tag> tags = new HashSet<>();
        checkBoxTreeList.stream()
                .filter(checkBoxTreeItem -> checkBoxTreeItem.isSelected())
                .map(checkBoxTreeItem -> checkBoxTreeItem.getValue())
                .forEach(tags::add);
        return tags;
    }

    private void createImageManager() {
        contextMenu = new ContextMenu();
        MenuItem contextMenuDelete = new MenuItem("Delete");
        contextMenuDelete.setOnAction(new ContextMenuDeleteEventHandler(content));
        contextMenu.getItems().add(contextMenuDelete);

        content.getImages().stream().forEach(imageItem -> {
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
                        Logger.getLogger(EditController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    return imageView;
                }
            };
            btn.disableProperty().bind(task.runningProperty());
            new Thread(task).start();

            btn.setOnAction(new ThumbnailBtnActionEvent(webEditorEngine));
            btn.setOnContextMenuRequested(new ThumbnailBtnContextMenuEventHandler(contextMenu));

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
                        .filter(file -> CheckImage.check(file.getName()))
                        .forEach(file -> addButtonImage(file, imagesTilePane));
            }
        });

        imagesTilePane.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent dragEvent) {
                final Dragboard dragboard = dragEvent.getDragboard();

                boolean isAccepted = dragboard.getFiles()
                        .stream()
                        .map(file -> file.getName().toLowerCase())
                        .noneMatch(name -> !CheckImage.check(name));

                if (dragboard.hasFiles() && isAccepted) {
//                    imagesTilePane.getStyleClass().add("tile-pane-drag-over");
                    imagesTilePane.setStyle(
                            "-fx-effect: innershadow(three-pass-box, green, 10, 0, 0, 0);"
                            + "-fx-background-color: #cfcfcf;"
                    );
                    dragEvent.acceptTransferModes(TransferMode.COPY);
                } else {
                    dragEvent.consume();
                }
            }
        });

        imagesTilePane.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent dragEvent) {
                final Dragboard db = dragEvent.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    db.getFiles().stream().forEach(file -> {
                        Platform.runLater(() -> {
                            addButtonImage(file, imagesTilePane);
                        });
                    });
                }
                dragEvent.setDropCompleted(success);
                dragEvent.consume();
            }
        });

        imagesTilePane.setOnDragExited(new EventHandler() {
            @Override
            public void handle(Event event) {
//                imagesTilePane.getStyleClass().remove("tile-pane-drag-over");
                imagesTilePane.setStyle("-fx-border-color: #C6C6C6;");
            }
        });
    }

    public void destroy() {
        // initData
        webEditorEngineChangeListener = null;
        weakDatabaseImageChangeListener = null;
        content = null;
        contextMenu = null;
        imagesTilePane = null;
        // initialize
        weakMenuChangeListener = null;
        menuEventHandler = null;
        menu = null;
        editorMenuItem = null;
        previewMenuItem = null;
        sourceMenuItem = null;
        sideBarMenuItem = null;

        resultChangeListener = null;
        result = null;
        webEditorEventHandler = null;
        webEditorEngine = null;

        System.gc();
        //UtilMemory.report();
    }
}
