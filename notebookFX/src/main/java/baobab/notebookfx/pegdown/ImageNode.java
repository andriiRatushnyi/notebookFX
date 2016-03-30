package baobab.notebookfx.pegdown;

import org.pegdown.ast.Node;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.Visitor;

public class ImageNode extends TextNode {

    private String name;

    public ImageNode(String text) {
        super(text);
        name = text;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit((Node) this);
    }

    public String getName() {
        return name;
    }

}