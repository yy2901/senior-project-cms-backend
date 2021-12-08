package helpers;

import models.SQLNotSettable;
import models.Helpers.SqlGenerator.Inserts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlGenerator {
    /**
     * Parse POJO to a 2D Array
     * @param _object POJO
     * @return 2D Array [[key,value]]
     */
    private static List<List<String>> parsePojo(Object _object) {
        List<List<String>> result = new ArrayList<>();
        Field[] fields = _object.getClass().getDeclaredFields();
        try {
            for (Field f:fields) {
                f.setAccessible(true);
                List<String> list = new ArrayList<>();
                String t = f.getName();
                Object v = f.get(_object);
                if (v!=null && !f.isAnnotationPresent(SQLNotSettable.class)) {
                    list.add(t);
                    String value = "";
                    if(!f.getGenericType().getTypeName().equals("int") && !f.getGenericType().getTypeName().equals("long")){
                        value = "'" + v.toString() + "'";
                    } else {
                        value = v.toString();
                    }
                    list.add(value);
                    result.add(list);
                }
                f.setAccessible(false);
            }
        } catch(IllegalAccessException e) {
            System.out.println(e);
        }
        return result;
    }

    /**
     * Generate SET key value part of SQL
     * @param _object POJO
     * @return String "key1 = value1, key2 = value2"
     */
    public static String getSets(Object _object) {
        List<List<String>> partialUpdateList = parsePojo(_object);
        return partialUpdateList.stream().map(list->list.get(0)+" = "+list.get(1)).collect(Collectors.joining(", "));
    }

    /**
     * Generate INSERT key value part of SQL
     * @param _object POJO
     * @return Key: "key1, key2,..." Value: "value1, value2,..."
     */
    public static Inserts getInserts(Object _object) {
        List<List<String>> insertColList = parsePojo(_object);
        Inserts inserts = new Inserts();
        inserts.setKeys(insertColList.stream().map(list->list.get(0)).collect(Collectors.joining(", ")));
        inserts.setValues(insertColList.stream().map(list->list.get(1)).collect(Collectors.joining(", ")));
        return inserts;
    }
}
