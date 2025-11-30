package simulator;

import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;

public class Memory {
    private Map<Integer, Double> data; // For simplicity, assume double storage

    public Memory() {
        data = new HashMap<>();
        // Initialize some data
        data.put(0, 1.0);
        data.put(8, 1.0);
    }

    public double load(int address) {
        return data.getOrDefault(address, 0.0);
    }

    public void store(int address, Object value) {
        if (value instanceof Double) {
            data.put(address, (Double) value);
        } else if (value instanceof Integer) {
            data.put(address, ((Integer) value).doubleValue());
        }
    }
}