package baobab.notebookfx;

import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFXLoader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Parent root = null;
        try {
            root = (Parent) SpringFXLoader.getInstance().loader("/fxml/Index.fxml").load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/index.css");

        stage.getIcons().addAll(
                new Image(getClass().getResource("/icons/app_icon256x256.png").toExternalForm()),
                new Image(getClass().getResource("/icons/app_icon128x128.png").toExternalForm()),
                new Image(getClass().getResource("/icons/app_icon64x64.png").toExternalForm()),
                new Image(getClass().getResource("/icons/app_icon48x48.png").toExternalForm()),
                new Image(getClass().getResource("/icons/app_icon32x32.png").toExternalForm()),
                new Image(getClass().getResource("/icons/app_icon24x24.png").toExternalForm()),
                new Image(getClass().getResource("/icons/app_icon16x16.png").toExternalForm())
        );

        stage.setOnCloseRequest(event -> {
            ApplicationContext applicationContext = SpringFXLoader.getInstance().getApplicationContex();

            applicationContext.getBean(ContentManager.class).close();
            //applicationContext.getBean(AppConfiguration.Shutdown.class).close();
        });
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setTitle("NotebookFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "true");
        launch(args);
    }
}
