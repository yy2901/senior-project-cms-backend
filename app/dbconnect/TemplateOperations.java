package dbconnect;

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
        final String sql = "SELECT rowid, parent FROM templates WHERE deleted = 'TRUE';";
        List<Map<String,Object>> results = _dbConnect.getResults(sql, DBConnect.generateRequiredColumns(Template.class));
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
        final String sql = "SELECT rowid, * FROM templates WHERE parent = '"+route+"' AND deleted = 'FALSE';";
        List<Map<String,Object>> results = _dbConnect.getResults(sql, DBConnect.generateRequiredColumns(Template.class));
        Template template = new Template();
        if(results.size()>=1){
            template.setRowid((long) results.get(0).get("rowid"));
            template.setParent((String) results.get(0).get("parent"));
            if(results.get(0).get("fields")!=null){
                template.setFields(Json.parse((String) results.get(0).get("fields")));
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
        Template template = new Template();
        template.setParent(route);
        final String insertSql = "INSERT INTO templates (parent) VALUES ('" + template.getParent() + "') " +
                "ON CONFLICT(parent) DO UPDATE SET deleted = 'FALSE';";
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
