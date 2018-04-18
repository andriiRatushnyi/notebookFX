package baobab.notebookfx.dialogs;

import java.util.Optional;

public class TextInputDialog {

    /**
     * Wrapper for TextInputDialog
     *
     * @param title
     * @param headerText
     * @param contentText
     * @param editText
     * @return
     */
    public static Optional<String> create(
            String title,
            String headerText,
            String contentText,
            String editText
    ) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(editText);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        return dialog.showAndWait();
    }

}
