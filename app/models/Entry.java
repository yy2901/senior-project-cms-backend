package models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Entry {
    private Long rowid;
    private String parent;
    private Long time;
    private String content;
    private String teaser;
    private String title;
    private String slug;
}
