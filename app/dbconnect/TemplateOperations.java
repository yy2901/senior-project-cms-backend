package dbconnect;

import com.google.common.collect.ImmutableMap;
import helpers.SqlGenerator;
import models.*;
import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class TemplateOperations {
    private final DBConnect _dbConnect;

    @Inject
    public TemplateOperations(DBConnect dbConnect) {
        _dbConnect = dbConnect;
    }

    public List<Template> getTrashedTemplates() {
        final String sql = "SELECT l.rowid, r.route AS parent FROM templates l INNER JOIN apis r ON r.rowid = l.parent WHERE l.deleted = 'TRUE';";
        List<Map<String,Object>> results = _dbConnect.getResults(sql, ImmutableMap.of(
                "rowid", long.class.getSimpleName(),
                "parent", String.class.getSimpleName()
        ));
        return results.stream().map(result->{
            Template template = new Template();
            template.setRowid((long) result.get("rowid"));
            template.setParent((String) result.get("parent"));
            return template;
        }).collect(Collectors.toList());
    }

    /**
     * Get Template
     * @return the Template
     */
    public Template getTemplate(String route) {
        final String sql = String.format(
                "SELECT l.rowid, r.route AS parent, l.content, l.teaser, l.deleted FROM templates l JOIN apis r ON r.rowid = l.parent WHERE r.route = '%s' AND l.deleted = 'FALSE';",
                route
        );
        List<Map<String,Object>> results = _dbConnect.getResults(sql, DBConnect.generateRequiredColumns(Template.class));
        Template template = new Template();
        if(results.size()>=1){
            template.setRowid((long) results.get(0).get("rowid"));
            template.setParent((String) results.get(0).get("parent"));
            template.setDeleted(Deleted.valueOf((String) results.get(0).get("deleted")));
            if(results.get(0).get("content")!=null){
                template.setContent(Json.parse((String) results.get(0).get("content")));
            }
            if(results.get(0).get("teaser")!=null){
                template.setTeaser(Json.parse((String) results.get(0).get("teaser")));
            }
        }
        return template;
    }

    /**
     * Create a template
     * @return DB Execution status
     */
    public String createTemplate(String route) {
        if (route==null) {
            return Status.NO_INPUT.toString();
        }
        final String insertSql = String.format("INSERT INTO templates (parent) VALUES ((SELECT rowid FROM apis WHERE route = '%s')) " +
                "ON CONFLICT(parent) " +
                "DO UPDATE SET deleted = 'FALSE';", route);
        return _dbConnect.execute(insertSql);
    }

    /**
     * Delete the template
     * @return DB Execution Status
     */
    public String deleteTemplate(long rowid) {
        Template template = new Template();
        template.setRowid(rowid);
        final String sql = "DELETE FROM templates WHERE rowid = " + template.getRowid() + " AND deleted = 'TRUE';";
        return _dbConnect.execute(sql);
    }

    /**
     * Update an existed template
     * @return DB Execution Status
     */
    public String updateTemplate(UpdateTemplate updateTemplate) {
        Template diff = updateTemplate.getTemplate();
        final String sql = "UPDATE templates SET "+ SqlGenerator.getSets(diff)+" WHERE rowid = "+updateTemplate.getRowid()+";";
        return _dbConnect.execute(sql);
    }
}
