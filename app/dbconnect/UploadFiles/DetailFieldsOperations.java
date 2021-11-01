package dbconnect.UploadFiles;

import com.google.common.collect.ImmutableMap;
import dbconnect.DBConnect;
import models.UploadFiles.DetailFields;
import models.UploadFiles.UpdateDetailFields;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Singleton
public class DetailFieldsOperations {
    private DBConnect _dbConnect;
    @Inject
    public DetailFieldsOperations(DBConnect dbConnect){
        _dbConnect = dbConnect;
    }

    public String getDetailFields(String name) {
        final String sql = "SELECT fields FROM uploadedFileDetailFields WHERE name = '" +
                name +
                "'; ";
        Map<String, String> requiredCols = ImmutableMap.of("fields",String.class.getSimpleName());
        List<Map<String,Object>> rawResult = _dbConnect.getResults(sql, requiredCols);
        if(rawResult.size()>0){
            return (String) rawResult.get(0).get("fields");
        } else {
            return null;
        }
    }

    public String insertFields(DetailFields detailFields) {
        final String sql = "INSERT INTO uploadedFileDetailFields (name, fields) VALUES ('" +
                detailFields.getName() +
                "','" +
                detailFields.getFields() +
                "');";
        return _dbConnect.execute(sql);
    }

    public String updateFields(UpdateDetailFields updateDetailFields) {
        final String sql = "UPDATE uploadedFileDetailFields SET name = '" +
                updateDetailFields.getDetail().getName() +
                "', fields = '" +
                updateDetailFields.getDetail().getFields() +
                "' WHERE name = '" +
                updateDetailFields.getName() +
                "';";
        return _dbConnect.execute(sql);
    }

    public String deleteFields(String name) {
        final String sql = "DELETE FROM uploadedFileDetailFields WHERE name='" +
                name +
                "';";
        return _dbConnect.execute(sql);
    }
}
