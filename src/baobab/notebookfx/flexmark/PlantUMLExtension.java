package baobab.notebookfx.flexmark;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.options.MutableDataHolder;

public class PlantUMLExtension implements HtmlRenderer.HtmlRendererExtension {

    @Override
    public void rendererOptions(final MutableDataHolder options) {

    }

    @Override
    public void extend(final HtmlRenderer.Builder rendererBuilder, final String rendererType) {
        rendererBuilder.nodeRendererFactory(new PlantUML.PlantUMLRenderer.Factory());
    }

    public static PlantUMLExtension create() {
        return new PlantUMLExtension();
    }
}
