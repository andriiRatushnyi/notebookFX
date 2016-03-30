package baobab.notebookfx.controls.handlers.edit;

import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;

public class MenuEventHandler implements EventHandler<ActionEvent> {

    private IntegerProperty menu;

    public MenuEventHandler(IntegerProperty menu) {
        this.menu = menu;
    }

    @Override
    public void handle(ActionEvent event) {
        CheckMenuItem menuItem = (CheckMenuItem) event.getTarget();
        int value = (Integer) menuItem.getUserData();
        menu.set(menuItem.isSelected() ? menu.get() + value : menu.get() - value);
    }

}
