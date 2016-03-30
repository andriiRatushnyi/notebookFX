package baobab.notebookfx.controls.handlers.edit;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;

public class ThumbnailBtnContextMenuEventHandler implements EventHandler<ContextMenuEvent> {

    private ContextMenu contextMenu;

    public ThumbnailBtnContextMenuEventHandler(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    @Override
    public void handle(ContextMenuEvent event) {
        Button btn = (Button) event.getTarget();
        contextMenu.getItems().get(0).setUserData(btn);
        contextMenu.show(btn, event.getScreenX(), event.getScreenY());
    }

}
