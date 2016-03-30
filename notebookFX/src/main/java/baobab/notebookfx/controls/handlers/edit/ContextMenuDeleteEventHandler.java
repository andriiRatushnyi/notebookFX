package baobab.notebookfx.controls.handlers.edit;

import baobab.notebookfx.models.Content;
import java.util.Objects;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.TilePane;

public class ContextMenuDeleteEventHandler implements EventHandler<ActionEvent> {

    private final Content content;

    public ContextMenuDeleteEventHandler(Content content) {
        this.content = content;
    }

    @Override
    public void handle(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Image");
        alert.setContentText("Are you want delete image?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Button currentButton = (Button) ((MenuItem) event.getTarget()).getUserData();
            baobab.notebookfx.models.Image image = (baobab.notebookfx.models.Image) currentButton.getUserData();

            content.imagesProperty().get()
                    .remove(content.getImages()
                                        .stream()
                                        .filter(imageItem -> Objects.equals(image.getId(), imageItem.getId()))
                                        .findFirst().get());
            ((TilePane) currentButton.getParent()).getChildren().remove(currentButton);
        }
    }
}
