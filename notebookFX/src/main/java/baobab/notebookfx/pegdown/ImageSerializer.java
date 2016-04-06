package baobab.notebookfx.pegdown;

import baobab.notebookfx.models.Image;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFXLoader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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

            try {
                String fileName = id.toString() + ".noteFX";
                File tmpFile = new File(System.getProperty("java.io.tmpdir") +
                                           FileSystems.getDefault().getSeparator() + fileName);
                if(!tmpFile.exists()) {
                    Image image = contentManager.getImage(id);
                    try(PrintWriter out = new PrintWriter(tmpFile.getAbsolutePath())){
                        if (image != null) {
                            out.println("<img src=\"data:"
                                    + image.getType()
                                    + ";base64,"
                                    + DatatypeConverter.printBase64Binary(image.getContent())
                                    + "\" >");
                        } else {
                            out.println("Image not found!");
                        }
                    }
                }
                String img = new String(Files.readAllBytes(tmpFile.toPath()));
                printer.print(img);
                printer.println();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

}
