package simulator;

import java.util.*;

public class CustomTomasuloSimulator extends TomasuloSimulator {
    private SimulatorGUI gui;
    private int updateFrequency;

    public CustomTomasuloSimulator(int numLoad, int numStore, int numAdd, int numMult,
                                  Map<String, Integer> latencies, int memSize, int blockSize,
                                  int cacheSize, int updateFreq, SimulatorGUI gui) {
        super(numLoad, numStore, numAdd, numMult, latencies, cacheSize, blockSize, 
              1, 10); // hitLatency=1, missPenalty=10
        this.gui = gui;
        this.updateFrequency = updateFreq;
    }
}
