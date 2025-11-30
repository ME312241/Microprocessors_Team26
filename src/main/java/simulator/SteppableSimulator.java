package simulator;

import java.util.*;

public class SteppableSimulator {
    private TomasuloSimulator simulator;
    private List<String> instructions;
    private boolean initialized = false;
    private boolean finished = false;

    public SteppableSimulator() {
        instructions = new ArrayList<>();
    }

    public void loadInstructions(String filename) throws Exception {
        instructions.clear();
        List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filename));
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                instructions.add(line.trim());
            }
        }
    }

    public void initialize() {
        if (initialized)
            return;

        Map<String, Integer> latencies = new HashMap<>();
        latencies.put("LD", 2);
        latencies.put("SD", 2);
        latencies.put("ADD.D", 2);
        latencies.put("SUB.D", 2);
        latencies.put("MUL.D", 10);
        latencies.put("DIV.D", 40);
        latencies.put("DADDI", 1);
        latencies.put("BEQ", 1);
        latencies.put("BNE", 1);

        simulator = new TomasuloSimulator(2, 2, 3, 2, latencies, 1024, 4, 1, 10);

        for (String instr : instructions) {
            simulator.addInstruction(instr);
        }

        initialized = true;
        finished = false;
    }

    public void stepCycle() {
        if (!initialized) {
            throw new IllegalStateException("Simulator not initialized. Call initialize() first.");
        }

        if (finished) {
            throw new IllegalStateException("Simulation already finished.");
        }

        // Call the internal step method (we'll need to add this to TomasuloSimulator)
        boolean hasMore = simulator.executeCycle();

        if (!hasMore) {
            finished = true;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public TomasuloSimulator getSimulator() {
        return simulator;
    }

    public int getCycle() {
        return simulator != null ? simulator.getCycle() : 0;
    }

    public List<ReservationStation> getReservationStations() {
        return simulator != null ? simulator.getReservationStations() : new ArrayList<>();
    }

    public RegisterFile getRegisterFile() {
        return simulator != null ? simulator.getRegisterFile() : null;
    }

    public List<Instruction> getInstructionQueue() {
        return simulator != null ? simulator.getInstructionQueue() : new ArrayList<>();
    }
}
