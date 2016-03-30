package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Image;
import baobab.notebookfx.models.Image_;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class ImageRepositoryImpl implements ImageRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void cleanUpImage() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<Image> deleteQueryImage = criteriaBuilder.createCriteriaDelete(Image.class);
        Root<Image> rootImage = deleteQueryImage.from(Image.class);

        // Subquery
        Subquery<Content> subQuery = deleteQueryImage.subquery(Content.class);
        // correlate subquery root to root of main query:
        Root<Image> subQueryCorrelate = subQuery.correlate(rootImage);
        Join<Image, Content> subQueryJoin = subQueryCorrelate.join(Image_.contents, JoinType.LEFT);
        subQuery.select(subQueryJoin);

        deleteQueryImage.where(criteriaBuilder.not(criteriaBuilder.exists(subQuery)));

        entityManager.createQuery(deleteQueryImage).executeUpdate();
    }

    @Override
    public List<Image> getUnrelationImages() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Image> criterieQueryImage = criteriaBuilder.createQuery(Image.class);
        Root<Image> rootImage = criterieQueryImage.from(Image.class);
        criterieQueryImage.select(rootImage);

        // Subquery
        Subquery<Content> subQuery = criterieQueryImage.subquery(Content.class);
        // correlate subquery root to root of main query:
        Root<Image> subQueryCorrelate = subQuery.correlate(rootImage);
        Join<Image, Content> subQueryJoin = subQueryCorrelate.join(Image_.contents, JoinType.LEFT);
        subQuery.select(subQueryJoin);

        criterieQueryImage.where(criteriaBuilder.not(criteriaBuilder.exists(subQuery)));

        return entityManager.createQuery(criterieQueryImage).getResultList();
    }

}
