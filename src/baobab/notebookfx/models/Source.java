package baobab.notebookfx.models;

import baobab.notebookfx.models.enums.TypeSource;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Access(AccessType.PROPERTY)
@Table(name = "source")
public class Source implements Externalizable {

    private static final long serialVersionUID = 1L;

    // ID field
    // =========================================================================
    private LongProperty id;
    private Long _id;

    @Id
    @Column(name = "source_id")
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

    // TYPE field
    // =========================================================================
    private ObjectProperty<TypeSource> type;
    private TypeSource _type;

    @Column(name = "type", nullable = false)
    public TypeSource getType() {
        if (type == null) {
            return _type;
        } else {
            return type.get();
        }
    }

    public void setType(TypeSource type) {
        if (this.type == null) {
            _type = type;
        } else {
            this.type.set(type);
        }
    }

    public ObjectProperty<TypeSource> typeProperty() {
        if (type == null) {
            type = new SimpleObjectProperty<>(this, "type", _type);
        }
        return type;
    }

    @Override
    public String toString() {
        return "Resource{" + "id=" + _id + ", name=" + _name + ", type=" + _type + '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        out.writeObject(getName());
        out.writeObject(getType());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setName((String) in.readObject());
        setType((TypeSource) in.readObject());
    }

}
