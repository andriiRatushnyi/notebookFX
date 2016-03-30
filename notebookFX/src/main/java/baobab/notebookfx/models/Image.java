package baobab.notebookfx.models;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "image")
public class Image implements Externalizable {

    private static final long serialVersionUID = 1L;

    // ID field
    // =========================================================================
    private LongProperty id = new SimpleLongProperty();
    private long _id;

    @Id
    @Column(name = "image_id")
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

    // TYPE field
    // =========================================================================
    private StringProperty type;
    private String _type;

    @Column(name = "type", nullable = false)
    public String getType() {
        if (type == null) {
            return _type;
        } else {
            return type.get();
        }
    }

    public void setType(String type) {
        if (this.type == null) {
            _type = type;
        } else {
            this.type.set(type);
        }
    }

    public StringProperty typeProperty() {
        if (type == null) {
            type = new SimpleStringProperty(this, "type", _type);
        }
        return type;
    }

    // CONTENT field
    // =========================================================================
    private byte[] content;

    @Column(columnDefinition = "clob")
    @Lob
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    // CONTENTS field
    // =========================================================================
    private ObjectProperty<ObservableSet<Content>> contents;
    private Set<Content> _contents = new HashSet<>();

    @ManyToMany(mappedBy = "images", fetch = FetchType.EAGER)
    public Set<Content> getContents() {
        if (this.contents == null) {
            return _contents;
        } else {
            return contents.get();
        }
    }

    public void setContents(Set<Content> contents) {
        if (this.contents == null) {
            _contents = contents;
        } else {
            this.contents.set(FXCollections.observableSet(contents));
        }
    }

    public ObjectProperty<ObservableSet<Content>> contentsProperty() {
        if (contents == null) {
            contents = new SimpleObjectProperty<>(this, "contents", FXCollections.observableSet(_contents));
        }
        return contents;
    }

    @Override
    public String toString() {
        return "Image{" + "id=" + _id + ", type=" + _type + '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        out.writeObject(getType());
        out.writeObject(getContent());
        out.writeObject(getContents());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setType((String) in.readObject());
        setContent((byte[]) in.readObject());
        setContents((Set<Content>) in.readObject());
    }

}
