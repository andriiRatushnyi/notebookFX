package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Content;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContentRepository extends PagingAndSortingRepository<Content, Long>, ContentRepositoryCustom {

    public List<Content> findAllByContentLike(String like);

}
