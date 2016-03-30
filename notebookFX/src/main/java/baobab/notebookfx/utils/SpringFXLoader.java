package baobab.notebookfx.utils;

import baobab.notebookfx.config.AppConfiguration;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringFXLoader {

    private static ApplicationContext applicationContext;
    private static SpringFXLoader instance;

    private SpringFXLoader() {
        applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
    }

    public static synchronized SpringFXLoader getInstance(){
        if (instance == null) {
            instance = new SpringFXLoader();
        }
        return instance;
    }

    public FXMLLoader loader(String url) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(clazz -> applicationContext.getBean(clazz));
        loader.setLocation(getClass().getResource(url));
        return loader;
    }

    public FXMLLoader loader(String url, String resources) {
        FXMLLoader loader = loader(url);
        loader.setResources(ResourceBundle.getBundle(resources));
        return loader;
    }

    public ApplicationContext getApplicationContex() {
        return applicationContext;
    }
}
