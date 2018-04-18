package baobab.notebookfx;

import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.OS;
import baobab.notebookfx.utils.SpringFX;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.prefs.Preferences;
import static java.util.stream.Collectors.toList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.markdownwriterfx.options.Options;
import org.markdownwriterfx.util.StageState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class NotebookFX extends Application {

    @SuppressWarnings("unused")
    private StageState stageState;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Options.load(getOptions());

        Parent root = (Parent) SpringFX.getInstance().load("/fxml/Index.fxml");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/style.bss");
//        scene.getStylesheets().add("/css/style.css");

        primaryStage.getIcons()
                .addAll(Arrays.asList(16, 24, 32, 48, 64, 128, 256)
                        .stream()
                        .map(i -> "/icons/app_icon" + i + "x" + i + ".png")
                        .map(getClass()::getResource)
                        .map(URL::toExternalForm)
                        .map(Image::new)
                        .collect(toList())
                );

        primaryStage.setOnCloseRequest(event -> {
            ApplicationContext applicationContext = SpringFX.getInstance().getApplicationContex();
            applicationContext.getBean(ContentManager.class).close();
        });

        stageState = new StageState(primaryStage, getState());

        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setTitle("NotebookFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        File database = new File("notebookDB.mv.db");
        if (!Files.exists(database.toPath())) {
            System.setProperty("database.create", "true");
        }
        if (OS.isUnix()) {
            System.setProperty("prism.lcdtext", "false");
            //System.setProperty("prism.text", "t2k");
        };
        launch(args);
    }

    static private Preferences getPrefsRoot() {
        return Preferences.userRoot().node("notebookFX");
    }

    static Preferences getOptions() {
        return getPrefsRoot().node("options");
    }

    public static Preferences getState() {
        return getPrefsRoot().node("state");
    }

    public static Preferences getIndexView() {
        return getPrefsRoot().node("index-view");
    }

    public static Preferences getEditView() {
        return getPrefsRoot().node("edit-view");
    }

    public static Preferences getViewView() {
        return getPrefsRoot().node("view-view");
    }

}
