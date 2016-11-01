
/**
 * Json extraction helper
 *
 * @author Andrei
 *
 */
package au.com.auspost.startrack_corp.core.helpers;

import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

/**
 * Json extraction helper
 */
public class Json {
    // constants
    private static final String INDEX = "index";

    static class Indexer {
        Integer i = 1;
        Integer count = 0;
        JSONObject obj = null;
    };

    public static List<Map<String, Object>> extractJsonArray(String[] array) {
        if (array != null && array.length > 0) {
            Indexer indexer = new Indexer();
            return Arrays.stream(array).map(o -> {
                Map<String, Object> item = new HashMap<String, Object>();
                try {
                    indexer.obj = new JSONObject(o);
                    Iterable<String> iterable = () -> indexer.obj.keys();
                    indexer.count += StreamSupport.stream(iterable.spliterator(), false).map(oo -> {
                        try {
                            item.put(oo, indexer.obj.get(oo));
                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                        return 1;
                    }).reduce(0, (x, y) -> x + y);
                    item.put(INDEX, indexer.i++);
                }
                catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
                return item;
            }).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
