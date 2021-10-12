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
        final String sql = "SELECT rowid, route, type FROM apis;";
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
                apiRoute.setType(resultSet.getString("type"));
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
        final String sql = "SELECT rowid, * FROM apis WHERE route = '"+route+"';";
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
        if (route.isEmpty()) {
            return Status.NO_INPUT.toString();
        }
        APIRoute apiRoute = new APIRoute();
        apiRoute.setRoute(route);
        final String insertSql = "INSERT INTO apis (route, _order, type, _limit) VALUES ('" + apiRoute.getRoute() + "', 'DESC', 'SINGLE', 10);";
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
    public String deleteRoute(String route) {
        if (route.isEmpty()) {
            return Status.NO_INPUT.toString();
        }
        APIRoute apiRoute = new APIRoute();
        apiRoute.setRoute(route);
        final String sql = "DELETE FROM apis WHERE route = '" + apiRoute.getRoute() + "';";
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
