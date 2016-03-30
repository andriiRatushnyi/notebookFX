package baobab.notebookfx.controls.listeners.edit;

import baobab.notebookfx.utils.UtilEOF;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class WebEditorEngineChangeListener implements ChangeListener<Worker.State> {

    WebEngine webEditorEngine;
    StringProperty result;
    String content;

    /**
     *  Listeners for set content in Web Engine and listener back StringProperty result
     *
     * @param webEditorEngine WebEngine
     * @param result StringProperty
     * @param content String
     */
    public WebEditorEngineChangeListener(WebEngine webEditorEngine, StringProperty result, String content) {
        this.webEditorEngine = webEditorEngine;
        this.result = result;
        this.content = content;
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue == Worker.State.SUCCEEDED) {
            JSObject win = (JSObject) webEditorEngine.executeScript("window");
            win.setMember("fx", result);
            webEditorEngine.executeScript(" setValue(\"" + UtilEOF.escape(content) + "\") ");
        }
    }

}
