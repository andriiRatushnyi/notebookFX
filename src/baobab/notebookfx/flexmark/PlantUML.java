package baobab.notebookfx.flexmark;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.DelegatingNodeRendererFactory;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.options.DataHolder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUML {

//    public static class PlantUMLExtension implements HtmlRenderer.HtmlRendererExtension {
//
//        @Override
//        public void rendererOptions(final MutableDataHolder options) {
//
//        }
//
//        @Override
//        public void extend(final HtmlRenderer.Builder rendererBuilder, final String rendererType) {
//            rendererBuilder.nodeRendererFactory(new PlantUMLRenderer.Factory());
//        }
//
//        public static PlantUMLExtension create() {
//            return new PlantUMLExtension();
//        }
//    }
    static class PlantUMLRenderer implements NodeRenderer {

        public static class Factory implements DelegatingNodeRendererFactory {

            @Override
            public NodeRenderer create(final DataHolder options) {
                return new PlantUMLRenderer();
            }

            @Override
            public Set<Class<? extends NodeRendererFactory>> getDelegates() {
                return null;
            }
        };

        @Override
        public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
            HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
            set.add(new NodeRenderingHandler<>(FencedCodeBlock.class, (FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) -> {
                ResolvedLink resolvedLink = context.resolveLink(LinkType.IMAGE, node.getChars().unescape(), null);
                String[] info = node.getInfo().toString().trim().split("#");
                // test the node to see if it needs overriding
                if (info.length == 2 && info[0].equals("plant-uml")) {
                    String hashCode = info[1];

                    html.srcPos(node.getChars()).withAttr().tag("p").line();
                    ////////////////////////////////////////////////////////////
                    html.srcPos(node.getChars())
                            .attr("id", "id-" + hashCode)
                            .attr("src", getImage(hashCode, node.getContentChars().normalizeEOL()))
                            .withAttr(resolvedLink).tag("img", true).line();
                    ////////////////////////////////////////////////////////////
                    html.closeTag("p").line();

                } else {
                    context.delegateRender();
                }
            }));
            return set;
        }

        private boolean equalsData(File tmpFileSource, String source) {
            try {
                byte[] b1 = Files.readAllBytes(tmpFileSource.toPath());
                byte[] b2 = source.getBytes();

                // Bytes diff
                return Arrays.equals(b1, b2);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        private String getResult(File tmpFileResult) {
            try {
                // SVG
//                String data = new String(Files.readAllBytes(tmpFileResult.toPath()));
//                return "data:image/svg+xml;utf8," + data;
                // PNG
                byte[] data = Files.readAllBytes(tmpFileResult.toPath());
                return "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            return "";
        }

        private String getImage(String hashCode, String source) {
            try {
                String path = System.getProperty("java.io.tmpdir")
                        + FileSystems.getDefault().getSeparator() + "plantuml-" + hashCode;
                String fileSource = path + "-source.noteFX";
                String fileResult = path + "-result.noteFX";

                File tmpFileSource = new File(fileSource);
                File tmpFileResult = new File(fileResult);
                tmpFileSource.deleteOnExit();
                tmpFileResult.deleteOnExit();
                if (!(tmpFileSource.exists() && tmpFileResult.exists() && equalsData(tmpFileSource, source))) {
                    // main logic prepare data

                    try (FileWriter sourceWriter = new FileWriter(tmpFileSource, false);// true to append, false to overwrite.
                            OutputStream outStr = new FileOutputStream(tmpFileResult)) {
                        // write source
                        sourceWriter.write(source);

                        // write result
                        SourceStringReader reader = new SourceStringReader(source);
                        // SVG
//                        DiagramDescription str = reader.generateImage(outStr, new FileFormatOption(FileFormat.SVG));
//                        String str = outputImage.getDescription();
//                        System.out.println(str);

                        // PNG
                        String outputImage = reader.generateImage(outStr, new FileFormatOption(FileFormat.PNG));
                    }
                }
                return getResult(tmpFileResult);
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
}
