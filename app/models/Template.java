package models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Template {
    private Long rowid;
    private String parent;
    private JsonNode fields;
    private JsonNode teaser;
    private Deleted deleted;
}
