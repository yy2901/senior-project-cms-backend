package dbconnect;

import helpers.PartialUpdateRows;
import models.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Singleton
public class TemplateOperations {
    private final DBConnect _dbConnect;

    @Inject
    public TemplateOperations(DBConnect dbConnect) {
        _dbConnect = dbConnect;
    }

    /**
     * Get Template
     * @return the Template
     */
    public Template getTemplate(String route) {
        final String sql = "SELECT rowid, * FROM templates WHERE parent = '"+route+"';";
        Template template = new Template();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                template.setRowid(resultSet.getLong("rowid"));
                template.setParent(resultSet.getString("parent"));
                template.setFields(resultSet.getString("fields"));
                template.setTeaser(resultSet.getString("teaser"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try { resultSet.close(); } catch (Exception e) { /* Ignored */ }
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
        return template;
    }

    /**
     * Create a template
     * @return DB Execution status
     */
    public String createTemplate(String route) {
        if (route.isEmpty()) {
            return Status.NO_INPUT.toString();
        }
        Template template = new Template();
        template.setParent(route);
        final String insertSql = "INSERT INTO templates (parent) VALUES ('" + template.getParent() + "');";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            boolean execute =  statement.execute(insertSql);
            return Status.SUCCESS.toString();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } finally {
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
    }

    /**
     * Delete the template
     * @return DB Execution Status
     */
    public String deleteTemplate(long rowid) {
        Template template = new Template();
        template.setRowid(rowid);
        final String sql = "DELETE FROM templates WHERE rowid = " + template.getRowid() + ";";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            boolean execute = statement.execute(sql);
            return Status.SUCCESS.toString();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } finally {
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
    }

    /**
     * Update an existed template
     * @return DB Execution Status
     */
    public String updateTemplate(UpdateTemplate updateTemplate) {
        Template diff = updateTemplate.getTemplate();
        final String sql = "UPDATE templates SET "+ PartialUpdateRows.getSets(diff)+" WHERE rowid = "+updateTemplate.getRowid()+";";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            boolean execute = statement.execute(sql);
            return Status.SUCCESS.toString();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        } finally {
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
    }
}
