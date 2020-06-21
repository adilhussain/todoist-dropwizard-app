package com.demo.util;

import com.demo.api.Task;
import org.bson.Document;


public class TaskMapper {

    /**
     *
     * @param taskDocument
     * @return
     */
    public static Task map(final Document taskDocument) {
        final Task task = new Task();
        task.setId(taskDocument.getObjectId("_id"));
        task.setTodo_id(taskDocument.getObjectId("todo_id"));
        task.setName(taskDocument.getString("name"));
        task.setDescription(taskDocument.getString("description"));
        return task;
    }
}
