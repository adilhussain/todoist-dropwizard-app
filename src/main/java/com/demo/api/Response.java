package com.demo.api;


import java.util.HashMap;

public class Response {

    /**
     * The message.
     */
    private String message;

    public Response()
    {

    }

    /**
     * Constructor.
     * @param message the message.
     */
    public Response(final String message) {
        this.message = message;
    }


    /**
     * Gets the message.
     *
     * @return the value message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message value.
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    public Object showJSON(String status, boolean deletedStatus) {
        HashMap<String, Boolean> result = new HashMap<>();
        result.put(status, deletedStatus);
        return result;
    }
}
