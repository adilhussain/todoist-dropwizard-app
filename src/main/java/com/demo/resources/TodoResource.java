package com.demo.resources;

import com.demo.api.Todo;
import com.demo.db.daos.TodoDAO;
import com.demo.exception.ResourceValidationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.NotFoundException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Api(value = "todos",
        description = "Todos REST API for handling Todo CRUD operations on todos collection.",
        tags = {"todos"})
@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {
    /**
     * Logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoResource.class);

    /**
     * DAO todo.
     */
    private TodoDAO todoDAO;

    /**
     * Constructor.
     *
     * @param todoDAO the dao todo.
     */
    public TodoResource(final TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    /**
     * Get all {@link Todo} objects.
     *
     * @return A object {@link Response} with the information of result this method.
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation success."),
            @ApiResponse(code = 404, message = "Todos not found")
    })
    @GET
    public Response all() {
        LOGGER.info("Listing all Todos.");
        final List<Todo> todosFind = todoDAO.getAll();
        if (todosFind.isEmpty()) {
            return Response.accepted(new com.demo.api.Response("Todos not found."))
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        return Response.ok(todosFind).build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation success."),
            @ApiResponse(code = 404, message = "Todo not found")
    })
    @GET
    @Path("/{id}")
    public Response getOne(@ApiParam(value = "id") @PathParam("id") @NotNull final ObjectId id) {
        LOGGER.info("Find the task by identifier : " + id.toString());
        final Todo todo = todoDAO.getOne(id);
        if (todo != null) {
            return Response.ok(todo).build();
        }
        return Response.accepted(new com.demo.api.Response("Todo not found.")).build();
    }

    /**
     * Persis a {@link Todo} object in a collection.
     *
     * @param todo The object to to persist.
     * @return A object {@link Response} with the information of result this method.
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation success.")
    })
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(@ApiParam(value = "Todo") @NotNull final Todo todo) {
        LOGGER.info("Persist a todo in collection with the information: {}", todo.toString());
        Todo createdTodo = null;
        try {
            createdTodo = todoDAO.save(todo);
        } catch (ResourceValidationException e) {
            LOGGER.error("Not saving todo. Got exception");
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        return Response.ok(createdTodo).status(Response.Status.CREATED).build();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation success.")
    })
    @PUT
    @Path("/{id}")
    public Response update(@ApiParam(value = "id") @PathParam("id") @NotNull final ObjectId id,
                           @ApiParam(value = "Todo") @NotNull final Todo todo) {
        LOGGER.info("Update the information of a todo : {} ", todo.toString());
        Todo updatedTodo;
        try {
            updatedTodo = todoDAO.update(id, todo);
        } catch (ResourceValidationException e) {
            LOGGER.error("Not saving todo. Got exception");
            LOGGER.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            LOGGER.error("Trying to update a todo which does not exist " + id);
            throw new BadRequestException(e.getMessage());
        }
        return Response.ok(updatedTodo).build();
    }

    /**
     * Delete a {@link Todo} object.
     * @param id   the identifier.
     * @return  A object {@link Response} with the information of result this method.
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation success.")
    })
    @DELETE
    @Path("/{id}")
    public Response delete(@ApiParam(value = "id") @PathParam("id") @NotNull final ObjectId id) {
        LOGGER.info("Delete a todo from collection with identifier: " + id.toString());
        boolean deletedStatus = todoDAO.delete(id);
        return Response.ok((new com.demo.api.Response()).showJSON("status", deletedStatus)).status(Response.Status.ACCEPTED).build();
    }
}
