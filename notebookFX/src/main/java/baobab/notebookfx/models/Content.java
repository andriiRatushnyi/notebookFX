package baobab.notebookfx.models;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.Set;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "content")
public class Content implements Externalizable {

    private static final long serialVersionUID = 1L;

    // ID field
    // =========================================================================
    private LongProperty id;
    private Long _id;

    @Id
    @Column(name = "content_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        if (id == null) {
            return _id;
        } else {
            return id.get();
        }
    }

    public void setId(Long id) {
        if (this.id == null) {
            _id = id;
        } else {
            this.id.set(id);
        }
    }

    public LongProperty idProperty() {
        if (id == null) {
            id = new SimpleLongProperty(this, "id", _id);
        }
        return id;
    }

    // TITLE field
    // =========================================================================
    private StringProperty title;
    private String _title;

    @Column(name = "title")
    public String getTitle() {
        if (title == null) {
            return _title;
        } else {
            return title.get();
        }
    }

    public void setTitle(String title) {
        if (this.title == null) {
            _title = title;
        } else {
            this.title.set(title);
        }
    }

    public StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty(this, "title", _title);
        }
        return title;
    }

    // CONTENT field
    // =========================================================================
    private StringProperty content;
    private String _content;

    @Column(name = "content", columnDefinition = "LONG VARCHAR")
    public String getContent() {
        if (this.content == null) {
            return _content;
        } else {
            return content.get();
        }
    }

    public void setContent(String content) {
        if (this.content == null) {
            _content = content;
        } else {
            this.content.set(content);
        }
    }

    public StringProperty contentProperty() {
        if (content == null) {
            content = new SimpleStringProperty(this, "content", _content);
        }
        return content;
    }

    // VIEWCOUNT field
    // =========================================================================
    private IntegerProperty viewCount;
    private int _viewCount;

    @Column(name = "view_count")
    public int getViewCount() {
        if (viewCount == null) {
            return _viewCount;
        } else {
            return viewCount.get();
        }
    }

    public void setViewCount(Integer viewCount) {
        if (this.viewCount == null) {
            _viewCount = viewCount;
        } else {
            this.viewCount.set(viewCount);
        }
    }

    public IntegerProperty viewCountProperty() {
        if (viewCount == null) {
            viewCount = new SimpleIntegerProperty(this, "viewCount", _viewCount);
        }
        return viewCount;
    }

    // TAGS field
    // =========================================================================
    private ObjectProperty<ObservableSet<Tag>> tags;
    private Set<Tag> _tags;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tag_content_bridge",
            joinColumns = {
                @JoinColumn(name = "content_id", referencedColumnName = "content_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "tag_id", referencedColumnName = "tag_id")})
    public Set<Tag> getTags() {
        if (this.tags == null) {
            return _tags;
        } else {
            return tags.get();
        }
    }

    public void setTags(Set<Tag> tags) {
        if (this.tags == null) {
            _tags = tags;
        } else {
            this.tags.set(FXCollections.<Tag>observableSet(tags));
        }
    }

    public ObjectProperty<ObservableSet<Tag>> tagsProperty() {
        if (tags == null) {
            tags = new SimpleObjectProperty<>(this, "tags", FXCollections.<Tag>observableSet(_tags));
        }
        return tags;
    }

    // IMAGES field
    // =========================================================================
    private ObjectProperty<ObservableSet<Image>> images;
    private Set<Image> _images;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "image_content_bridge",
            joinColumns = {
                @JoinColumn(name = "content_id", referencedColumnName = "content_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "image_id", referencedColumnName = "image_id")})

    public Set<Image> getImages() {
        if (images == null) {
            return _images;
        } else {
            return images.get();
        }
    }

    public void setImages(Set<Image> images) {
        if (this.images == null) {
            _images = images;
        } else {
            this.images.set(FXCollections.<Image>observableSet(images));
        }
    }

    public ObjectProperty<ObservableSet<Image>> imagesProperty() {
        if (images == null) {
            images = new SimpleObjectProperty<>(this, "images", FXCollections.<Image>observableSet(_images));
        }
        return images;
    }

    // RESOURCES field
    // =========================================================================
//    private ObjectProperty<ObservableList<Resource>> resources;
//    private List<Resource> _resources;
//
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) // foreign Key
//    @JoinColumn(name = "content_id", referencedColumnName = "content_id")
//    @OrderColumn(name = "resource_index")
//    public List<Resource> getResources() {
//        if (resources == null) {
//            return _resources;
//        } else {
//            return resources.get();
//        }
//    }
//
//    public void setResources(List<Resource> resources) {
//        if (this.resources == null) {
//            _resources = resources;
//        } else {
//            this.resources.set(FXCollections.<Resource>observableArrayList(_resources));
//        }
//    }
//
//    public ObjectProperty<ObservableList<Resource>> resourcesProperty() {
//        if (resources == null) {
//            resources = new SimpleObjectProperty<>(this, "resources", FXCollections.<Resource>observableList(_resources));
//        }
//        return resources;
//    }

    // EXECUTORS field
    // =========================================================================
//    private ObjectProperty<ObservableList<Executor>> executors;
//    private List<Executor> _executors;
//
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) // foreign key
//    @JoinColumn(name = "content_id")
//    @OrderColumn(name = "executor_index")
//    public List<Executor> getExecutors() {
//        if (executors == null) {
//            return _executors;
//        } else {
//            return executors.get();
//        }
//    }
//
//    public void setExecutors(List<Executor> executors) {
//        if (this.executors == null) {
//            _executors = executors;
//        } else {
//            this.executors.set(FXCollections.<Executor>observableArrayList(executors));
//        }
//    }
//
//    public ObjectProperty<ObservableList<Executor>> executorsProperty() {
//        if (executors == null) {
//            executors = new SimpleObjectProperty<>(this, "executors", FXCollections.<Executor>observableList(_executors));
//        }
//        return executors;
//    }

    // CREATEAT field
    // =========================================================================
    private ObjectProperty<Date> createAt;
    private Date _createAt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    public Date getCreateAt() {
        if (createAt == null) {
            return _createAt;
        } else {
            return createAt.get();
        }
    }

    public void setCreateAt(Date createAt) {
        if (this.createAt == null) {
            _createAt = createAt;
        } else {
            this.createAt.set(createAt);
        }

    }

    public ObjectProperty<Date> createAtProperty() {
        if (createAt == null) {
            createAt = new SimpleObjectProperty<>(this, "createAt", _createAt);
        }
        return createAt;
    }

    // UPDATEAT field
    // =========================================================================
    private ObjectProperty<Date> updateAt;
    private Date _updateAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    public Date getUpdateAt() {
        if (updateAt == null) {
            return _updateAt;
        } else {
            return updateAt.get();
        }
    }

    public void setUpdateAt(Date updateAt) {
        if (this.updateAt == null) {
            _updateAt = updateAt;
        } else {
            this.updateAt.set(updateAt);
        }
    }

    public ObjectProperty<Date> updateAtProperty() {
        if (updateAt == null) {
            updateAt = new SimpleObjectProperty<>(this, "updateAt", _updateAt);
        }
        return updateAt;
    }

    @Override
    public String toString() {
        return "Content{" + "id=" + _id + ", title=" + _title + ", content=" + _content + ", viewCount=" + _viewCount + ", createAt=" + _createAt + ", updateAt=" + _updateAt + ", executors=" /*+ _executors*/ + ", resources=" + /*_resources +*/ '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        out.writeObject(getTitle());
        out.writeObject(getContent());
        out.writeInt(getViewCount());
        out.writeObject(getTags());
        out.writeObject(getImages());
//        out.writeObject(getResources());
//        out.writeObject(getExecutors());
        out.writeObject(getCreateAt());
        out.writeObject(getUpdateAt());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setTitle((String) in.readObject());
        setContent((String) in.readObject());
        setViewCount(in.readInt());
        setTags((Set<Tag>) in.readObject());
        setImages((Set<Image>) in.readObject());
//        setResources((List<Resource>) in.readObject());
//        setExecutors((List<Executor>) in.readObject());
        setCreateAt((Date) in.readObject());
        setUpdateAt((Date) in.readObject());
    }

}
