package models.UploadFiles;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class Artifact {
    public Artifact(){}
    public Artifact(Map<String, Object> map){
        this.fileName = (String) map.get("fileName");
        this.originalFile = (long) map.get("originalFile");
    }
    private String fileName;
    private long originalFile;
}
