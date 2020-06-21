package com.demo.db.daos;

import com.demo.api.Task;
import com.demo.exception.ResourceValidationException;
import com.demo.resources.TodoResource;
import com.demo.util.TaskMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    /**
     * Logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoResource.class);

    /**
     * The collection of Tasks
     */
    final MongoCollection<Document> taskCollection;

    /**
     * Constructor.
     *
     * @param taskCollection the collection of Tasks.
     */
    public TaskDAO(final MongoCollection<Document> taskCollection) {
        this.taskCollection = taskCollection;
    }

    public ObjectId save(final Task task, final ObjectId todo_id) throws ResourceValidationException {
        this.validateTask(task);
        LOGGER.info("Saving task " + task.toString());
        final Document saveTask =new Document("name", task.getName())
                .append("description", task.getDescription())
                .append("todo_id", todo_id);
        taskCollection.insertOne(saveTask);
        final Document taskFind = (Document) taskCollection.find().sort(new Document("_id", -1)).limit(1).first();
        ObjectId lastInsertedTaskId = taskFind.getObjectId("_id");
        LOGGER.info("Saved Successfully");
        return lastInsertedTaskId;
    }

    public List<Task> findAllTodoTasks(ObjectId todo_id) {
        LOGGER.info("finding all tasks for");
        LOGGER.info(String.valueOf(todo_id));
        final MongoCursor<Document> tasks = taskCollection.find(new Document("todo_id", todo_id)).iterator();
        final List<Task> tasksFind = new ArrayList<>();
        try {
            while (tasks.hasNext()) {
                final Document task = tasks.next();
                tasksFind.add(TaskMapper.map(task));
            }
        } finally {
            tasks.close();
        }
        return tasksFind;
    }

    public long deleteAllTodoTasks(ObjectId todo_id) {
        final DeleteResult deletedTasks = taskCollection.deleteMany(new Document("todo_id", todo_id));
        LOGGER.info("Deleted "+ deletedTasks.getDeletedCount() +  " task(s) for todo" + todo_id);
        return deletedTasks.getDeletedCount();
    }

    public List<ObjectId> createOrUpdateTasks(List<Task> tasks, ObjectId todo_id) throws ResourceValidationException {
        List<ObjectId> taskIds = new ArrayList<>();
        for(Task task : tasks)
        {
            // update the existing task
            if(task.getId() != null)
            {   LOGGER.info("Updating existing task " + task.getId() + "for todo " + todo_id);
                taskCollection.updateOne(new Document("_id", task.getId()),
                        new Document("$set", new Document("name", task.getName())
                                .append("description", task.getDescription()))
                );
                taskIds.add(task.getId());
                LOGGER.info("Updated existing task " + task.getId() + "for todo " + todo_id);
            } else {
                // save a new task
                LOGGER.info("Creating new task for todo " + todo_id);
                ObjectId task_id = this.save(task, todo_id);
                taskIds.add(task_id);
                LOGGER.info("Created new task " + task_id +" for todo " + todo_id);
            }
        }
        LOGGER.info("Updated/Created all tasks for the todo " + todo_id + " successfully");
        LOGGER.info("Following task ids " + taskIds);
        return taskIds;
    }

    private void validateTask(Task task) throws ResourceValidationException {
        if(task.getName() == null || task.getName().isEmpty())
        {
            throw new ResourceValidationException("A Task must have a name");
        }
    }

    public void deleteManyTasks(List<ObjectId> idsToBeRemoved) {
        LOGGER.info("Removing existing tasks for this todo if present " + idsToBeRemoved);
        DeleteResult i = taskCollection.deleteMany(new Document("_id", new Document("$in", idsToBeRemoved)));
        LOGGER.info("Removed " +  i.getDeletedCount() + " existing tasks for this todo if present");
    }
}
