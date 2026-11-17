package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.*;

/**
 * Non-destructive additions required when upgrading a pre-Flyway installation.
 * This migration never drops existing recipe/user data or pre-existing tables.
 */
public class V2__UpgradeLegacyRecipeColumns extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        ensure(connection, "recipe_info", "published", "BIT(1) NOT NULL DEFAULT b'1'");
        ensure(connection, "recipe_info", "servings", "INT NOT NULL DEFAULT 2");
        ensure(connection, "recipe_info", "prep_minutes", "INT NOT NULL DEFAULT 10");
        ensure(connection, "recipe_info", "cook_minutes", "INT NOT NULL DEFAULT 20");
        ensure(connection, "recipe_info", "image_data", "LONGBLOB NULL");
        ensure(connection, "recipe_info", "image_content_type", "VARCHAR(30) NULL");
    }

    private void ensure(Connection connection, String table, String column, String definition) throws SQLException {
        try (ResultSet rs = connection.getMetaData().getColumns(connection.getCatalog(), null, table, column)) {
            if (rs.next()) return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
        }
    }
}
