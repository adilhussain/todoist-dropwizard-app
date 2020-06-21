package com.demo.db.configuration;

import com.demo.util.PasswordSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Credentials {

    /** The user name.*/
    private String username;

    /** The password.*/
    @JsonSerialize(using = PasswordSerializer.class)
    private char[] password;

    /**
     * Gets the user name.
     * @return  the user name.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user name.
     * @param username  the user name.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the user name.
     * @return  the user name.
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password  the password.
     */
    public void setPassword(final char[] password) {
        this.password = password;
    }
}
