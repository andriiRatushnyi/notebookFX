package baobab.notebookfx.controls;

import baobab.notebookfx.controllers.EditController;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFXLoader;
import java.util.Optional;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ContextMenuEvent;
import org.controlsfx.control.CheckTreeView;

public class BaobTab extends Tab {

    public boolean selected;
    public CheckBox cb = new CheckBox();
    public Label label = new Label();
    private ContentManager contentManager;

    public BaobTab(String tabName) {
        super();
        label.setText(tabName);
        label.setContentDisplay(ContentDisplay.RIGHT);
        setGraphic(label);
        setClosable(true);
    }

    public BaobTab(String tabName, EditController controller) {
        this(tabName);
        this.contentManager = SpringFXLoader.getInstance()
                                            .getApplicationContex()
                                            .getBean(ContentManager.class);

        BaobTab baobTab = this;

        ContextMenu tabItemContextMenu = new ContextMenu();
        label.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                tabItemContextMenu.getItems().clear();

                Scene scene = label.getScene();
                TabPane tabPane = (TabPane) scene.lookup("#tagsTabPane");
                Optional<Tab> tab = tabPane.getTabs().stream()
                        .filter(item -> item.equals(baobTab))
                        .findFirst();

                if (tab.isPresent()) {
                    tabPane.getSelectionModel().select(tab.get());
                }

                CheckTreeView checkTreeView = (CheckTreeView) getContent();

                int tabIndex = tabPane.getSelectionModel().selectedIndexProperty().get();
                Tag currentTag = ((TreeItem<Tag>) checkTreeView.getRoot()).getValue();

                MenuItem edit = new MenuItem("Edit");
                edit.setOnAction(eventEdit -> {
                    TextInputDialog dialog = new TextInputDialog(label.getText());
                    dialog.setTitle("Edit tab");
                    dialog.setHeaderText("Edit Tab Dialog");
                    dialog.setContentText("Please enter tab name:");
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(tabName -> {
                        currentTag.setName(tabName);
                        label.setText(tabName);
                        contentManager.saveTag(currentTag);
                    });
                });
                tabItemContextMenu.getItems().add(edit);

                MenuItem add = new MenuItem("Add");
                add.setOnAction(eventAdd -> {
                    TextInputDialog addDialog = new TextInputDialog();
                    addDialog.setTitle("Add tab");
                    addDialog.setHeaderText("Add Tab Dialog");
                    addDialog.setContentText("Please enter tab name:");
                    Optional<String> result = addDialog.showAndWait();
                    result.ifPresent(tabName -> {
                        // increase sort in tags
                        contentManager.getTagsByParentId(0).stream()
                                .filter(item -> item.getSort() > tabIndex)
                                .forEach(item -> {
                                    item.setSort(item.getSort() + 1);
                                    contentManager.saveTag(item);
                                });
                        Tag newTag = new Tag();
                        newTag.setName(tabName);
                        newTag.setParentId(0);
                        newTag.setSort(tabIndex + 1);
                        newTag = contentManager.saveTag(newTag);
                        tabPane.getTabs().add(newTag.getSort(), controller.createCheckTreeView(newTag));
                    });
                });
                tabItemContextMenu.getItems().add(add);

                if (checkTreeView.getRoot().isLeaf()) {
                    tabItemContextMenu.getItems().add(new SeparatorMenuItem());
                    MenuItem menuDelete = new MenuItem("Delete");
                    menuDelete.setOnAction(eventDelete -> {
                        contentManager.deleteTag(currentTag);
                        tabPane.getTabs().remove(tabIndex);
                        // decrease sort in tags
                        contentManager.getTagsByParentId(0).stream()
                                .filter(item -> item.getSort() > tabIndex)
                                .forEach(item -> {
                                    item.setSort(item.getSort() - 1);
                                    contentManager.saveTag(item);
                                });
                    });
                    tabItemContextMenu.getItems().add(menuDelete);
                }
                tabItemContextMenu.show(label, event.getScreenX(), event.getScreenY());
            }
        });
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        cb.setSelected(true);
        this.selected = selected;
        if (selected) {
            label.setGraphic(cb);
        } else {
            label.setGraphic(null);
        }
    }

    public CheckBox getCheckBox() {
        return cb;
    }

}
