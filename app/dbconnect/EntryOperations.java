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
        final String sql = "SELECT rowid, title, slug FROM entries WHERE deleted = 'TRUE';";
        List<Map<String, Object>> results = _dbConnect.getResults(sql, ImmutableMap.of(
                "rowid", long.class.getSimpleName(),
                "title", String.class.getSimpleName(),
                "slug", String.class.getSimpleName()
        ));
        return results.stream().map(result->{
            Entry entry = new Entry();
            entry.setRowid((long)result.get("rowid"));
            entry.setTitle((String) result.get("title"));
            entry.setSlug((String) result.get("slug"));
            return entry;
        }).collect(Collectors.toList());
    }

    /**
     * Get Entries
     * @return list of Entries
     */
    public List<Entry> getEntries(String parent) {
        final String sql = "SELECT rowid, title, name, parent, slug FROM entries WHERE parent = '"+parent+"' AND deleted = 'FALSE' ORDER BY time DESC;";
        List<Map<String, Object>> results = _dbConnect.getResults(sql, ImmutableMap.of(
                "rowid", long.class.getSimpleName(),
                "title", String.class.getSimpleName(),
                "name", String.class.getSimpleName(),
                "parent", String.class.getSimpleName(),
                "slug", String.class.getSimpleName()
        ));
        return results.stream().map(result->{
            Entry entry = new Entry();
            entry.setRowid((long) result.get("rowid"));
            entry.setTitle((String) result.get("title"));
            entry.setParent((String) result.get("parent"));
            entry.setName((String) result.get("name"));
            entry.setSlug((String) result.get("slug"));
            return entry;
        }).collect(Collectors.toList());
    }

    /**
     * Get the Entry
     * @return the Entry
     */
    public Entry getEntry(String slug) {
        final String sql = "SELECT rowid, * FROM entries WHERE slug = '"+slug+"' AND deleted = 'FALSE';";
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
            entry.setSlug((String) result.get("slug"));
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
        Entry entry = new Entry();
        entry.setName(name);
        entry.setParent(parent);
        entry.setTitle(title);
        final String sql = "INSERT INTO entries (parent, name, slug, time, title) VALUES ('" + entry.getParent() + "','" +
                entry.getName() + "','" +
                entry.getSlug() + "', " +
                timestamp.getTime() + ", '" +
                entry.getTitle()+ "') " +
                "ON CONFLICT(slug) DO UPDATE SET deleted = 'FALSE';";
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
        return _dbConnect.execute(sql);
    }
}
