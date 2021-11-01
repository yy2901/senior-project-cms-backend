package models.UploadFiles;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class Meta {
    public Meta(){}
    public Meta(Map<String,Object> map) {
        this.rowid = (Long)map.get("rowid");
        this.fileName = (String) map.get("fileName");
        this.time = (long) map.get("time");
        this.size = (long) map.get("size");
        this.extension = (String) map.get("extension");
        this.details = (String) map.get("details");
    }
    private Long rowid;
    private String fileName;
    private long time;
    private long size;
    private String extension;
    private String details;
}
