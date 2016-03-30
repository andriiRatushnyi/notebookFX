package baobab.notebookfx.pegdown;

import baobab.notebookfx.models.Image;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFXLoader;
import javax.xml.bind.DatatypeConverter;
import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

public class ImageSerializer implements ToHtmlSerializerPlugin {

    private ContentManager contentManager;

    public ImageSerializer() {
        this.contentManager = SpringFXLoader.getInstance().getApplicationContex().getBean(ContentManager.class);
    }

    @Override
    public boolean visit(Node node, Visitor visitor, Printer printer) {
        if (node instanceof ImageNode) {
            ImageNode imageNode = (ImageNode) node;
            Long id = Long.decode(imageNode.getName());
            Image image = contentManager.getImage(id);
            if (image != null) {
                printer.print("<img src=\"data:"
                        + image.getType()
                        + ";base64,"
                        + DatatypeConverter.printBase64Binary(image.getContent())
                        + "\" >");
                printer.println();
            } else {
                printer.print("Image not found!");
                printer.println();
            }

            return true;
        }
        return false;
    }

}
