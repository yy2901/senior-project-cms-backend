package models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

/**
 * all urls should not be closed by forward slash
 * url depth can only be 1
 * example: '/news'
 */
@Getter @Setter
public class APIRoute {
    private Long rowid;
    private String route;
    private JsonNode content;//content of the route when this route is a single type
    private JsonNode template;//template of the content when this route is a single type
    private Integer _limit;
    private Type type;
    private Order _order;
    private Deleted deleted;

    private enum Type {
        SINGLE, COLLECTION
    }
    private enum Order {
        ASC, DESC
    }

    public void setType(String _type) {
        type = Type.valueOf(_type);
    }

    public void set_order(String __order) {
        _order = Order.valueOf(__order);
    }
}
