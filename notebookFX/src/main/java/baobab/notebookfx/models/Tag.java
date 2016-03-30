package baobab.notebookfx.models;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "tag")
public class Tag implements Externalizable {

    private static final long serialVersionUID = 1L;

    // ID field
    // =========================================================================
    private LongProperty id;
    private Long _id;

    @Id
    @Column(name = "tag_id")
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

    // PARENTID field
    // =========================================================================
    private IntegerProperty parentId;
    private int _parentId;

    @Column(name = "parent_id")
    public int getParentId() {
        if (parentId == null) {
            return _parentId;
        } else {
            return parentId.get();
        }
    }

    public void setParentId(int parentId) {
        if (this.parentId == null) {
            _parentId = parentId;
        } else {
            this.parentId.set(parentId);
        }
    }

    public IntegerProperty parentIdProperty() {
        if (parentId == null) {
            parentId = new SimpleIntegerProperty(this, "parentId", _parentId);
        }
        return parentId;
    }

    // NAME field
    // =========================================================================
    private StringProperty name;
    private String _name;

    @Column(name = "name", nullable = false)
    public String getName() {
        if (name == null) {
            return _name;
        } else {
            return name.get();
        }
    }

    public void setName(String name) {
        if (this.name == null) {
            _name = name;
        } else {
            this.name.set(name);
        }
    }

    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(this, "name", _name);
        }
        return name;
    }

    // SORT field
    // =========================================================================
    private IntegerProperty sort;
    private int _sort;

    @Column(name = "sort", nullable = false)
    public int getSort() {
        if (sort == null) {
            return _sort;
        } else {
            return sort.get();
        }
    }

    public void setSort(int sort) {
        if (this.sort == null) {
            _sort = sort;
        } else {
            this.sort.set(sort);
        }
    }

    public IntegerProperty sortProperty() {
        if (sort == null) {
            sort = new SimpleIntegerProperty(this, "sort", _sort);
        }
        return sort;
    }

    // CONTENTS field
    // =========================================================================
    private ObjectProperty<ObservableSet<Content>> contents;
    private Set<Content> _contents;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    //@OnDelete(action=OnDeleteAction.CASCADE)
    public Set<Content> getContents() {
        if (contents == null) {
            return _contents;
        } else {
            return contents.get();
        }
    }

    public void setContents(Set<Content> contents) {
        if (this.contents == null) {
            _contents = contents;
        } else {
            this.contents.set(FXCollections.<Content>observableSet(contents));
        }
    }

    public ObjectProperty<ObservableSet<Content>> contentsProperty() {
        if (contents == null) {
            contents = new SimpleObjectProperty<>(this, "contents", FXCollections.<Content>observableSet(_contents));
        }
        return contents;
    }

    @Override
    public String toString() {
        return "Tag{" + "id=" + _id + ", parentId=" + _parentId + ", name=" + _name + ", sort=" + _sort + '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        out.writeInt(getParentId());
        out.writeObject(getName());
        out.writeInt(getSort());
        out.writeObject(getContents());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setParentId(in.readInt());
        setName((String) in.readObject());
        setSort(in.readInt());
        setContents((Set<Content>) in.readObject());
    }

}
