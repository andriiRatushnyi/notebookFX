package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Source;
import org.springframework.data.repository.CrudRepository;

public interface SourceRepository extends CrudRepository<Source, Long>, SourceRepositoryCustom {

//    public List<Tag> findAllByOrderByParentIdAscSortAsc();
//
//    public List<Tag> findByParentIdOrderByParentIdAsc(int parentId);

}
