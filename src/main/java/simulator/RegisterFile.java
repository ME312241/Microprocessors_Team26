package simulator;

import java.util.HashMap;
import java.util.Map;

public class RegisterFile {
    private Map<String, Object> values;
    private Map<String, String> tags;

    public RegisterFile() {
        values = new HashMap<>();
        tags = new HashMap<>();
        // Initialize some registers
        for (int i = 0; i < 32; i++) {
            values.put("R" + i, 0);
            tags.put("R" + i, null);
        }
        for (int i = 0; i < 32; i++) {
            values.put("F" + i, 0.0);
            tags.put("F" + i, null);
        }
    }

    public boolean hasValue(String reg) {
        return tags.get(reg) == null;
    }

    public Object getValue(String reg) {
        return values.get(reg);
    }

    public String getTag(String reg) {
        return tags.get(reg);
    }

    public void setTag(String reg, String tag) {
        tags.put(reg, tag);
    }

    public void setValue(String reg, Object value) {
        values.put(reg, value);
        tags.put(reg, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append("R").append(i).append(":").append(values.get("R" + i)).append(" ");
        }
        for (int i = 0; i < 32; i++) {
            sb.append("F").append(i).append(":").append(values.get("F" + i)).append(" ");
        }
        return sb.toString();
    }
}