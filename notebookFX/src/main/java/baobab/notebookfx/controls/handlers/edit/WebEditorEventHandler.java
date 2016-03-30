package baobab.notebookfx.controls.handlers.edit;

import baobab.notebookfx.utils.UtilEOF;
import javafx.event.EventHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;

public class WebEditorEventHandler implements EventHandler<KeyEvent> {

    private WebEngine webEditorEngine;

    public WebEditorEventHandler(WebEngine webEditorEngine) {
        this.webEditorEngine = webEditorEngine;
    }

    /**
     * Capture Ctrl+V event and process it
     * http://stackoverflow.com/questions/25675231/function-undefined-error-in-javafx-application/25676561#25676561
     *
     * @param event
     */
    @Override
    public void handle(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.V) {
                // PASTE
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                String pasteContent = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
                webEditorEngine.executeScript(" pasteContent(\"" + UtilEOF.escape(pasteContent) + "\") ");
            }
    }

}
