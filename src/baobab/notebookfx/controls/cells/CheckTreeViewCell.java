package baobab.notebookfx.controls.cells;

import baobab.notebookfx.models.Tag;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;

public class CheckTreeViewCell implements Callback<TreeView<Tag>, TreeCell<Tag>> {

    @Override
    public TreeCell<Tag> call(TreeView<Tag> t) {
        return new CheckBoxTreeCell<Tag>() {

            @Override
            public void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                }
            }
        };
    }
}
