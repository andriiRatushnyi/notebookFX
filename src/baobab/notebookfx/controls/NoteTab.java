package baobab.notebookfx.controls;

import baobab.notebookfx.dialogs.TextInputDialog;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFX;
import java.util.LinkedList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.CheckTreeView;

public class NoteTab extends Tab {

    private boolean selected;
    private CheckBox checkBox = new CheckBox();
    private Label label = new Label();
    private ContentManager contentManager;

    public NoteTab(String tabName) {
        super();
        label.setText(tabName);
        label.setContentDisplay(ContentDisplay.RIGHT);
        setGraphic(label);
        setClosable(true);
    }

    public NoteTab(String tabName, ICheckTreeView ctv) {
        this(tabName);
        contentManager = SpringFX.getInstance()
                .getApplicationContex()
                .getBean(ContentManager.class);

        NoteTab baobTab = this;

        ContextMenu tabItemContextMenu = new ContextMenu();
        label.setOnContextMenuRequested(contextMenuEvent -> {
            tabItemContextMenu.getItems().clear();
            TabPane tabPane = (TabPane) label.getScene().lookup("#tagsTabPane");
//            Optional<Tab> tab = tabPane.getTabs()
//                    .stream()
//                    .filter(item -> item.equals(baobTab)).findFirst();
//            if (tab.isPresent()) {
//                tabPane.getSelectionModel().select(tab.get());
//            }
            (tabPane.getTabs()
                    .stream()
                    .filter(baobTab::equals)
                    .findFirst())
                    .ifPresent(tabPane.getSelectionModel()::select);
            
            CheckTreeView checkTreeView = (CheckTreeView) getContent();
            int tabIndex = tabPane.getSelectionModel().selectedIndexProperty().get();
            Tag currentTag = ((TreeItem<Tag>) checkTreeView.getRoot()).getValue();

            MenuItem edit = new MenuItem("Edit");
            edit.setOnAction(actionEvent
                    -> TextInputDialog
                            .create("Edit tab", "Edit Tab Dialog", "Please enter tab name:", label.getText())
                            .ifPresent(tabName1 -> {
                                if (tabName1.trim().equals("")) {
                                    return;
                                }
                                currentTag.setName(tabName1);
                                label.setText(tabName1);
                                contentManager.saveTag(currentTag);
                            })
            );
            MenuItem add = new MenuItem("Add");
            add.setOnAction(actionEvent -> {
                TextInputDialog
                        .create("Add tab", "Add Tab Dialog", "Please enter tab name:", "")
                        .ifPresent(tabName2 -> {
                            if (tabName2.trim().equals("")) {
                                return;
                            }
                            Tag parentTag = currentTag.getParent();
                            int index = parentTag.getChildren().lastIndexOf(currentTag);
                            int nextIndex = index + 1;

                            Tag newTag = new Tag();
                            newTag.setName(tabName2);
                            newTag.setParent(parentTag);
                            newTag.setChildren(new LinkedList<>());

                            newTag = contentManager.saveTag(newTag, nextIndex);
                            tabPane.getTabs().add(nextIndex, ctv.createCheckTreeView(newTag));
                        });
            });

            tabItemContextMenu.getItems().addAll(edit, add);

            if (checkTreeView.getRoot().isLeaf()) {
                tabItemContextMenu.getItems().add(new SeparatorMenuItem());
                MenuItem menuDelete = new MenuItem("Delete");
                menuDelete.setOnAction(actionEvent -> {
                    contentManager.deleteTag(currentTag);
                    tabPane.getTabs().remove(tabIndex);
                });
                tabItemContextMenu.getItems().add(menuDelete);
            }
            tabItemContextMenu.show(label, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        checkBox.setSelected(true);
        if (this.selected) {
            label.setGraphic(checkBox);
        } else {
            label.setGraphic(null);
        }
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public interface ICheckTreeView {

        NoteTab createCheckTreeView(Tag tag);
    }

}
