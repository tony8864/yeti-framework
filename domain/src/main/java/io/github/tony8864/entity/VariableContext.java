package io.github.tony8864.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableContext {
    private final Map<String, Object> variables = new HashMap<>();

    public void put(String key, Object value) {
        variables.put(key, value);
    }

    public Object get(String key) {
        return variables.get(key);
    }

    public String resolvePlaceholders(String input) {
        Pattern p = Pattern.compile("\\$\\{(.+?)}");
        Matcher m = p.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String varName = m.group(1);
            Object val = variables.get(varName);
            if (val == null) throw new IllegalArgumentException("Variablenot found: " + varName);
            m.appendReplacement(sb, val.toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
