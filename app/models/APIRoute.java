package models;

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
    private String content;
    private Integer _limit;
    private Type type;
    private Order _order;

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
