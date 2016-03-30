package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Content_;
import baobab.notebookfx.models.Tag;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ContentRepositoryImpl implements ContentRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery criteriaQuery;
    private Root<Content> root;

    private String searchQuery;
    private Set<Tag> tags;

    @Override
    public Page<Content> searchListContents(String searchQuery, Set<Tag> tags, Pageable pageable) {
        init(searchQuery, tags);

        Query query = entityManager.createQuery(getQuery());
        int totalRows = query.getResultList().size();

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        Page<Content> page = new PageImpl<>(query.getResultList(), pageable, totalRows);

        return page;
    }

    @Override
    public Long searchCountContents(String searchQuery, Set<Tag> tags) {
        init(searchQuery, tags);

        TypedQuery<Long> typedQuery = this.entityManager.createQuery(getQueryForCount());
        return typedQuery.getSingleResult();
    }

    private CriteriaQuery<Content> getQuery() {
        return criteriaQuery.select(root)
                .where(criteriaBuilder.and(getPredicate()));
    }

    private CriteriaQuery<Long> getQueryForCount() {
        return criteriaQuery.select(criteriaBuilder.count(root))
                .where(criteriaBuilder.and(getPredicate()));
    }

    private Predicate[] getPredicate() {
        Set<String> items = new HashSet<>(Arrays.asList(searchQuery.toLowerCase().trim().split(" ")));
        // content title & content
        Predicate[] contentPredicates = items.stream()
                .map(word -> criteriaBuilder.literal("%" + word + "%"))
                .map(wordLiteral -> criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Content_.title)), criteriaBuilder.lower(wordLiteral)),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Content_.content)), criteriaBuilder.lower(wordLiteral))
                ))
                .toArray(size -> new Predicate[size]);
        // tags
        Predicate[] tagPredicates = tags.stream()
                .map(tag -> criteriaBuilder.isMember(tag, root.get(Content_.tags)))
                .toArray(size -> new Predicate[size]);

        // concat arrays
        Predicate[] completePredicates = Stream.concat(Arrays.stream(contentPredicates), Arrays.stream(tagPredicates))
                .toArray(Predicate[]::new);

        return completePredicates;
    }

    private void init(String searchQuery, Set<Tag> tags) {
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Content.class);
        root = criteriaQuery.from(Content.class);
        this.searchQuery = searchQuery;
        this.tags = tags;
    }

}
