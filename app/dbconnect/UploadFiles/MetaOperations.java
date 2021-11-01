package dbconnect.UploadFiles;

import com.google.common.collect.ImmutableMap;
import dbconnect.DBConnect;
import helpers.SqlGenerator;
import models.Helpers.SqlGenerator.Inserts;
import models.UploadFiles.Meta;
import models.UploadFiles.UpdateMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class MetaOperations {
    private DBConnect _dbConnect;

    @Inject
    public MetaOperations(DBConnect dbConnect) {
        _dbConnect = dbConnect;
    }

    public String addMeta(Meta meta) {
        Inserts inserts = SqlGenerator.getInserts(meta);
        final String sql = "INSERT INTO uploadedFileMeta ("+inserts.getKeys()+") VALUES ("+inserts.getValues()+");";
        return _dbConnect.execute(sql);
    }

    public Meta getMeta(long rowid) {
        final String sql = "SELECT rowid, * FROM uploadedFileMeta WHERE rowid = "+rowid+";";
        Map<String,String> requiredCols = DBConnect.generateRequiredColumns(new Meta());
        List<Map<String,Object>> results = _dbConnect.getResults(sql,requiredCols);
        if(results.size()>0){
            return new Meta(results.get(0));
        }else{
            return null;
        }
    }

    public Meta getMeta(String fileName) {
        final String sql = "SELECT rowid, * FROM uploadedFileMeta WHERE fileName = '"+fileName+"';";
        Map<String,String> requiredCols = DBConnect.generateRequiredColumns(new Meta());
        List<Map<String,Object>> results = _dbConnect.getResults(sql,requiredCols);
        if(results.size()>0){
            return new Meta(results.get(0));
        }else{
            return null;
        }
    }

    public List<Meta> getUploadedFilesMeta(){
        final String sql = "SELECT rowid, * FROM uploadedFileMeta;";
        Map<String, String> requiredColumns = DBConnect.generateRequiredColumns(new Meta());
        List<Map<String, Object>> resultList = _dbConnect.getResults(sql, requiredColumns);
        List<Meta> results = resultList.stream().map(Meta::new).collect(Collectors.toList());
        return results;
    }

    public String updateMeta(UpdateMeta updateMetaRequest) {
        String sets = SqlGenerator.getSets(updateMetaRequest.getMeta());
        final String sql = "UPDATE uploadedFileMeta SET "+sets+" WHERE rowid = "+updateMetaRequest.getRowid()+";";
        return _dbConnect.execute(sql);
    }

    public String deleteMeta(long id) {
        final String sql = "DELETE FROM uploadedFileMeta WHERE rowid ="+id+";";
        return _dbConnect.execute(sql);
    }
}
