import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

class FlywayListener implements ServletContextListener {

    private final DataSource dataSource;

    FlywayListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final Flyway flyway = new Flyway(Flyway.configure().dataSource(this.dataSource));
        flyway.baseline();
        flyway.migrate();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContextListener.super.contextDestroyed(servletContextEvent);
    }
}
