package org.markdownwriterfx.util;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
//import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;

/**
 * Action utilities
 *
 * @author Karl Tauber
 */
public class ActionUtils {

    public static Menu createMenu(String text, Action... actions) {
        return new Menu(text, null, createMenuItems(actions));
    }

    public static MenuItem[] createMenuItems(Action... actions) {
        MenuItem[] menuItems = new MenuItem[actions.length];
        for (int i = 0; i < actions.length; i++) {
            menuItems[i] = (actions[i] != null)
                    ? createMenuItem(actions[i])
                    : new SeparatorMenuItem();
        }
        return menuItems;
    }

    public static MenuItem createMenuItem(Action action) {
        MenuItem menuItem = (action.selected != null) ? new CheckMenuItem(action.text) : new MenuItem(action.text);
        if (action.accelerator != null) {
            menuItem.setAccelerator(action.accelerator);
        }
//        if (action.icon != null) {
//            menuItem.setGraphic(FontAwesomeIconFactory.get().createIcon(action.icon));
//        }
        menuItem.setOnAction(action.action);
        if (action.disable != null) {
            menuItem.disableProperty().bind(action.disable);
        }
        if (action.selected != null) {
            ((CheckMenuItem) menuItem).selectedProperty().bindBidirectional(action.selected);
        }
        return menuItem;
    }

    public static ToolBar createToolBar(Action... actions) {
        return new ToolBar(createToolBarButtons(actions));
    }

    public static Node[] createToolBarButtons(Action... actions) {
        Node[] buttons = new Node[actions.length];
        for (int i = 0; i < actions.length; i++) {
            buttons[i] = (actions[i] != null)
                    ? createToolBarButton(actions[i])
                    : new Separator();
        }
        return buttons;
    }

    public static ButtonBase createToolBarButton(Action action) {
        ButtonBase button = (action.selected != null) ? new ToggleButton() : new Button();
//        button.setGraphic(FontAwesomeIconFactory.get().createIcon(action.icon, "1.2em"));
        String tooltip = action.text;
        if (tooltip.endsWith("...")) {
            tooltip = tooltip.substring(0, tooltip.length() - 3);
        }
        if (action.accelerator != null) {
            tooltip += " (" + action.accelerator.getDisplayText() + ')';
        }
        button.setTooltip(new Tooltip(tooltip));
        button.setFocusTraversable(false);
        button.setOnAction(action.action);
        if (action.disable != null) {
            button.disableProperty().bind(action.disable);
        }
        if (action.selected != null) {
            ((ToggleButton) button).selectedProperty().bindBidirectional(action.selected);
        }
        return button;
    }
}
