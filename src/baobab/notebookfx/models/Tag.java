package baobab.notebookfx.models;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@NamedQuery(name = "Tag.findAllTagsWithTheirChildren",
        query = "select t from Tag t left join fetch t.children"
)
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // CHILDREN field
    // =========================================================================
    private ObservableList<Tag> children;
    private List<Tag> _children;

    @OneToMany(fetch = FetchType.EAGER)
    @OrderColumn
    @JoinColumn(name = "parent_id")
    public List<Tag> getChildren() {
        if (children == null) {
            return _children;
        } else {
            return children.stream().collect(Collectors.toList());
        }
    }

    public void setChildren(List<Tag> children) {
        if (this.children == null) {
            _children = children;
        } else {
            this.children = FXCollections.observableList(children);
        }
    }

    public ObservableList<Tag> childrenProperty() {
        if (children == null) {
            children = FXCollections.observableList(children);
        }
        return children;
    }
    // PARENT field
    // =========================================================================
    private ObjectProperty<Tag> parent;
    private Tag _parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    public Tag getParent() {
        if (parent == null) {
            return _parent;
        } else {
            return parent.get();
        }
    }

    public void setParent(Tag parent) {
        if (this.parent == null) {
            _parent = parent;
        } else {
            this.parent.set(parent);
        }
    }

    public ObjectProperty<Tag> parentProperty() {
        if (parent == null) {
            parent = new SimpleObjectProperty<>(this, "parent", _parent);
        }
        return parent;
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
//    private IntegerProperty sort;
//    private int _sort;
//
//    @Column(name = "sort", nullable = false)
//    public int getSort() {
//        if (sort == null) {
//            return _sort;
//        } else {
//            return sort.get();
//        }
//    }
//
//    public void setSort(int sort) {
//        if (this.sort == null) {
//            _sort = sort;
//        } else {
//            this.sort.set(sort);
//        }
//    }
//
//    public IntegerProperty sortProperty() {
//        if (sort == null) {
//            sort = new SimpleIntegerProperty(this, "sort", _sort);
//        }
//        return sort;
//    }

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
//        return "Tag{" + "id=" + id + ", _id=" + _id + ", children=children, _children=_children, parent=parent, _parent=_parent, name=" + name + ", _name=" + _name + ", contents=" + contents + ", _contents=" + _contents + '}';

            return "Tag{id=" + _id + ", name=`" + _name + "`, children=" + _children + "}";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        out.writeObject(getChildren());
        out.writeObject(getParent());
        out.writeObject(getName());
        out.writeObject(getContents());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setChildren((List<Tag>) in.readObject());
        setParent((Tag) in.readObject());
        setName((String) in.readObject());
        setContents((Set<Content>) in.readObject());
    }

}
