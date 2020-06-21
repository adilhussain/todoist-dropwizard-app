package com.demo.api;

import com.demo.util.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

public class Task {

    /** The id.*/
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;

    /** The todo_id.*/
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId todo_id;

    /** The name. */
    @NotNull
    private String name;

    /** The description. */
    @NotNull
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getTodo_id() {
        return todo_id;
    }

    public void setTodo_id(ObjectId todo_id) {
        this.todo_id = todo_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", todo_id=" + todo_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
