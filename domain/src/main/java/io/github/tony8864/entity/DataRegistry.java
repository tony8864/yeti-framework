package io.github.tony8864.entity;

import java.util.HashMap;
import java.util.Map;

public class DataRegistry {
    private static final Map<String, DataRef> data = new HashMap<>();

    public static void register(String name, Object value) {
        data.put(name, new DataRef(name, value));
    }

    public static DataRef get(String name) {
        DataRef ref = data.get(name);
        if (ref == null) {
            throw new IllegalArgumentException("No data registered with name: " + name);
        }
        return ref;
    }

    static void clear() {
        data.clear();
    }
}
