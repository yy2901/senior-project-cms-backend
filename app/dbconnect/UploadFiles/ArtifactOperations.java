package dbconnect.UploadFiles;

import com.google.common.collect.ImmutableMap;
import dbconnect.DBConnect;
import models.UploadFiles.Artifact;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class ArtifactOperations {
    private DBConnect _dbConnect;

    @Inject
    public ArtifactOperations(DBConnect dbConnect) {
        _dbConnect = dbConnect;
    }

    public String addArtifact(String fileName, long originalFile) {
        long suffix = 1;
        String name = "NewArtifact";
        try{
            name = URLEncoder.encode(fileName.substring(0,fileName.lastIndexOf(".")), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e){}
        final String ext = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = name+ext;
        while(getArtifact(newFileName)!=null){
            newFileName=name+suffix+ext;
            suffix++;
        }
        final String sql = "INSERT INTO uploadedFileArtifacts (fileName, originalFile) VALUES ('" +
                newFileName+"', " +
                originalFile+ ")";
        _dbConnect.execute(sql);
        return newFileName;
    }

    public Artifact getArtifact(String fileName) {
        final String sql = "SELECT * FROM uploadedFileArtifacts WHERE fileName = '"+fileName+"';";
        Map<String, String> requiredColumns = DBConnect.generateRequiredColumns(Artifact.class);
        List<Map<String, Object>> resultSetList = _dbConnect.getResults(sql, requiredColumns);
        if(resultSetList.size()>0){
            Artifact artifact = new Artifact(resultSetList.get(0));
            return artifact;
        } else {
            return null;
        }
    }

    public String updateArtifact(String fileName, long originalFile){
        final String sql = "UPDATE uploadedFileArtifacts SET originalFile = "+originalFile+" WHERE fileName = '"+fileName+"';";
        return _dbConnect.execute(sql);
    }

    public List<String> deleteArtifacts(long originalFile) {
        final String selectArtifactsSql = "SELECT fileName FROM uploadedFileArtifacts WHERE originalFile = "+originalFile+";";
        Map<String,String> requiredColumns = ImmutableMap.of("fileName",String.class.getSimpleName());
        List<String> result = _dbConnect.getResults(selectArtifactsSql, requiredColumns).stream().map(r->(String) r.get("fileName"))
                .collect(Collectors.toList());
        final String sql = "DELETE FROM uploadedFileArtifacts WHERE originalFile = "+originalFile+";";
        _dbConnect.execute(sql);
        return result;
    }
}
