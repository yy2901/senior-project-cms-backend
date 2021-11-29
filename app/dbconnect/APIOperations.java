package dbconnect;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import helpers.SqlGenerator;
import models.*;
import play.libs.Json;
import play.libs.Time;
import scala.Option;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<APIRoute> getRoutes() {
        final String sql = "SELECT rowid, route FROM apis WHERE deleted = 'FALSE';";
        List<Map<String,Object>> results = _dbConnect.getResults(sql, ImmutableMap.of(
                "rowid", long.class.getSimpleName(),
                "route", String.class.getSimpleName()
        ));
        return results.stream().map(result->{
            APIRoute apiRoute = new APIRoute();
            apiRoute.setRowid((long) result.get("rowid"));
            apiRoute.setRoute((String) result.get("route"));
            return apiRoute;
        }).collect(Collectors.toList());
    }

    public List<APIRoute> getTrashedRoutes() {
        final String sql = "SELECT rowid, route FROM apis WHERE deleted = 'TRUE';";
        List<Map<String,Object>> results = _dbConnect.getResults(sql, ImmutableMap.of(
                "rowid", long.class.getSimpleName(),
                "route", String.class.getSimpleName()
        ));
        return results.stream().map(result->{
            APIRoute apiRoute = new APIRoute();
            apiRoute.setRowid((long) result.get("rowid"));
            apiRoute.setRoute((String) result.get("route"));
            return apiRoute;
        }).collect(Collectors.toList());
    }

    /**
     * Get API Route
     * @return list of API Routes
     */
    public APIRoute getRoute(String route) {
        final String sql = "SELECT rowid, * FROM apis WHERE route = '"+route+"' AND deleted = 'FALSE';";
        List<Map<String,Object>> results = _dbConnect.getResults(sql, DBConnect.generateRequiredColumns(APIRoute.class));
        APIRoute apiRoute = new APIRoute();
        if(results.size()>=1){
            Map<String, Object> result = results.get(0);
            apiRoute.setRowid((long) result.get("rowid"));
            if(result.get("content")!=null){
                apiRoute.setContent(Json.parse((String) result.get("content")));
            }
            apiRoute.setRoute((String) result.get("route"));
            if(result.get("template")!=null){
                apiRoute.setTemplate(Json.parse((String) result.get("template")));
            }
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
        final String sql = "INSERT INTO apis (route) VALUES ('" + apiRoute.getRoute() + "') " +
                "ON CONFLICT(route) DO UPDATE SET deleted = 'FALSE';";
        return _dbConnect.execute(sql);
    }

    /**
     * Delete and API Route
     * @return DB Execution Status
     */
    public String deleteRoute(long id) {
        APIRoute apiRoute = new APIRoute();
        apiRoute.setRowid(id);
        final String sql = "DELETE FROM apis WHERE rowid = '" + apiRoute.getRowid() + "' AND deleted = 'TRUE';";
        return _dbConnect.execute(sql);
    }

    /**
     * Update an existed API Route
     * @return DB Execution Status
     */
    public String updateRoute(UpdateAPIRoute updateAPIRoute) {
        APIRoute diff = updateAPIRoute.getApiRoute();
        List<String> sqls = new ArrayList<>();
        final String sql = "UPDATE apis SET "+ SqlGenerator.getSets(diff)+" WHERE rowid = "+updateAPIRoute.getRowid()+";";
        sqls.add(sql);
        if(diff.getRoute()!=null){
            String updateEntriesParent = "UPDATE entries SET parent = '"+diff.getRoute()+"', slug = '"+
                    diff.getRoute()+"' || name WHERE parent = (SELECT route FROM apis WHERE rowid = "+
                    updateAPIRoute.getRowid()+")";
            String updateTemplateParent = "UPDATE templates SET parent = '"+diff.getRoute()+"' WHERE parent = " +
                    "(SELECT route FROM apis WHERE " +
                    "rowid = "+updateAPIRoute.getRowid()+")";
            sqls.add(updateEntriesParent);
            sqls.add(updateTemplateParent);
        }
        return _dbConnect.executeBatch(sqls);
    }

    public List<ObjectNode> getCollection(int page, String parent, Option<Integer> _itemsPerPage, Option<String> _timeOrder ){
        int itemsPerPage;
        if (_itemsPerPage.isEmpty()) {
           itemsPerPage = 5;
        } else {
            itemsPerPage = _itemsPerPage.get();
        }
        TimeOrder timeOrder;
        if (_timeOrder.isEmpty()) {
            timeOrder = TimeOrder.DESC;
        } else {
            timeOrder = TimeOrder.valueOf(_timeOrder.get());
        }
        int offset = (page - 1) * itemsPerPage;
        final String sql = "SELECT rowid, slug, title, teaser, time FROM entries WHERE parent = '/" + parent + "' ORDER BY time " + timeOrder + " LIMIT " + offset + ", " + itemsPerPage + ";";
        List<Map<String,Object>> rawResults = _dbConnect.getResults(sql, ImmutableMap.of(
                "rowid", long.class.getSimpleName(),
                "slug", String.class.getSimpleName(),
                "time", long.class.getSimpleName(),
                "title", String.class.getSimpleName(),
                "teaser", String.class.getSimpleName()
        ));
        List<ObjectNode> results = rawResults.stream().map(rawResult->{
            ObjectNode result = Json.newObject();
            result.put("id",(long)rawResult.get("rowid"));
            result.put("time",((long)rawResult.get("time")));
            result.put("slug",((String)rawResult.get("slug")));
            result.put("title",((String)rawResult.get("title")));
            if(rawResult.get("teaser")!=null){
                result.set("teaser",(Json.parse((String) rawResult.get("teaser"))));
            }
            return result;
        }).collect(Collectors.toList());
        return results;
    }
}
