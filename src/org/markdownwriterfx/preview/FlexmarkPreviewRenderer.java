package org.markdownwriterfx.preview;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.NodeVisitor;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import org.markdownwriterfx.addons.PreviewRendererAddon;
import org.markdownwriterfx.options.MarkdownExtensions;
import org.markdownwriterfx.util.Range;

/**
 * flexmark-java preview.
 *
 * @author Karl Tauber
 */
public class FlexmarkPreviewRenderer implements MarkdownPreviewPane.Renderer {

    private static final ServiceLoader<PreviewRendererAddon> addons = ServiceLoader.load(PreviewRendererAddon.class);

    private String markdownText;
    private Node astRoot;
    private Node astRoot2;
//    private Path path;

    private String htmlPreview;
    private String htmlSource;

    @Override
    public void update(String markdownText, Node astRoot/*, Path path*/) {
        assert markdownText != null;
        assert astRoot != null;

        if (this.astRoot == astRoot) {
            return;
        }

        this.markdownText = markdownText;
        this.astRoot = astRoot;
//        this.path = path;

        astRoot2 = null;
        htmlPreview = null;
        htmlSource = null;
    }

    @Override
    public String getHtml(boolean source) {
        if (source) {
            if (htmlSource == null) {
                htmlSource = toHtml(true);
            }
            return htmlSource;
        } else {
            if (htmlPreview == null) {
                htmlPreview = toHtml(false);
            }
            return htmlPreview;
        }
    }
    
    @Override
    public List<Range> findSequences(int startOffset, int endOffset) {
        ArrayList<Range> sequences = new ArrayList<>();

        Node astRoot = toAstRoot();
        if (astRoot == null) {
            return sequences;
        }

        NodeVisitor visitor = new NodeVisitor(Collections.emptyList()) {
            @Override
            public void visit(Node node) {
                BasedSequence chars = node.getChars();
                if (isInSequence(startOffset, endOffset, chars)) {
                    sequences.add(new Range(chars.getStartOffset(), chars.getEndOffset()));
                }

                for (BasedSequence segment : node.getSegments()) {
                    if (isInSequence(startOffset, endOffset, segment)) {
                        sequences.add(new Range(segment.getStartOffset(), segment.getEndOffset()));
                    }
                }

                visitChildren(node);
            }
        };
        visitor.visit(astRoot);
        return sequences;
    }

    private boolean isInSequence(int start, int end, BasedSequence sequence) {
        if (end == start) {
            end++;
        }
        return start < sequence.getEndOffset() && end > sequence.getStartOffset();
    }

    private Node parseMarkdown(String text) {
        Parser parser = Parser.builder()
                .extensions(MarkdownExtensions.getExtensions())
                .build();
        return parser.parse(text);
    }

    private Node toAstRoot() {
        if (!addons.iterator().hasNext()) {
            return astRoot; // no addons --> use AST from editor
        }
        if (astRoot2 == null) {
            astRoot2 = parseMarkdown(markdownText);
        }
        return astRoot2;
    }

    private String toHtml(boolean source) {
        Node astRoot;
        if (addons.iterator().hasNext()) {
            String text = markdownText;

            for (PreviewRendererAddon addon : addons) {
                text = addon.preParse(text/*, path*/);
            }

            astRoot = parseMarkdown(text);
        } else {
            // no addons --> use cached AST
            astRoot = toAstRoot();
        }

        if (astRoot == null) {
            return "";
        }

        HtmlRenderer.Builder builder = HtmlRenderer.builder()
                .extensions(MarkdownExtensions.getExtensions());
        if (!source) {
            builder.attributeProviderFactory(new MyAttributeProvider.Factory());
        }
        String html = builder.build().render(astRoot);

        for (PreviewRendererAddon addon : addons) {
            html = addon.postRender(html/*, path*/);
        }

        return html;
    }

    //---- class MyAttributeProvider ------------------------------------------
    private static class MyAttributeProvider implements AttributeProvider {

        private static class Factory extends IndependentAttributeProviderFactory {

            @Override
            public AttributeProvider create(LinkResolverContext lrc) {
                return new MyAttributeProvider();
            }
        }

        @Override
        public void setAttributes(Node node, AttributablePart part, Attributes attributes) {
            attributes.addValue("data-pos", node.getStartOffset() + ":" + node.getEndOffset());
        }
    }
}
