package dbconnect;

import javax.inject.Singleton;

import java.sql.*;

/**
 * Provides database methods
 */
@Singleton
public class DBConnect {
    /**
     * Connect to the sqlite database
     * @return the connection
     */
    public Connection connect() {
        final String url = "jdbc:sqlite:/Users/yuhao/Documents/21fa/senior_project/db/cms-data.db";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        };
        return connection;
    }

    public DBConnect() {
        final String createApisTable =
                "CREATE TABLE IF NOT EXISTS apis (" +
                "route varchar NOT NULL UNIQUE, " +
                "content varchar, " +
                "_limit int, " +
                "type varchar, " +
                "_order varchar " +
                ");";
        final String createEntriesTable =
                "CREATE TABLE IF NOT EXISTS entries (" +
                "parent varchar NOT NULL, " +
                "time int, " +
                "content varchar, " +
                "teaser varchar, " +
                "title varchar, " +
                "name varchar NOT NULL, " +
                "slug varchar NOT NULL UNIQUE" +
                ");";
        final String createTemplatesTable =
                "CREATE TABLE IF NOT EXISTS templates (" +
                "parent varchar NOT NULL UNIQUE, " +
                "fields varchar, " +
                "teaser varchar" +
                ");";
        try {
            Connection connection = this.connect();
            Statement statement = connection.createStatement();
            statement.addBatch(createApisTable);
            statement.addBatch(createEntriesTable);
            statement.addBatch(createTemplatesTable);
            statement.executeBatch();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
