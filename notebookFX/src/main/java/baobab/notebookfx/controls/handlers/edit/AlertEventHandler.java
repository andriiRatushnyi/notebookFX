package baobab.notebookfx.controls.handlers.edit;

import javafx.event.EventHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.web.WebEvent;

public class AlertEventHandler implements EventHandler<WebEvent<String>> {

    /**
     *  Retrieve copy event via javascript:alert
     *  http://stackoverflow.com/questions/25675231/function-undefined-error-in-javafx-application/25676561#25676561
     *
     * @param event
     */
    @Override
    public void handle(WebEvent<String> event) {
        if (event.getData() != null && event.getData().startsWith("copy: ")) {
            // COPY
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent copyContent = new ClipboardContent();
            copyContent.putString(event.getData().substring(6));
            clipboard.setContent(copyContent);
        }
    }

}
