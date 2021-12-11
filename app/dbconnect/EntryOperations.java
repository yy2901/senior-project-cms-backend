package dbconnect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.SqlGenerator;
import models.*;
import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class EntryOperations {
    private final DBConnect _dbConnect;

    @Inject
    public EntryOperations(DBConnect dbConnect) {
        _dbConnect = dbConnect;
    }

    public List<Entry> getTrashedEntries() {
        final String sql = "SELECT l.rowid, l.title, l.name, r.route AS parent FROM entries l JOIN apis r ON r.rowid = l.parent WHERE l.deleted = 'TRUE';";
        List<Map<String, Object>> results = _dbConnect.getResults(sql, ImmutableMap.of(
                "rowid", long.class.getSimpleName(),
                "title", String.class.getSimpleName(),
                "name", String.class.getSimpleName(),
                "parent", String.class.getSimpleName()
        ));
        return results.stream().map(result->{
            Entry entry = new Entry();
            entry.setRowid((long)result.get("rowid"));
            entry.setTitle((String) result.get("title"));
            entry.setName((String) result.get("name"));
            entry.setParent((String) result.get("parent"));
            return entry;
        }).collect(Collectors.toList());
    }

    /**
     * Get Entries
     * @return list of Entries
     */
    public List<Entry> getEntries(String parent) {
        final String sql = String.format("SELECT l.rowid, l.title, l.name, r.route AS parent, l.time FROM entries l\n" +
                "JOIN apis r ON r.rowid = l.parent\n" +
                "WHERE r.route = '%s' AND l.deleted = 'FALSE' ORDER BY l.time DESC;", parent);
        List<Map<String, Object>> results = _dbConnect.getResults(sql, ImmutableMap.<String,String>builder()
                        .put("rowid", long.class.getSimpleName())
                        .put("title", String.class.getSimpleName())
                        .put("name", String.class.getSimpleName())
                        .put("parent", String.class.getSimpleName())
                        .put("time", long.class.getSimpleName())
                .build()
        );
        return results.stream().map(result->{
            Entry entry = new Entry();
            entry.setRowid((long) result.get("rowid"));
            entry.setTitle((String) result.get("title"));
            entry.setParent((String) result.get("parent"));
            entry.setName((String) result.get("name"));
            entry.setTime((long) result.get("time"));
            return entry;
        }).collect(Collectors.toList());
    }

    /**
     * Get the Entry
     * @return the Entry
     */
    public Entry getEntry(String parent, String name) {
        final String sql = String.format("SELECT l.rowid, l.name, r.route AS parent, l.deleted, l.title, l.time, l.content, l.teaser\n" +
                "FROM entries l JOIN apis r ON r.rowid=l.parent WHERE r.route = '%s' AND l.name = '%s' AND l.deleted = 'FALSE';",
                parent, name
        );
        List<Map<String, Object>> results = _dbConnect.getResults(sql, DBConnect.generateRequiredColumns(Entry.class));
        Entry entry = new Entry();
        if(results.size()>=1){
            Map<String, Object> result = results.get(0);
            entry.setRowid((long) result.get("rowid"));
            entry.setParent((String) result.get("parent"));
            if(result.get("content")!=null){
                entry.setContent(Json.parse((String) result.get("content")));
            }
            if(result.get("teaser")!=null){
                entry.setTeaser(Json.parse((String) result.get("teaser")));
            }
            entry.setTime((long) result.get("time"));
            entry.setName((String) result.get("name"));
            entry.setTitle((String) result.get("title"));
        }
       return entry;
    }

    /**
     * Create an Entry
     * @return DB Execution status
     */
    public String createEntry(String parent, String name, String title) {
        if (name==null||parent==null) {
            return Status.NO_INPUT.toString();
        }
        Timestamp timestamp =  new Timestamp(System.currentTimeMillis());
        final String sql = String.format(
                "INSERT INTO entries (parent, name, time, title) VALUES ((SELECT rowid FROM apis WHERE route='%s'), '%s', %d, '%s') ON CONFLICT(parent, name) DO UPDATE SET deleted = 'FALSE';",
                parent, name, timestamp.getTime(), title);
        System.out.println(sql);
        return _dbConnect.execute(sql);
    }

    /**
     * Delete an Entry
     * @return DB Execution Status
     */
    public String deleteEntry(long rowid) {
        Entry entry = new Entry();
        entry.setRowid(rowid);
        final String sql = "DELETE FROM entries WHERE rowid = " + entry.getRowid() + " AND deleted = 'TRUE';";
        return _dbConnect.execute(sql);
    }

    /**
     * Update an existed Entry
     * @return DB Execution Status
     */
    public String updateEntry(UpdateEntry updateEntry) {
        Entry diff = updateEntry.getEntry();
        final String sql = "UPDATE entries SET "+ SqlGenerator.getSets(diff)+" WHERE rowid = "+updateEntry.getRowid()+";";
        System.out.println(sql);
        return _dbConnect.execute(sql);
    }
}
