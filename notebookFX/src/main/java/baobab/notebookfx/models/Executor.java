package baobab.notebookfx.models;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
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
@Table(name = "executor")
public class Executor implements Externalizable {

    private static final long serialVersionUID = 1L;

    // ID field
    // =========================================================================
    private LongProperty id;
    private Long _id;

    @Id
    @Column(name = "executor_id")
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

    // COMMAND field
    // =========================================================================
    private StringProperty command;
    private String _command;

    @Column(name = "command")
    public String getCommand() {
        if (command == null) {
            return _command;
        } else {
            return command.get();
        }
    }

    public void setCommand(String command) {
        if (this.command == null) {
            _command = command;
        } else {
            this.command.set(command);
        }
    }

    public StringProperty commandProperty() {
        if (command == null) {
            command = new SimpleStringProperty(this, "command", _command);
        }
        return command;
    }

    // PORT field
    // =========================================================================
    private IntegerProperty port;
    private int _port;

    @Column(name = "port")
    public int getPort() {
        if (port == null) {
            return _port;
        } else {
            return port.get();
        }
    }

    public void setPort(int port) {
        if (this.port == null) {
            _port = port;
        } else {
            this.port.set(port);
        }
    }

    public IntegerProperty portProperty() {
        if (port == null) {
            port = new SimpleIntegerProperty(this, "port", _port);
        }
        return port;
    }

    @Override
    public String toString() {
        return "Executor{" + "id=" + _id + ", name=" + _name + ", command=" + _command + ", port=" + _port + '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        out.writeObject(getName());
        out.writeObject(getCommand());
        out.writeInt(getPort());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setName((String)in.readObject());
        setCommand((String) in.readObject());
        setPort(in.readInt());
    }

}
