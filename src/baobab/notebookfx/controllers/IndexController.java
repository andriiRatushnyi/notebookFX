package baobab.notebookfx.controllers;

import baobab.notebookfx.NotebookFX;
import baobab.notebookfx.controls.ContentItemPane;
import baobab.notebookfx.controls.NoteTab;
import baobab.notebookfx.controls.cells.CheckTreeViewCell;
import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFX;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import static java.util.stream.Collectors.toSet;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javax.inject.Inject;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.markdownwriterfx.util.PrefsDoubleProperty;
import org.markdownwriterfx.util.PrefsIntegerProperty;
import org.markdownwriterfx.util.PrefsLongsProperty;
import org.markdownwriterfx.util.PrefsStringProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class IndexController implements Initializable, ContentItemPane.IClick {

    @Inject
    ContentManager contentManager;

    @FXML
    private CustomTextField searchTxtF;
    @FXML
    private TabPane tabPane;
    @FXML
    private Pagination pagination;
    @FXML
    private SplitPane splitter;

    private Tag rootTag;

    private Set<Tag> tagsSelected;

    private Preferences indexView;

    // 'searchText' property
    private static final PrefsStringProperty searchText = new PrefsStringProperty();

    public static String getSearchText() {
        return searchText.get();
    }

    public static void setSearchText(String search) {
        searchText.set(search);
    }

    public static StringProperty searchText() {
        return searchText;
    }

    // 'selectedTabPane' property
    private static final PrefsIntegerProperty selectedTabPane = new PrefsIntegerProperty();

    public static Integer getSelectedTabPane() {
        return selectedTabPane.get();
    }

    public static void setSelectedTabPane(Integer selected) {
        selectedTabPane.set(selected);
    }

    public static PrefsIntegerProperty selectedTabPane() {
        return selectedTabPane;
    }

    // 'selectedPagination' property
    private static final PrefsIntegerProperty selectedPagination = new PrefsIntegerProperty();

    public static Integer getSelectedPagination() {
        return selectedPagination.get();
    }

    public static void setSelectedPagination(Integer selected) {
        selectedPagination.set(selected);
    }

    public static PrefsIntegerProperty selectedPagination() {
        return selectedPagination;
    }

    // 'splitPanePosition' property
    private static final PrefsDoubleProperty splitPanePosition = new PrefsDoubleProperty();

    public static Double getSplitPanePosition() {
        return splitPanePosition.get();
    }

    public static void setSplitPanePosition(double splitPanePos) {
        splitPanePosition.set(splitPanePos);
    }

    public static DoubleProperty splitPanePosition() {
        return splitPanePosition;
    }

    // 'scrollingTabPane' property
//    private static final PrefsDoubleProperty scrollingTabPane = new PrefsDoubleProperty();
//
//    public static Double getScrollingTabPane() {
//        return scrollingTabPane.get();
//    }
//
//    public static void setScrollingTabPane(double scrollingTab) {
//        scrollingTabPane.set(scrollingTab);
//    }
//
//    public static DoubleProperty scrollingTabPane() {
//        return scrollingTabPane;
//    }
    // 'scrollingPagination' property
    private static final PrefsDoubleProperty scrollingPagination = new PrefsDoubleProperty();

    public static Double getScrollingPagination() {
        return scrollingPagination.get();
    }

    public static void setScrollingPagination(double scrollingPage) {
        scrollingPagination.set(scrollingPage);
    }

    public static DoubleProperty scrollingPagination() {
        return scrollingPagination;
    }

    // 'checkedTags' property
    private static final PrefsLongsProperty checkedTags = new PrefsLongsProperty();

    public static Long[] getCheckedTags() {
        return checkedTags.get();
    }

    public static void setCheckedTags(Long[] tags) {
        checkedTags.set(tags);
    }

    public static PrefsLongsProperty checkedTags() {
        return checkedTags;
    }

    // 'selectedTags' property
    private static final PrefsLongsProperty selectedTags = new PrefsLongsProperty();

    public static Long[] getSelectedTags() {
        return selectedTags.get();
    }

    public static void setSelectedTags(Long[] tags) {
        selectedTags.set(tags);
    }

    public static PrefsLongsProperty selectedTags() {
        return selectedTags;
    }
    // 'extendedTags' property
    private static final PrefsLongsProperty extendedTags = new PrefsLongsProperty();

    public static Long[] getExtendedTags() {
        return extendedTags.get();
    }

    public static void setExtendedTags(Long[] tags) {
        extendedTags.set(tags);
    }

    public static PrefsLongsProperty extendedTags() {
        return extendedTags;
    }

    @FXML
    private void handlerSearch(ActionEvent event) {
        String searchText = searchTxtF.getText();
        setSearchText(searchText);

        int count = contentManager.getCountContentBySearch(1, searchText, tagsSelected).intValue();
        // Hack for update result
        if (count == pagination.getPageCount() || count == 0) {
            pagination.setPageCount(pagination.getPageCount() + 1_000_000);
        }
        pagination.setPageCount(count > 0 ? count : 1);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tagsSelected = new HashSet<>();
        indexView = NotebookFX.getIndexView();

        rootTag = contentManager.findRootTree();

        // Cleaner button
        setupClearButtonField(searchTxtF);
        // Pagination
        createPagination();

        searchText.init(indexView, "searchText", "");
        searchTxtF.setText(getSearchText());

        selectedTabPane.init(indexView, "selectedTabPane", 0);

        rootTag.getChildren().forEach(item -> {
            NoteTab tab = createCheckTreeView(item);
            tabPane.getTabs().add(tab);

            if (rootTag.getChildren().indexOf(item) == getSelectedTabPane()) {
                tabPane.getSelectionModel().select(tab);
            }
        });

        // save state
        tabPane.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> setSelectedTabPane(tabPane.getSelectionModel().getSelectedIndex()));

        splitPanePosition.init(indexView, "splitPanePosition", .25);

        splitter.setDividerPosition(0, getSplitPanePosition());
        splitter.getDividers()
                .get(0)
                .positionProperty()
                .addListener((obs, o, n) -> setSplitPanePosition((double) n));

        selectedPagination.init(indexView, "selectedPagination", 1);
//        scrollingTabPane.init(indexView, "scrollingTabPane", 0);
        scrollingPagination.init(indexView, "scrollingPagination", 0);
        checkedTags.init(indexView, "checkedTags");
        extendedTags.init(indexView, "extendedTags");
        selectedTags.init(indexView, "selectedTags");
    }

    private void createPagination() {
        pagination.setPageCount(getSelectedPagination() + 1);
        pagination.setCurrentPageIndex(getSelectedPagination());

        pagination.setPageFactory(this::createPage);
    }

    private void setupClearButtonField(CustomTextField customTextField) {
        try {
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, customTextField, customTextField.rightProperty());
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private NoteTab createCheckTreeView(Tag tag) {
        NoteTab nt = new NoteTab(tag.getName());

        CheckBoxTreeItem<Tag> root = recursiveCheckBoxTreeItem(tag);
        root.setIndependent(true);
        root.setExpanded(true);

        CheckTreeView<Tag> checkTreeView = new CheckTreeView<>(root);
        checkTreeView.setShowRoot(false);

        checkTreeView.getCheckModel()
                .getCheckedItems()
                .addListener((ListChangeListener.Change<? extends TreeItem<Tag>> change) -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            tagsSelected.addAll(change.getList().stream()
                                    .map(TreeItem<Tag>::getValue)
                                    .collect(toSet()));
                        }
                        if (change.wasRemoved()) {
                            tagsSelected.removeAll(change.getRemoved().stream()
                                    .map(TreeItem<Tag>::getValue)
                                    .collect(toSet()));
                        }
                        setCheckedTags(tagsSelected.stream()
                                .map(Tag::getId)
                                .toArray(Long[]::new)
                        );
                        handlerSearch(null);
                    }

                    if (checkTreeView.getCheckModel().getCheckedItems().toArray().length > 0) {
                        nt.setSelected(true);
                    } else {
                        nt.setSelected(false);
                    }
                });

        checkTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> {
                    if (getSelectedTags() == null) {
                        return;
                    }
                    List<Long> tagIds = new ArrayList<>(Arrays.asList(getSelectedTags()));

                    if (o != null) {
                        Long oldId = o.getValue().getId();
                        if (tagIds.contains(oldId)) {
                            tagIds.remove(oldId);
                        }
                    }
                    Long newId = n.getValue().getId();

                    if (!tagIds.contains(newId)) {
                        tagIds.add(n.getValue().getId());
                    }

                    setSelectedTags(tagIds.toArray(new Long[0]));
                });

        if (getSelectedTags() != null) {
            List<Long> tagIds = new ArrayList<>(Arrays.asList(getSelectedTags()));
            List<TreeItem<Tag>> allTreeItem = new ArrayList<>();
            // get all treeItem
            getTreeItem(root, allTreeItem);

            (allTreeItem.stream()
                    .filter(item -> tagIds.contains(item.getValue().getId()))
                    .findFirst())
                    .ifPresent(checkTreeView.getSelectionModel()::select);

        }

        checkTreeView.setCellFactory(new CheckTreeViewCell());
        nt.getCheckBox().setOnAction(event -> checkTreeView.getCheckModel().clearChecks());
        nt.setContent(checkTreeView);

        return nt;
    }

    private static void getTreeItem(TreeItem<Tag> tag, List<TreeItem<Tag>> all) {
        tag.getChildren().stream().forEach(item -> {
            all.add(item);
            getTreeItem(item, all);
        });
    }

    private CheckBoxTreeItem<Tag> recursiveCheckBoxTreeItem(Tag tag) {
        CheckBoxTreeItem<Tag> checkBoxTreeItem = new CheckBoxTreeItem<>(tag);
        checkBoxTreeItem.setIndependent(true);
        if (getExtendedTags() != null && Arrays.asList(getExtendedTags()).contains(tag.getId())) {
            checkBoxTreeItem.setExpanded(true);
        };
        checkBoxTreeItem.expandedProperty().addListener((obs, o, n) -> {
            if (getExtendedTags() == null) {
                return;
            }
            List<Long> tagIds = new ArrayList<>(Arrays.asList(getExtendedTags()));
            Long id = checkBoxTreeItem.getValue().getId();
            if (o && tagIds.contains(id)) {
                tagIds.remove(id);
            }
            if (n && !tagIds.contains(id)) {
                tagIds.add(id);
            }
            setExtendedTags(tagIds.toArray(new Long[0]));
        });

        if (getCheckedTags() != null
                && Arrays.asList(getCheckedTags()).contains(tag.getId()) == true) {
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

        return checkBoxTreeItem;
    }

    private Node createPage(Integer pageIndex) {
        Page<Content> content = contentManager.getContentBySearch(pageIndex + 1, searchTxtF.getText(), tagsSelected);

        pagination.setPageCount(content.getTotalPages());
        pagination.getStyleClass().remove("empty");

        // set stage pagination
        setSelectedPagination(pagination.getCurrentPageIndex());

        if (content.getContent().isEmpty()) {
            pagination.getStyleClass().add("empty");
            return new Label("Not found!");
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        if (pageIndex == getSelectedPagination()) {
            scrollPane.setVvalue(getScrollingPagination());
        }
        scrollPane.vvalueProperty()
                .addListener((obs, o, n) -> setScrollingPagination((double) n));

        VBox vBox = new VBox();
        scrollPane.setContent(vBox);

        content.getContent()
                .stream()
                .map(contentItem -> ContentItemPane.content(contentItem, this))
                .forEach(vBox.getChildren()::add);

        return scrollPane;
    }

    public void destroy() {
        tabPane = null;
        pagination = null;
        splitter = null;
        rootTag = null;

        System.gc();
        //UtilMemory.report();
    }

    @Override
    public void click(String action, Long id) {
        try {
            FXMLLoader loader;
            Parent root;
            Scene scene = splitter.getScene();
            
            switch (action) {
                case "link":
                    destroy();
                    loader = SpringFX.getInstance().loader("/fxml/View.fxml");
                    
                    root = (Parent) loader.load();
                    
                    ViewController viewController = loader.<ViewController>getController();
                    viewController.initData(id);
                    
                    scene.setRoot(root);
                    break;
                case "edit":
                    destroy();
                    loader = SpringFX.getInstance().loader("/fxml/Edit.fxml");
                    
                    root = (Parent) loader.load();
                    
                    EditController editController = loader.<EditController>getController();
                    editController.initData(id);
                    
                    scene.setRoot(root);
                    break;
                case "delete":
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Dialog");
                    alert.setHeaderText("Delete Page");
                    alert.setContentText("Are you want delete page?");
                    
                    alert.showAndWait()
                            .filter(buttonType -> buttonType.equals(ButtonType.OK))
                            .ifPresent(buttonType -> {
                                boolean deleteResult = contentManager.deleteContent(id);
                                if (deleteResult == false) {
                                    Alert warning = new Alert(Alert.AlertType.WARNING);
                                    warning.setTitle("Warning Dialog");
                                    warning.setHeaderText("You cannot delete last page");
                                    warning.setContentText("Because you will not can create new pages!");
                                    
                                    warning.showAndWait();
                                }
                                destroy();
                                Parent root1 = (Parent) SpringFX.getInstance().load("/fxml/Index.fxml");
                                scene.setRoot(root1);
                            });
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

}
