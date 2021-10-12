package helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartialUpdateRows {
    public static String getSets(Object _object) {
        List<List<String>> partialUpdateList = new ArrayList<>();
        Field[] fields = _object.getClass().getDeclaredFields();
        try {
            for (Field f:fields) {
                f.setAccessible(true);
                List<String> list = new ArrayList<>();
                String t = f.getName();
                Object v = f.get(_object);
                if (v!=null) {
                    list.add(t);
                    String value = "";
                    if(!f.getGenericType().getTypeName().equals("int") && !f.getGenericType().getTypeName().equals("long")){
                        value = "'" + v.toString() + "'";
                    } else {
                        value = v.toString();
                    }
                    list.add(value);
                    partialUpdateList.add(list);
                }
                f.setAccessible(false);
            }
        } catch(IllegalAccessException e) {
            System.out.println(e);
        }
        return partialUpdateList.stream().map(list->list.get(0)+" = "+list.get(1)).collect(Collectors.joining(", "));
    }
}
