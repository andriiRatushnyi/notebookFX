package baobab.notebookfx.controls.listeners.edit;

import baobab.notebookfx.utils.UtilHtml;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;

public class ResultChangeListener implements ChangeListener<String> {

    private final WebEngine webEngine;

    public ResultChangeListener(WebEngine webEngine) {

        this.webEngine = webEngine;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        webEngine.loadContent(UtilHtml.parse(newValue));
    }

}
