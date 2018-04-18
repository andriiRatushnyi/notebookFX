package baobab.notebookfx.services;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Image;
import baobab.notebookfx.models.Source;
import baobab.notebookfx.models.Tag;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface ContentManager {

    Page<Content> getContentBySearch(Integer pageNumber, String search, Set<Tag> tags);

    Long getCountContentBySearch(Integer pageNumber, String search, Set<Tag> tags);

    Content getContent(Long id);

    boolean deleteContent(Long id);

//    List<Tag> findTags();
//
//    List<Tag> getTagsByParentId(int parentId);

    Tag saveTag(Tag tag);
    
    Tag saveTag(Tag tag, int index);
    
    List<Tag> moveUpTag(Tag tag);
    
    List<Tag> moveDownTag(Tag tag);

//    Tag getTag(Long id);

    void deleteTag(Tag tag);

    Content saveContent(Content content);

    Image saveImage(Image image);

    Image getImage(Long id);

    void deleteImages();

    void close();
    
    Tag findRootTree();

    Source saveSource(Source source);
}
