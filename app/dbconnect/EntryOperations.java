package dbconnect;

import com.google.common.collect.ImmutableList;
import helpers.PartialUpdateRows;
import models.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;

@Singleton
public class EntryOperations {
    private final DBConnect _dbConnect;

    @Inject
    public EntryOperations(DBConnect dbConnect) {
        _dbConnect = dbConnect;
    }

    /**
     * Get Entries
     * @return list of Entries
     */
    public ImmutableList<Entry> getEntries(String parent) {
        final String sql = "SELECT rowid, title, name, parent, slug FROM entries WHERE parent = '"+parent+"' ORDER BY time DESC;";
        ImmutableList.Builder<Entry> immutableListBuilder = new ImmutableList.Builder<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Entry entry = new Entry();
                entry.setRowid(resultSet.getLong("rowid"));
                entry.setTitle(resultSet.getString("title"));
                entry.setParent(resultSet.getString("parent"));
                entry.setName(resultSet.getString("name"));
                entry.setSlug(resultSet.getString("slug"));
                immutableListBuilder.add(entry);
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
     * Get the Entry
     * @return the Entry
     */
    public Entry getEntry(String slug) {
        final String sql = "SELECT rowid, * FROM entries WHERE slug = '"+slug+"';";
        Entry entry = new Entry();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = _dbConnect.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                entry.setRowid(resultSet.getLong("rowid"));
                entry.setParent(resultSet.getString("parent"));
                entry.setContent(resultSet.getString("content"));
                entry.setTeaser(resultSet.getString("teaser"));
                entry.setTime(resultSet.getLong("time"));
                entry.setName(resultSet.getString("name"));
                entry.setTitle(resultSet.getString("title"));
                entry.setSlug(resultSet.getString("slug"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try { resultSet.close(); } catch (Exception e) { /* Ignored */ }
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
        return entry;
    }

    /**
     * Create an Entry
     * @return DB Execution status
     */
    public String createEntry(String parent, String name) {
        if (name.isEmpty()||parent.isEmpty()) {
            return Status.NO_INPUT.toString();
        }
        Timestamp timestamp =  new Timestamp(System.currentTimeMillis());
        Entry entry = new Entry();
        entry.setName(name);
        entry.setParent(parent);
        final String insertSql = "INSERT INTO entries (parent, name, slug, time) VALUES ('" + entry.getParent() + "','" +
                entry.getName() + "','" +
                entry.getSlug() + "', " +
                timestamp.getTime() + ");";
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
     * Delete an Entry
     * @return DB Execution Status
     */
    public String deleteEntry(long rowid) {
        Entry entry = new Entry();
        entry.setRowid(rowid);
        final String sql = "DELETE FROM entries WHERE rowid = " + entry.getRowid() + ";";
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
     * Update an existed Entry
     * @return DB Execution Status
     */
    public String updateEntry(UpdateEntry updateEntry) {
        Entry diff = updateEntry.getEntry();
        final String sql = "UPDATE entries SET "+ PartialUpdateRows.getSets(diff)+" WHERE rowid = "+updateEntry.getRowid()+";";
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
