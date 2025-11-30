package simulator;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    private int size, blockSize, hitLatency, missPenalty;
    private Map<Integer, CacheBlock> blocks;

    public Cache(int size, int blockSize, int hitLatency, int missPenalty) {
        this.size = size;
        this.blockSize = blockSize;
        this.hitLatency = hitLatency;
        this.missPenalty = missPenalty;
        blocks = new HashMap<>();
    }

    public boolean isHit(int address) {
        int blockIndex = address / blockSize;
        return blocks.containsKey(blockIndex);
    }

    public int getLatency(int address) {
        return isHit(address) ? hitLatency : missPenalty;
    }

    // Add methods for loading blocks, etc.

    @Override
    public String toString() {
        return "Cache size:" + size + " blockSize:" + blockSize;
    }
}

class CacheBlock {
    int tag;
    byte[] data;
}