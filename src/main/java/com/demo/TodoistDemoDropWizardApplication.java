package com.demo;

import com.demo.db.MongoDBFactoryConnection;
import com.demo.db.MongoDBManaged;
import com.demo.db.daos.TaskDAO;
import com.demo.db.daos.TodoDAO;
import com.demo.health.TodoistDemoHealthCheck;

import com.demo.resources.TodoResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TodoistDemoDropWizardApplication extends Application<TodoistDemoDropWizardConfiguration> {

    /**
     * Logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoistDemoDropWizardApplication.class);

    /**
     * Entry point for the Application.
     *
     * @param args the args.
     * @throws Exception when the app can not start.
     */
    public static void main(final String[] args) throws Exception {
        LOGGER.info("Starting application...");
        new TodoistDemoDropWizardApplication().run(args);
    }

    @Override
    public String getName() {
        return "TodoistDemoDropWizardApplication";
    }

    @Override
    public void initialize(final Bootstrap<TodoistDemoDropWizardConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<TodoistDemoDropWizardConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
                        final TodoistDemoDropWizardConfiguration todoistDemoDropWizardConfiguration) {
                return todoistDemoDropWizardConfiguration.getSwaggerBundleConfiguration();
            }
        });
    }

    @Override
    public void run(final TodoistDemoDropWizardConfiguration configuration,
                    final Environment environment) {

        final MongoDBFactoryConnection mongoDBManagerConn = new MongoDBFactoryConnection(configuration.getMongoDBConnection());

        final MongoDBManaged mongoDBManaged = new MongoDBManaged(mongoDBManagerConn.getClient());

        final TaskDAO taskDAO = new TaskDAO(mongoDBManagerConn.getClient()
                .getDatabase(configuration.getMongoDBConnection().getDatabase())
                .getCollection("tasks"));

        final TodoDAO todoDAO = new TodoDAO(mongoDBManagerConn.getClient()
            .getDatabase(configuration.getMongoDBConnection().getDatabase())
            .getCollection("todos"), taskDAO);

        environment.lifecycle().manage(mongoDBManaged);
        environment.jersey().register(new TodoResource(todoDAO));
        environment.healthChecks().register("TodoistDemoHealthCheck",
                new TodoistDemoHealthCheck(mongoDBManagerConn.getClient()));
    }

}
