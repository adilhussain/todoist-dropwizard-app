package com.demo.db.configuration;

import java.util.List;

public class MongoDBConnection {

    /**
     * The credentials user and password.
     */
    private Credentials credentials;

    /**
     * The list of seeds.
     */
    private List<Seed> seeds;

    /**
     * The db.
     */
    private String database;

    /**
     * Gets the credentials.
     *
     * @return the value credentials.
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the credentials.
     *
     * @param credentials value.
     */
    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Gets the seeds.
     *
     * @return the value seeds.
     */
    public List<Seed> getSeeds() {
        return seeds;
    }

    /**
     * Sets the seeds.
     *
     * @param seeds value.
     */
    public void setSeeds(final List<Seed> seeds) {
        this.seeds = seeds;
    }

    /**
     * Gets the database.
     *
     * @return the value database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Sets the database.
     *
     * @param database value.
     */
    public void setDatabase(final String database) {
        this.database = database;
    }
}
