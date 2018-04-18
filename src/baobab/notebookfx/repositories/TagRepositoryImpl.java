package baobab.notebookfx.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TagRepositoryImpl implements TagRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

}
