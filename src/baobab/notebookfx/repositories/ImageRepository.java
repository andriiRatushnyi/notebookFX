package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long>, ImageRepositoryCustom {

}
