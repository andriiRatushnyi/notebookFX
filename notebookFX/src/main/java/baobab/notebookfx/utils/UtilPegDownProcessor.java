package baobab.notebookfx.utils;

import baobab.notebookfx.pegdown.ImageParser;
import baobab.notebookfx.pegdown.ImageSerializer;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

public class UtilPegDownProcessor {

    private final PegDownProcessor pegDownProcessor;
    private static UtilPegDownProcessor instance;

    private UtilPegDownProcessor() {
        PegDownPlugins imagePlugin = new PegDownPlugins.Builder()
                .withPlugin(ImageParser.class)
                .withHtmlSerializer(
                        (ToHtmlSerializerPlugin) new ImageSerializer()
                ).build();

        this.pegDownProcessor = new PegDownProcessor(Extensions.ALL, imagePlugin);
    }

    public PegDownProcessor getPegDownProcessor() {
        return pegDownProcessor;
    }


    public static synchronized UtilPegDownProcessor getInstance() {
        if (instance == null) {
            instance = new UtilPegDownProcessor();
        }
        return instance;
    }

    public static String parse(String value) {
        return UtilPegDownProcessor.getInstance().getPegDownProcessor()
                        .markdownToHtml(value);
    }
}
