package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Tag;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepositoryCustom {

    public Page<Content> searchListContents(String searchQuery, Set<Tag> tags, Pageable p);

    public Long searchCountContents(String searchQuery, Set<Tag> tags);

}
