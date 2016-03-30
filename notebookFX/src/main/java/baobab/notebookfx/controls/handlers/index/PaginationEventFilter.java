package baobab.notebookfx.controls.handlers.index;

import baobab.notebookfx.controllers.EditController;
import baobab.notebookfx.controllers.IndexController;
import baobab.notebookfx.controllers.ViewController;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFXLoader;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;

public class PaginationEventFilter implements EventHandler<MouseEvent> {

    private IndexController controller;

    public PaginationEventFilter(IndexController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(MouseEvent event) {
        String switcher = "";
        Scene scene = ((Node) event.getSource()).getScene();
        Node node = (Node) event.getTarget();
        Parent parent = node.getParent();

        if (node instanceof Button || parent instanceof Button) {
            if (parent instanceof Button) {
                node = parent;
            }
            switcher = "button";
        } else if (node instanceof Hyperlink || parent instanceof Hyperlink) {
            if (parent instanceof Hyperlink) {
                node = parent;
            }
            switcher = "hyperlink";
        }

        try {
            switch (switcher) {
                case "button":
                    ObservableList<String> style = node.getStyleClass();
                    if (style.contains("edit")) {
                        controller.destroy();
                        FXMLLoader loader = SpringFXLoader.getInstance().loader("/fxml/Edit.fxml");

                        Parent root = (Parent) loader.load();

                        EditController controller = loader.<EditController>getController();
                        controller.initData(Long.valueOf(node.getId()));

                        scene.setRoot(root);
                    } else if (style.contains("delete")) {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation Dialog");
                        alert.setHeaderText("Delete Page");
                        alert.setContentText("Are you want delete page?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            ContentManager contentManager = SpringFXLoader.getInstance()
                                                                          .getApplicationContex()
                                                                          .getBean(ContentManager.class);
                            boolean deleteResult = contentManager.deleteContent(Long.valueOf(node.getId()));
                            if (deleteResult == false) {
                                Alert warning = new Alert(AlertType.WARNING);
                                warning.setTitle("Warning Dialog");
                                warning.setHeaderText("You cannot delete last page");
                                warning.setContentText("Because you will not can create new pages!");

                                warning.showAndWait();
                            }
                            controller.destroy();
                            Parent root = (Parent) SpringFXLoader.getInstance().loader("/fxml/Index.fxml").load();
                            scene.setRoot(root);
                        }
                    }
                    break;
                case "hyperlink":
                    controller.destroy();
                    FXMLLoader loader = SpringFXLoader.getInstance().loader("/fxml/View.fxml");

                    Parent root = (Parent) loader.load();

                    ViewController controller = loader.<ViewController>getController();
                    controller.initData(Long.valueOf(node.getId()));

                    scene.setRoot(root);
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(IndexController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
