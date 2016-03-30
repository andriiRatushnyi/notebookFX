package baobab.notebookfx.models.states;

import java.util.HashSet;
import java.util.Set;

public class PageState {

    private String searchText = "";
    private int tabId;
    private double tabScroll;
    private Set<Long> selectedTags = new HashSet<>();
    private double splitter = .25;
    private int pageId;
    private double pageScroll;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    public double getTabScroll() {
        return tabScroll;
    }

    public void setTabScroll(double tabScroll) {
        this.tabScroll = tabScroll;
    }

    public Set<Long> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(Set<Long> selectedTags) {
        this.selectedTags = selectedTags;
    }

    public double getSplitter() {
        return splitter;
    }

    public void setSplitter(double splitter) {
        this.splitter = splitter;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public double getPageScroll() {
        return pageScroll;
    }

    public void setPageScroll(double pageScroll) {
        this.pageScroll = pageScroll;
    }

    @Override
    public String toString() {
        return "PageState{" + "searchText=" + searchText + ", tabId=" + tabId + ", tabScroll=" + tabScroll + ", selectedTags=" + selectedTags + ", pageId=" + pageId + ", pageScroll=" + pageScroll + '}';
    }

}
