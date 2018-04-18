package baobab.notebookfx.services;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Image;
import baobab.notebookfx.models.Source;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.repositories.ContentRepository;
import baobab.notebookfx.repositories.ImageRepository;
import baobab.notebookfx.repositories.SourceRepository;
import baobab.notebookfx.repositories.TagRepository;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("contentManager")
public class ContentManagerImpl implements ContentManager {

    private static final int PAGE_SIZE = 5;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    ContentRepository contentRepository;

    @Inject
    SourceRepository sourceRepository;

    @Inject
    TagRepository tagRepository;

    @Inject
    ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Content> getContentBySearch(Integer pageNumber, String search, Set<Tag> tags) {
        PageRequest request = new PageRequest(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "updateAt");
        return contentRepository.searchListContents(search, tags, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCountContentBySearch(Integer pageNumber, String search, Set<Tag> tags) {
        return contentRepository.searchCountContents(search, tags);
    }

    @Override
    @Transactional(readOnly = true)
    public Content getContent(Long id) {
        return contentRepository.findOne(id);
    }

    @Override
    @Transactional
    public Content saveContent(Content content) {
        return contentRepository.save(content);
    }

    @Override
    @Transactional
    public boolean deleteContent(Long id) {
        if (contentRepository.count() > 1) {
            Content content = getContent(id);
            content.setContent("");
            content.getImages().clear();
            saveContent(content);
            deleteImages();
            contentRepository.delete(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findRootTree() {
        entityManager.createNamedQuery("Tag.findAllTagsWithTheirChildren").getResultList();
        return tagRepository.findOne(0l);
    }

    @Override
    @Transactional
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag saveTag(Tag tag, int index) {
        Tag newTag = tagRepository.save(tag);

        Tag parentTag = newTag.getParent();

        parentTag.getChildren().add(index, newTag);
        saveTag(parentTag);

        return newTag;
    }

    @Override
    public List<Tag> moveUpTag(Tag tag) {
        Tag parent = tag.getParent();
        List<Tag> children = parent.getChildren();
        int index = children.indexOf(tag);

        Collections.swap(children, index, --index);

        List<Tag> tags = new LinkedList<>();
        children.stream().forEach(tagItem -> tags.add(tagItem));

        parent.setChildren(tags);

        saveTag(parent);
        return tags;
    }

    @Override
    public List<Tag> moveDownTag(Tag tag) {
        Tag parent = tag.getParent();
        List<Tag> children = parent.getChildren();
        int index = children.indexOf(tag);

        Collections.swap(children, index, ++index);

        List<Tag> tags = new LinkedList<>();
        children.stream().forEach(tagItem -> tags.add(tagItem));

        parent.setChildren(tags);

        saveTag(parent);

        return tags;
    }

    @Override
    @Transactional
    public void deleteTag(Tag tag) {
        // remove tag from content
        if (tag.getContents() != null) {
            tag.getContents().stream()
                    .forEach(content -> {
                        content.getTags().remove(tag);
                        saveContent(content);
                    });
        }

        // remove tag from parent tree
        Tag parentTag = tag.getParent();
        parentTag.getChildren().remove(tag);
        saveTag(parentTag);

        tagRepository.delete(tag);
    }

    @Override
    @Transactional
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    @Override
    @Transactional(readOnly = true)
    public Image getImage(Long id) {
        return imageRepository.findOne(id);
    }

    @Override
    @Transactional
    public void deleteImages() {
        List<Image> unrelationImage = imageRepository.getUnrelationImages();
        unrelationImage.stream().forEach(image -> {
            // Delete temp file
            File imgFile = new File(System.getProperty("java.io.tmpdir")
                    + FileSystems.getDefault().getSeparator()
                    + image.getId().toString() + ".noteFX");
            imgFile.delete();

            List<Content> contents = contentRepository.findAllByContentLike("%@[" + image.getId().toString() + "]()%");
            contents.stream()
                    .forEach(content -> {
                        content.getImages().add(image);
                        saveContent(content);
                    });
        });
        imageRepository.cleanUpImage();
    }

    @Override
    public void close() {
        entityManager.close();
    }

     @Override
    @Transactional
    public Source saveSource(Source source) {
        return sourceRepository.save(source);
    }

}
