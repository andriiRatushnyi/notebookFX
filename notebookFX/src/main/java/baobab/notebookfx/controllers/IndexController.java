package baobab.notebookfx.controllers;

import baobab.notebookfx.controls.BaobTab;
import baobab.notebookfx.controls.ContentItemPane;
import baobab.notebookfx.controls.cells.CheckTreeViewCell;
import baobab.notebookfx.controls.handlers.index.PaginationEventFilter;
import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.models.states.PageState;
import baobab.notebookfx.services.ContentManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javax.inject.Inject;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class IndexController implements Initializable {

    @Inject
    ContentManager contentManager;

    @Inject
    PageState pageState;

    @FXML
    private CustomTextField searchTxtF;
    @FXML
    private TabPane tabPane;
    @FXML
    private Pagination pagination;
    @FXML
    private SplitPane splitter;

    private PaginationEventFilter paginationEventFilter;

    private WeakEventHandler weakPaginationEventFilter;

    private ObservableList<Tag> tagList;

    private Set<CheckBoxTreeItem<Tag>> checkBoxTreeList;

    @FXML
    private void handlerSearch(ActionEvent event) {
        String searchText = searchTxtF.getText();
        // save state
        pageState.setSearchText(searchText);

        int count = contentManager.getCountContentBySearch(1, searchText, getSelectedTags()).intValue();
        // Hack for update result
        if (count == pagination.getPageCount() || count == 0) {
            pagination.setPageCount(pagination.getPageCount() + 1_000_000);
        }
        pagination.setPageCount(count > 0 ? count : 1);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checkBoxTreeList = new HashSet<>();
        // Database
        tagList = FXCollections.observableArrayList(contentManager.findTags());
        // Cleaner button
        setupClearButtonField(searchTxtF);
        // Pagination
        paginationEventFilter = new PaginationEventFilter(this);
        weakPaginationEventFilter = new WeakEventHandler(paginationEventFilter);
        createPagination();

        // set state
        searchTxtF.setText(pageState.getSearchText());

        tagList.stream().filter(item -> item.getParentId() == 0)
                .forEach(item -> {
                    BaobTab tab = createCheckTreeView(item);
                    tabPane.getTabs().add(tab);
                    // set state
                    if (item.getSort() == (pageState.getTabId() + 1)) {
                        tabPane.getSelectionModel().select(tab);
                    }
                });

        checkBoxTreeList.stream().filter(
                item -> pageState.getSelectedTags().contains(item.getValue().getId())
        ).forEach(treeViewItem -> treeViewItem.setSelected(true));

        checkBoxTreeList.stream()
                .forEach(checkBoxTreeItem -> {
                    // save state
                    checkBoxTreeItem.selectedProperty()
                            .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                                Long id = checkBoxTreeItem.getValue().getId();
                                if (newValue) {
                                    pageState.getSelectedTags().add(id);
                                } else {
                                    pageState.getSelectedTags().remove(id);
                                }
                                handlerSearch(null);
                            });
                });
        // save state
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
                    pageState.setTabId(tabPane.getSelectionModel().getSelectedIndex());
                });

        splitter.setDividerPosition(0, pageState.getSplitter());
        splitter.getDividers().get(0).positionProperty()
                .addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    pageState.setSplitter((double) newValue);
                });
    }

    private void createPagination() {
        pagination.setPageCount(pageState.getPageId() + 1);
        // set state
        pagination.setCurrentPageIndex(pageState.getPageId());

        pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
        pagination.addEventFilter(MouseEvent.MOUSE_CLICKED, weakPaginationEventFilter);
    }

    private void setupClearButtonField(CustomTextField customTextField) {
        try {
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, customTextField, customTextField.rightProperty());
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          e.printStackTrace();
        }
    }

    private BaobTab createCheckTreeView(Tag tag) {
        BaobTab tb = new BaobTab(tag.getName());
        CheckBoxTreeItem<Tag> root = recursiveCheckBoxTreeItem(tag);
        root.setIndependent(true);
        root.setExpanded(true);

        CheckTreeView<Tag> checkTreeView = new CheckTreeView<>(root);
        checkTreeView.setShowRoot(false);
        checkTreeView.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener.Change<? extends TreeItem<Tag>> t) -> {
                    if (checkTreeView.getCheckModel().getCheckedItems().toArray().length > 0) {
                        tb.setSelected(true);
                    } else {
                        tb.setSelected(false);
                    }
                });
        checkTreeView.setCellFactory(new CheckTreeViewCell());
        tb.getCheckBox().setOnAction(event -> checkTreeView.getCheckModel().clearChecks());
        tb.setContent(checkTreeView);
        return tb;
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

    private Node createPage(Integer pageIndex) {
        Page<Content> content = contentManager.getContentBySearch(pageIndex + 1, searchTxtF.getText(), getSelectedTags());

        pagination.setPageCount(content.getTotalPages());
        pagination.getStyleClass().remove("empty");

        // set stage pagination
        pageState.setPageId(pagination.getCurrentPageIndex());

        if (content.getContent().isEmpty()) {
            pagination.getStyleClass().add("empty");
            return new Label("Not found!");
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        if (pageIndex == pageState.getPageId()) {
            scrollPane.setVvalue(pageState.getPageScroll());
        }
        scrollPane.vvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            pageState.setPageScroll((double) newValue);
        });

        VBox vBox = new VBox();
        scrollPane.setContent(vBox);

        content.getContent().stream()
                .forEach(item -> vBox.getChildren().add(ContentItemPane.content(item, tagList)));

        return scrollPane;
    }

    private Set<Tag> getSelectedTags() {
        return tagList.stream()
                .filter(tag -> pageState.getSelectedTags().contains(tag.getId()))
                .collect(toSet());
    }

    public void destroy() {
        tabPane = null;
        pagination = null;
        splitter = null;
        paginationEventFilter = null;
        checkBoxTreeList = null;
        tagList = null;

        System.gc();
        //UtilMemory.report();
    }

}
