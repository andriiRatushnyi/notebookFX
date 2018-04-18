package baobab.notebookfx.flexmark;

import baobab.notebookfx.models.Image;
import baobab.notebookfx.services.ContentManager;
import baobab.notebookfx.utils.SpringFX;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.InlineLinkNode;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// @web https://github.com/vsch/flexmark-java/issues/82
// @[<youtube>](<text>)
public class ImageLink {

    public static class ImageDBLink extends InlineLinkNode {

        public ImageDBLink() {
        }

        public ImageDBLink(final Link other) {

            super(other.getChars().baseSubSequence(other.getChars().getStartOffset() - 1, other.getChars().getEndOffset()), // @[youtube](text)
                    other.getChars().baseSubSequence(other.getChars().getStartOffset() - 1, other.getTextOpeningMarker().getEndOffset()), // @[
                    other.getText(), // <youtube>
                    other.getTextClosingMarker(), // ]
                    other.getLinkOpeningMarker(), // (
                    other.getUrl(), // <text>
                    other.getTitleOpeningMarker(),
                    other.getTitle(),
                    other.getTitleClosingMarker(),
                    other.getLinkClosingMarker() // )
            );
        }

        @Override
        public void setTextChars(final BasedSequence textChars) {
            int textCharsLength = textChars.length();
            this.textOpeningMarker = textChars.subSequence(0, 1);
            this.text = textChars.subSequence(1, textCharsLength - 1).trim();
            this.textClosingMarker = textChars.subSequence(textCharsLength - 1, textCharsLength);
        }
    }

    public static class ImageDBLinkNodePostProcessor extends NodePostProcessor {

        public ImageDBLinkNodePostProcessor(DataHolder options) {
        }

        @Override
        public void process(NodeTracker state, Node node) {
            if (node instanceof Link) {
                Node previous = node.getPrevious();

                if (previous instanceof Text) {
                    final BasedSequence chars = previous.getChars();
                    if (chars.endsWith("@") && chars.isContinuedBy(node.getChars())) {
                        // trim previous chars to remove '@'
                        previous.setChars(chars.subSequence(0, chars.length() - 1));

                        ImageDBLink imageDBLink = new ImageDBLink((Link) node);
                        imageDBLink.takeChildren(node);
                        node.unlink();
                        previous.insertAfter(imageDBLink);
                        state.nodeRemoved(node);
                        state.nodeAddedWithChildren(imageDBLink);
                    }
                }
            }
        }

        public static class Factory extends NodePostProcessorFactory {

            public Factory(DataHolder options) {
                super(false);

                addNodes(Link.class);
            }

            @Override
            public NodePostProcessor create(Document document) {
                return new ImageDBLinkNodePostProcessor(document);
            }
        }
    }

    public static class ImageDBLinkNodeRenderer implements NodeRenderer {

        private ContentManager contentManager;

        public ImageDBLinkNodeRenderer(DataHolder options) {
            this.contentManager = SpringFX.getInstance().getApplicationContex().getBean(ContentManager.class);
        }

        @Override
        public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
            HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
            set.add(new NodeRenderingHandler<>(ImageDBLink.class, (ImageDBLink node, NodeRendererContext context, HtmlWriter html) -> {
                ImageDBLinkNodeRenderer.this.render(node, context, html);
            }));
            return set;
        }

        private void render(final ImageDBLink node, final NodeRendererContext context, final HtmlWriter html) {
            if (context.isDoNotRenderLinks()) {
                context.renderChildren(node);
            } else {
                // standard Link Rendering
                ResolvedLink resolvedLink = context.resolveLink(LinkType.IMAGE, node.getUrl().unescape(), null);

                if (!node.getText().isEmpty()) {
                    String id = node.getText().toString();
                    try {
                        String file = System.getProperty("java.io.tmpdir")
                                + FileSystems.getDefault().getSeparator() + "img-" + id + ".noteFX";
                        File tmpFile = new File(file);
                        tmpFile.deleteOnExit();
                        if (!tmpFile.exists()) {
                            Image image = contentManager.getImage(Long.parseLong(id));
                            try (PrintWriter out = new PrintWriter(tmpFile.getAbsolutePath())) {
                                if (image != null) {
                                    out.println("data:"
                                            + image.getType()
                                            + ";base64,"
                                            + Base64.getEncoder().encodeToString(image.getContent()));
//                                    + DatatypeConverter.printBase64Binary(image.getContent()));
                                }
                            }
                        }
                        String img = new String(Files.readAllBytes(tmpFile.toPath()), Charset.forName("UTF-8"));
                        html.attr("src", img);
                    } catch (IOException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (node.getTitle().isNotNull()) {
                    html.attr("alt", node.getTitle().unescape());
                }
                html.srcPos(node.getChars()).withAttr(resolvedLink).tag("img", true).line();
                //context.renderChildren(node);
//                html.tag("/img", true);
            }
        }

        public static class Factory implements NodeRendererFactory {

            @Override
            public NodeRenderer create(final DataHolder options) {
                return new ImageDBLinkNodeRenderer(options);
            }
        }
    }

//    public static class ImageDBLinkExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
//
//        private ImageDBLinkExtension() {
//        }
//
//        public static Extension create() {
//            return new ImageDBLinkExtension();
//        }
//
//        @Override
//        public void extend(Parser.Builder parserBuilder) {
//            parserBuilder.postProcessorFactory(new ImageDBLinkNodePostProcessor.Factory(parserBuilder));
//        }
//
//        @Override
//        public void rendererOptions(final MutableDataHolder options) {
//
//        }
//
//        @Override
//        public void parserOptions(final MutableDataHolder options) {
//
//        }
//
//        @Override
//        public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
//            if (rendererType.equals("HTML")) {
//                rendererBuilder.nodeRendererFactory(new ImageDBLinkNodeRenderer.Factory());
//            } else if (rendererType.equals("JIRA") || rendererType.equals("YOUTRACK")) {
//            }
//        }
//    }
}
