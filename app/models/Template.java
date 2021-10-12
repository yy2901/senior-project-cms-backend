package models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Template {
    private Long rowid;
    private String parent;
    private String fields;
    private String teaser;
}
