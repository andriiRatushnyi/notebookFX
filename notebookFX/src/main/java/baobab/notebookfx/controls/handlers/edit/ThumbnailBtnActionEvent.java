package baobab.notebookfx.controls.handlers.edit;

import baobab.notebookfx.models.Image;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;

public class ThumbnailBtnActionEvent implements EventHandler<ActionEvent> {

    private WebEngine webEditorEngine;

    public ThumbnailBtnActionEvent(WebEngine webEditorEngine) {
        this.webEditorEngine = webEditorEngine;
    }

    @Override
    public void handle(ActionEvent event) {
        Image image = (Image) ((Button) event.getTarget()).getUserData();
        webEditorEngine.executeScript(" injectText(\"IMG#" + image.getId() + "#IMG\") ");
    }

}
