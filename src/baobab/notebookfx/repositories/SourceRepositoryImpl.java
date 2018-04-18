package baobab.notebookfx.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class SourceRepositoryImpl implements SourceRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

}
