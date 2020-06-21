package com.demo.db.daos;

import com.demo.api.Task;
import com.demo.api.Todo;
import com.demo.exception.ResourceValidationException;
import com.demo.resources.TodoResource;
import com.demo.util.TodoMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import javassist.NotFoundException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoDAO {

    /**
     * Logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoResource.class);

    /**
     * The collection of Todos
     */
    final MongoCollection<Document> todoCollection;

    final TaskDAO taskDAO;

    /**
     * Constructor.
     *
     * @param todoCollection the collection of Todos.
     */
    public TodoDAO(final MongoCollection<Document> todoCollection, TaskDAO taskDAO) {
        this.todoCollection = todoCollection;
        this.taskDAO = taskDAO;
    }

    /**
     * Find all todos.
     *
     * @return the todos.
     */
    public List<Todo> getAll() {
        final MongoCursor<Document> todos = todoCollection.find().iterator();
        final List<Todo> todosFound = new ArrayList<>();
        try {
            while (todos.hasNext()) {
                final Document todo = todos.next();
                LOGGER.info("Getting all tasks for todo " + todo.getObjectId("_id"));
                final List<Task> tasks = taskDAO.findAllTodoTasks(todo.getObjectId("_id"));
                LOGGER.info("Got " + tasks.size() + " tasks");
                todosFound.add(TodoMapper.map(todo, tasks));
            }
        } finally {
            LOGGER.info("Closing connection with todos table");
            todos.close();
        }
        return todosFound;
    }

    public Todo getOne(final ObjectId id) {
        final Optional<Document> todoFind = Optional.ofNullable(todoCollection.find(new Document("_id", id)).first());
        return todoFind.isPresent() ? TodoMapper.map(todoFind.get(), taskDAO.findAllTodoTasks(id)) : null;
    }


    public Todo save(final Todo todo) throws ResourceValidationException {
        this.validateTodo(todo);
        LOGGER.info("Saving todo " + todo);
        final Document saveTodo =new Document("name", todo.getName())
                .append("description", todo.getDescription());
        todoCollection.insertOne(saveTodo);

        final Document todoFind = (Document) todoCollection.find().sort(new Document("_id", -1)).limit(1).first();
        ObjectId lastInsertedTodoId = todoFind.getObjectId("_id");

        LOGGER.info("lastInserted Todo Id is " + lastInsertedTodoId);
        if(todo.getTasks() != null)
        {
            for(Task task: todo.getTasks())
            {
                try {
                    taskDAO.save(task, lastInsertedTodoId);
                } catch (ResourceValidationException e) {
                    // rollback changes. Remove created todo
                    LOGGER.info("Rolling Back. Task does not have a name");
                    LOGGER.info("Deleting todo " + lastInsertedTodoId);
                    this.delete(lastInsertedTodoId);
                    throw e;
                }
            }
        }
        return this.getOne(lastInsertedTodoId);
    }

    private void validateTodo(Todo todo) throws ResourceValidationException {
        if(todo.getName() == null || todo.getName().isEmpty() || todo.getName().equals(""))
        {
            throw new ResourceValidationException("A Todo must have a name");
        }
    }


    /**
     * Delete a todo and its tasks.
     * @param id    the identifier.
     * @return
     */
    public boolean delete(final ObjectId id){
        LOGGER.info("Deleting todo " + id);
        DeleteResult deletedTodo = todoCollection.deleteOne(new Document("_id", id));
        LOGGER.info("Todo removed");
        LOGGER.info("Deleting task(s) related to the todo " + id);
        long deletedTasks = taskDAO.deleteAllTodoTasks(id);
        LOGGER.info("Todo Deletion Complete ");
        return deletedTodo.getDeletedCount() > 0 ? true : false;
    }

    public Todo update(final ObjectId id, final Todo todo) throws ResourceValidationException, NotFoundException {
        this.validateTodo(todo);
        if(this.getOne(id) == null)
        {
            throw new NotFoundException("Todo not found");
        }
        LOGGER.info("Updating todo " + todo.toString());
        UpdateResult updatedTodo = todoCollection.updateOne(new Document("_id", id),
                new Document("$set", new Document("name", todo.getName())
                        .append("description", todo.getDescription()))
        );
        if(todo.getTasks() != null && todo.getTasks().size() > 0)
        {
            LOGGER.info("Updating tasks for the todo " + todo.getId());
            List<ObjectId> updatedOrCreatedTaskIds = taskDAO.createOrUpdateTasks(todo.getTasks(), todo.getId());
            this.removeTasksNotInList(updatedOrCreatedTaskIds, todo.getId());
        } else {
            LOGGER.info("No new task to add/update for the todo " + todo.getId());
        }

        LOGGER.info("Todo updated successfully " + todo.getId());
        return this.getOne(todo.getId());
    }

    private void removeTasksNotInList(List<ObjectId> updatedOrCreatedTaskIds, ObjectId id) {
        List<Task> tasks = taskDAO.findAllTodoTasks(id);
        LOGGER.info("All todo tasks " + tasks);
        List<ObjectId> idsToBeRemoved = new ArrayList<>();

        for(Task task : tasks)
        {
            ObjectId taskId = task.getId();
            boolean found = false;
            for(ObjectId updatedTaskId : updatedOrCreatedTaskIds)
            {
                if(updatedTaskId.equals(taskId))
                {
                    found = true;
                    break;
                }
            }
            if(!found)
            {
                idsToBeRemoved.add(taskId);
            }
        }

        taskDAO.deleteManyTasks(idsToBeRemoved);
    }
}
