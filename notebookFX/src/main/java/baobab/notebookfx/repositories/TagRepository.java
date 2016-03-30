package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Tag;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Long>, TagRepositoryCustom {

    public List<Tag> findAllByOrderByParentIdAscSortAsc();

    public List<Tag> findByParentIdOrderByParentIdAsc(int parentId);

}
