package org.markdownwriterfx.editor;

import javafx.scene.control.ContextMenu;
import org.markdownwriterfx.util.Messages;
import org.markdownwriterfx.util.Action;
import org.markdownwriterfx.util.ActionUtils;

class SmartEditActions {

    static void initContextMenu(MarkdownEditorPane editor, ContextMenu contextMenu) {
        Action cutAction = new Action(Messages.get("MainWindow.editCutAction"), "Shortcut+X", /*null,*/
                e -> editor.cut());
        Action copyAction = new Action(Messages.get("MainWindow.editCopyAction"), "Shortcut+C", /*null,*/
                e -> editor.copy());
        Action pasteAction = new Action(Messages.get("MainWindow.editPasteAction"), "Shortcut+V", /*null,*/
                e -> editor.paste());

        contextMenu.getItems().addAll(ActionUtils.createMenuItems(
                cutAction,
                copyAction,
                pasteAction));
    }

    static void updateContextMenu(MarkdownEditorPane editor, ContextMenu contextMenu, int characterIndex) {
    }
}
