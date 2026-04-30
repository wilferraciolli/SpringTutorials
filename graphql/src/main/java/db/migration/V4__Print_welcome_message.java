package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;

public class V4__Print_welcome_message extends BaseJavaMigration {
    private static final Logger logger = LoggerFactory.getLogger(V4__Print_welcome_message.class);

    @Override
    public void migrate(Context context) throws Exception {
// 1. Printing to the console/logs
        System.out.println("==============================================");
        System.out.println("FLYWAY JAVA MIGRATION: Initializing extra data...");
        System.out.println("==============================================");

        logger.info("Java migration V4 is running. You can use JDBC here if needed.");

        // 2. You can also execute raw SQL via the context if needed
        try (Statement select = context.getConnection().createStatement()) {
            // For example, update a specific row that needs complex Java logic
            // select.execute("UPDATE users SET password = 'hashed_manually' WHERE id = 1");
        }
    }
}
