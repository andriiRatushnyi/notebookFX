package baobab.notebookfx.controls.handlers.edit;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SplitPane;

public class MenuChangeListener implements ChangeListener<Integer> {

    private final SplitPane editorsSpliter;
    private final SplitPane sideBarSpliter;

    private final Map<Integer, double[][]> menuSetting = new HashMap<>();

    {
        menuSetting.put(1,  new double[][]{ {1.0, 1.0}, {1.0} });
        menuSetting.put(2,  new double[][]{ { .0, 1.0}, {1.0} });
        menuSetting.put(3,  new double[][]{ { .5, 1.0}, {1.0} });
        menuSetting.put(4,  new double[][]{ { .0,  .0}, {1.0} });
        menuSetting.put(5,  new double[][]{ { .5,  .5}, {1.0} });
        menuSetting.put(6,  new double[][]{ { .0,  .5}, {1.0} });
        menuSetting.put(7,  new double[][]{ {.33, .67}, {1.0} });
        menuSetting.put(9,  new double[][]{ {1.0, 1.0}, {.67} });
        menuSetting.put(10, new double[][]{ { .0, 1.0}, {.67} });
        menuSetting.put(11, new double[][]{ { .5, 1.0}, {.67} });
        menuSetting.put(12, new double[][]{ { .0,  .0}, {.67} });
        menuSetting.put(13, new double[][]{ { .5,  .5}, {.67} });
        menuSetting.put(14, new double[][]{ { .0,  .5}, {.67} });
        menuSetting.put(15, new double[][]{ {.33, .67}, {.67} });
    }

    public MenuChangeListener(SplitPane editorsSpliter, SplitPane sideBarSpliter) {
        this.editorsSpliter = editorsSpliter;
        this.sideBarSpliter = sideBarSpliter;
    }

    @Override
    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        double[][] item = menuSetting.get(newValue);
        editorsSpliter.setDividerPositions(item[0]);
        sideBarSpliter.setDividerPositions(item[1]);
    }

}
