package org.markdownwriterfx.options;

import com.vladsch.flexmark.Extension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.markdownwriterfx.util.Messages;

public class MarkdownExtensions {

    static final HashMap<String, String> displayNames = new HashMap<>();
    static final HashMap<String, String> extClasses = new HashMap<>();

    static {
        extClasses.put("footnotes", "com.vladsch.flexmark.ext.footnotes.FootnoteExtension");
        extClasses.put("definition", "com.vladsch.flexmark.ext.definition.DefinitionExtension");
        extClasses.put("yaml-front-matter", "com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension");
        extClasses.put("gfm-strikethrough", "com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension");
        extClasses.put("autolink", "com.vladsch.flexmark.ext.autolink.AutolinkExtension");
        extClasses.put("aside", "com.vladsch.flexmark.ext.aside.AsideExtension");
        extClasses.put("anchorlink", "com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension");
        extClasses.put("wikilink", "com.vladsch.flexmark.ext.wikilink.WikiLinkExtension");
        extClasses.put("gfm-tables", "com.vladsch.flexmark.ext.gfm.tables.TablesExtension");
        extClasses.put("toc", "com.vladsch.flexmark.ext.toc.TocExtension");
        extClasses.put("abbreviation", "com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension");
        extClasses.put("gfm-tasklist", "com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension");
        extClasses.put("db-image", "baobab.notebookfx.flexmark.ImageDBLinkExtension");
        extClasses.put("plant-uml", "baobab.notebookfx.flexmark.PlantUMLExtension");

        extClasses.keySet().forEach(id -> displayNames.put(id, Messages.get("MarkdownExtensionsPane.ext." + id)));
    }

    public static String[] ids() {
        return displayNames.keySet().toArray(new String[displayNames.size()]);
    }

    public static String displayName(String id) {
        return displayNames.get(id);
    }

    public static List<Extension> getExtensions() {
        List<Extension> extensions = new ArrayList<>();

        for (String markdownExtension : Options.getMarkdownExtensions()) {

            String extClassName = extClasses.get(markdownExtension);
            if (extClassName == null) {
                continue; // extension not supported by renderer
            }
            try {
                Class<?> cls = Class.forName(extClassName);
                Method createMethod = cls.getMethod("create");
                @SuppressWarnings("unchecked")
                Extension extension = (Extension) createMethod.invoke(null);
                extensions.add(extension);
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {

            }
        }
        return extensions;
    }
}
