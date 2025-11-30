package simulator;

import java.util.*;

public class TomasuloSimulator {
    // Components
    private List<Instruction> instructionQueue;
    private Map<String, ReservationStation> reservationStations;
    private RegisterFile registerFile;
    private Cache cache;
    private Memory memory;
    private int cycle;
    private CommonDataBus cdb;

    // Config
    private Map<String, Integer> latencies;
    private int loadBufferSize, storeBufferSize, addBufferSize, multBufferSize;

    public TomasuloSimulator(int loadSize, int storeSize, int addSize, int multSize,
            Map<String, Integer> latencies, int cacheSize, int blockSize,
            int hitLatency, int missPenalty) {
        this.loadBufferSize = loadSize;
        this.storeBufferSize = storeSize;
        this.addBufferSize = addSize;
        this.multBufferSize = multSize;
        this.latencies = latencies;

        instructionQueue = new ArrayList<>();
        reservationStations = new HashMap<>();
        registerFile = new RegisterFile();
        cache = new Cache(cacheSize, blockSize, hitLatency, missPenalty);
        memory = new Memory();
        cdb = new CommonDataBus();
        cycle = 0;

        initializeReservationStations();
    }

    private void initializeReservationStations() {
        // Load buffers
        for (int i = 1; i <= loadBufferSize; i++) {
            String name = "Load" + i;
            reservationStations.put(name, new ReservationStation(name, "Load", latencies.get("LD"), memory));
        }
        // Store buffers
        for (int i = 1; i <= storeBufferSize; i++) {
            String name = "Store" + i;
            reservationStations.put(name, new ReservationStation(name, "Store", latencies.get("SD"), memory));
        }
        // Add stations
        for (int i = 1; i <= addBufferSize; i++) {
            String name = "Add" + i;
            reservationStations.put(name, new ReservationStation(name, "Add", latencies.get("ADD.D"), memory));
        }
        // Mult stations
        for (int i = 1; i <= multBufferSize; i++) {
            String name = "Mult" + i;
            reservationStations.put(name, new ReservationStation(name, "Mult", latencies.get("MUL.D"), memory));
        }
    }

    public void loadInstructions(String filename) {
        // Parse instructions from file
        // For now, assume instructions are added manually or from list
    }

    public void addInstruction(String instr) {
        Instruction inst = Instruction.parse(instr);
        instructionQueue.add(inst);
    }

    public void simulate() {
        int maxCycles = 10000; // Prevent infinite loop
        while ((!instructionQueue.isEmpty() || hasActiveStations()) && cycle < maxCycles) {
            executeCycle();
        }
        if (cycle >= maxCycles) {
            System.out.println("Simulation stopped after " + maxCycles + " cycles to prevent infinite loop.");
        }
    }

    public boolean executeCycle() {
        final int maxCycles = 10000;

        if (cycle >= maxCycles) {
            return false;
        }

        if (instructionQueue.isEmpty() && !hasActiveStations()) {
            return false;
        }

        cycle++;
        System.out.println("Cycle " + cycle);

        // Write result
        writeResult();

        // Execute
        execute();

        // Issue
        issue();

        // Print state
        printState();

        // Return true if more cycles can be executed
        return (!instructionQueue.isEmpty() || hasActiveStations()) && cycle < maxCycles;
    }

    private void issue() {
        if (instructionQueue.isEmpty())
            return;
        Instruction inst = instructionQueue.get(0);
        String stationType = getStationType(inst.op);
        ReservationStation rs = findFreeStation(stationType);
        if (rs != null) {
            rs.issue(inst, registerFile);
            instructionQueue.remove(0);
        }
    }

    private void execute() {
        for (ReservationStation rs : reservationStations.values()) {
            rs.execute();
        }
    }

    private void writeResult() {
        List<ReservationStation> readyStations = new ArrayList<>();
        for (ReservationStation rs : reservationStations.values()) {
            if (rs.isReadyToWrite()) {
                readyStations.add(rs);
            }
        }
        // Handle bus conflict: for simplicity, write one at a time
        if (!readyStations.isEmpty()) {
            ReservationStation rs = readyStations.get(0); // or random
            rs.writeResult(cdb, registerFile, reservationStations.values());
        }
    }

    private boolean hasActiveStations() {
        for (ReservationStation rs : reservationStations.values()) {
            if (rs.isBusy())
                return true;
        }
        return false;
    }

    private String getStationType(String op) {
        if (op.equals("LD") || op.equals("LW") || op.equals("L.S") || op.equals("L.D"))
            return "Load";
        if (op.equals("SD") || op.equals("SW") || op.equals("S.S") || op.equals("S.D"))
            return "Store";
        if (op.equals("ADD.D") || op.equals("ADD.S") || op.equals("SUB.D") || op.equals("SUB.S") || op.equals("DADDI")
                || op.equals("DSUBI") || op.equals("BEQ") || op.equals("BNE"))
            return "Add";
        if (op.equals("MUL.D") || op.equals("MUL.S") || op.equals("DIV.D") || op.equals("DIV.S"))
            return "Mult";
        return null;
    }

    private ReservationStation findFreeStation(String type) {
        for (ReservationStation rs : reservationStations.values()) {
            if (rs.type.equals(type) && !rs.isBusy())
                return rs;
        }
        return null;
    }

    // Getter methods for GUI access
    public List<ReservationStation> getReservationStations() {
        return new ArrayList<>(reservationStations.values());
    }

    public RegisterFile getRegisterFile() {
        return registerFile;
    }

    public List<Instruction> getInstructionQueue() {
        return new ArrayList<>(instructionQueue);
    }

    public int getCycle() {
        return cycle;
    }

    private void printState() {
        System.out.println("Instruction Queue: " + instructionQueue.size() + " instructions");
        System.out.println("Reservation Stations:");
        for (Map.Entry<String, ReservationStation> entry : reservationStations.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Register File: " + registerFile);
        System.out.println("Cache: " + cache);
        System.out.println();
    }

    // Main method for testing
    public static void main(String[] args) {
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

        TomasuloSimulator sim = new TomasuloSimulator(2, 2, 3, 2, latencies, 1024, 4, 1, 10);

        // Determine which file to load
        String filename = "instructions.txt"; // default
        if (args.length > 0) {
            filename = args[0];
        }

        // Load instructions from file
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filename);
            List<String> lines = java.nio.file.Files.readAllLines(path);
            System.out.println("Loaded " + lines.size() + " lines from " + filename);
            for (String line : lines) {
                if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                    sim.addInstruction(line.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading instructions from " + filename + ": " + e.getMessage());
            System.exit(1);
        }
        sim.simulate();
    }
}