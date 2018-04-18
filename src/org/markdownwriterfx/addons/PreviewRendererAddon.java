package org.markdownwriterfx.addons;

public interface PreviewRendererAddon {

    String preParse(String markdown/*, Path path*/);

    String postRender(String html/*, Path path*/);
}
