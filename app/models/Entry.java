package models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Entry {
    private Long rowid;
    private String parent;
    private Long time;
    private JsonNode content;
    private JsonNode teaser;
    private String title;
    private String name;
    private String slug;
    private Deleted deleted;

    public void setParent(String _parent) {
        parent = _parent;
        slug = parent + name;
    }

    public void setName(String _name) {
        name = _name;
        slug = parent + name;
    }
}
