package baobab.notebookfx.controllers;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFXLoader;
import baobab.notebookfx.utils.UtilHtml;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.web.WebView;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class ViewController implements Initializable {

    @Inject
    ContentManager contentManager;

    @FXML
    private WebView webView;
    @FXML
    private Label title;

    private Content content;

    @FXML
    private void handlerBack(ActionEvent event) {
        try {
            Scene scene = ((Node) event.getSource()).getScene();
            Parent root = SpringFXLoader.getInstance().loader("/fxml/Index.fxml").load();
            scene.setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handlerEdit(ActionEvent event) {
        try {
            Scene scene = ((Node) event.getSource()).getScene();
            FXMLLoader loader = SpringFXLoader.getInstance().loader("/fxml/Edit.fxml");

            Parent root = (Parent) loader.load();

            EditController controller = loader.<EditController>getController();
            controller.initData(content.getId());

            scene.setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initData(Long pageId) {
        content = contentManager.getContent(pageId);

        content.setViewCount(content.getViewCount() + 1);
        contentManager.saveContent(content);

        title.setText(content.getTitle());
        title.setTooltip(new Tooltip(content.getTitle()));
        webView.getEngine().loadContent(UtilHtml.parse(content.getContent()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

}
