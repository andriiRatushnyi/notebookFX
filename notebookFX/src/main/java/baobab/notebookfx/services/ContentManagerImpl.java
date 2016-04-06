package baobab.notebookfx.services;

import baobab.notebookfx.models.Content;
import baobab.notebookfx.models.Image;
import baobab.notebookfx.models.Tag;
import baobab.notebookfx.repositories.ContentRepository;
import baobab.notebookfx.repositories.ImageRepository;
import baobab.notebookfx.repositories.TagRepository;
import java.io.File;
import java.nio.file.FileSystems;
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

    private static final int PAGE_SIZE = 3;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    ContentRepository contentRepository;

    @Inject
    TagRepository tagRepository;

    @Inject
    ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Content> getContentBySearch(Integer pageNumber, String search, Set<Tag> tags) {
        PageRequest request = new PageRequest(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "updateAt");
        return this.contentRepository.searchListContents(search, tags, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCountContentBySearch(Integer pageNumber, String search, Set<Tag> tags) {
        return this.contentRepository.searchCountContents(search, tags);
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
    public List<Tag> findTags() {
        return tagRepository.findAllByOrderByParentIdAscSortAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getTagsByParentId(int parentId) {
        return tagRepository.findByParentIdOrderByParentIdAsc(parentId);
    }

    @Override
    @Transactional
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getTag(Long id) {
        return tagRepository.findOne(id);
    }

    @Override
    @Transactional
    public void deleteTag(Tag tag) {
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
            File imgFile = new File(System.getProperty("java.io.tmpdir") +
                                           FileSystems.getDefault().getSeparator() +
                                            image.getId().toString() + ".noteFX");
            imgFile.delete();

            List<Content> contents = contentRepository.findAllByContentLike("%IMG#" + image.getId().toString() + "#IMG%");
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

}
