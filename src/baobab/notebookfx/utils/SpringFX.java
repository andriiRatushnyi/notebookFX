package baobab.notebookfx.utils;

import baobab.notebookfx.config.AppConfiguration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringFX {

    private static ApplicationContext applicationContext;
    private static SpringFX instance;

    private SpringFX() {
        applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
    }

    public static synchronized SpringFX getInstance() {
        if (instance == null) {
            instance = new SpringFX();
        }
        return instance;
    }

    public FXMLLoader loader(String url) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        loader.setControllerFactory(clazz -> applicationContext.getBean(clazz));
        return loader;
    }

    public FXMLLoader loader(String url, String resources) {
        FXMLLoader loader = loader(url);
        loader.setResources(ResourceBundle.getBundle(resources));
        return loader;
    }
    
    public <T> T load(String url) {
        try {
            return loader(url).<T>load();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public <T> T load(String url, String resources) {
        try {
            return loader(url, resources).<T>load();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ApplicationContext getApplicationContex() {
        return applicationContext;
    }
}
