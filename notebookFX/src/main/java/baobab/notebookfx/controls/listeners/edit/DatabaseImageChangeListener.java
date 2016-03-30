package baobab.notebookfx.controls.listeners.edit;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Image;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFXLoader;
import javafx.collections.SetChangeListener;

public class DatabaseImageChangeListener implements SetChangeListener<Image> {

    private final ContentManager contentManager = SpringFXLoader.getInstance()
                                                                .getApplicationContex()
                                                                .getBean(ContentManager.class);
    private final Content content;

    public DatabaseImageChangeListener(Content content) {
        this.content = content;
    }

    @Override
    public void onChanged(SetChangeListener.Change<? extends Image> change) {
        if (change.wasAdded()) {
            contentManager.saveContent(content);
        } else if (change.wasRemoved()) {
            contentManager.saveContent(content);
            contentManager.deleteImages();
        }
    }

}
