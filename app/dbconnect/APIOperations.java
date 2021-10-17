package dbconnect;

import com.google.common.collect.ImmutableList;
import helpers.PartialUpdateRows;
import models.APIRoute;
import models.Status;
import models.UpdateAPIRoute;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Singleton
public class APIOperations {
    private final DBConnect _dbConnect;

    @Inject
    public APIOperations(DBConnect dbConnect) {
        _dbConnect = dbConnect;
    }
    /**
     * Get API Routes
     * @return list of API Routes
     */
    public ImmutableList<APIRoute> getRoutes() {
        final String sql = "SELECT rowid, route FROM apis WHERE deleted = 'FALSE';";
        ImmutableList.Builder<APIRoute> immutableListBuilder = new ImmutableList.Builder<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                APIRoute apiRoute = new APIRoute();
                apiRoute.setRowid(resultSet.getLong("rowid"));
                apiRoute.setRoute(resultSet.getString("route"));
                immutableListBuilder.add(apiRoute);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try { resultSet.close(); } catch (Exception e) { /* Ignored */ }
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
        return immutableListBuilder.build();
    }

    public ImmutableList<APIRoute> getTrashedRoutes() {
        final String sql = "SELECT rowid, route FROM apis WHERE deleted = 'TRUE';";
        ImmutableList.Builder<APIRoute> immutableListBuilder = new ImmutableList.Builder<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                APIRoute apiRoute = new APIRoute();
                apiRoute.setRowid(resultSet.getLong("rowid"));
                apiRoute.setRoute(resultSet.getString("route"));
                immutableListBuilder.add(apiRoute);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try { resultSet.close(); } catch (Exception e) { /* Ignored */ }
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
        return immutableListBuilder.build();
    }

    /**
     * Get API Route
     * @return list of API Routes
     */
    public APIRoute getRoute(String route) {
        final String sql = "SELECT rowid, * FROM apis WHERE route = '"+route+"' AND deleted = 'FALSE';";
        APIRoute apiRoute = new APIRoute();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                apiRoute.setRowid(resultSet.getLong("rowid"));
                apiRoute.setType(resultSet.getString("type"));
                apiRoute.setContent(resultSet.getString("content"));
                apiRoute.set_order(resultSet.getString("_order"));
                apiRoute.set_limit(resultSet.getInt("_limit"));
                apiRoute.setRoute(resultSet.getString("route"));
                apiRoute.setTemplate(resultSet.getString("template"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try { resultSet.close(); } catch (Exception e) { /* Ignored */ }
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
        return apiRoute;
    }

    /**
     * Set an API Route
     * @return DB Execution status
     */
    public String setRoute(String route) {
        if (route==null) {
            return Status.NO_INPUT.toString();
        }
        APIRoute apiRoute = new APIRoute();
        apiRoute.setRoute(route);
        final String insertSql = "INSERT INTO apis (route) VALUES ('" + apiRoute.getRoute() + "') " +
                "ON CONFLICT(route) DO UPDATE SET deleted = 'FALSE';";
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
     * Delete and API Route
     * @return DB Execution Status
     */
    public String deleteRoute(long id) {
        APIRoute apiRoute = new APIRoute();
        apiRoute.setRowid(id);
        final String sql = "DELETE FROM apis WHERE rowid = '" + apiRoute.getRowid() + "' AND deleted = 'TRUE';";
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
     * Update an existed API Route
     * @return DB Execution Status
     */
    public String updateRoute(UpdateAPIRoute updateAPIRoute) {
        APIRoute diff = updateAPIRoute.getApiRoute();
        final String sql = "UPDATE apis SET "+ PartialUpdateRows.getSets(diff)+" WHERE rowid = "+updateAPIRoute.getRowid()+";";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            if(diff.getRoute()!=null){
                String updateEntriesParent = "UPDATE entries SET parent = '"+diff.getRoute()+"', slug = '"+
                        diff.getRoute()+"' || name WHERE parent = (SELECT route FROM apis WHERE rowid = "+
                        updateAPIRoute.getRowid()+")";
                String updateTemplateParent = "UPDATE templates SET parent = '"+diff.getRoute()+"' WHERE parent = " +
                        "(SELECT route FROM apis WHERE " +
                        "rowid = "+updateAPIRoute.getRowid()+")";
                statement.addBatch(updateEntriesParent);
                statement.addBatch(updateTemplateParent);
            }
            statement.addBatch(sql);
            statement.executeBatch();
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
