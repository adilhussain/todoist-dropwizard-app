package com.demo.util;

import com.demo.api.Task;
import com.demo.api.Todo;
import org.bson.Document;

import java.util.List;

public class TodoMapper {

    /**
     * Map objects {@link Document} to {@link Todo}.
     *
     * @param todoDocument the information document.
     * @param tasks
     * @return A object {@link Todo}.
     */
    public static Todo map(final Document todoDocument, List<Task> tasks) {
        final Todo todo = new Todo();
        todo.setId(todoDocument.getObjectId("_id"));
        todo.setName(todoDocument.getString("name"));
        todo.setDescription(todoDocument.getString("description"));
        todo.setTasks(tasks);
        return todo;
    }
}
