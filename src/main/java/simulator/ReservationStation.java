package simulator;

public class ReservationStation {
    public String name;
    public String type;
    public boolean busy;
    public String op;
    public String dest;
    public Object vj, vk;
    public String qj, qk;
    public int cyclesLeft;
    public boolean ready;
    public int address; // for load/store
    public int imm; // for immediate values
    private Memory memory;

    public ReservationStation(String name, String type, int latency, Memory memory) {
        this.name = name;
        this.type = type;
        this.cyclesLeft = latency;
        this.memory = memory;
    }

    public void issue(Instruction inst, RegisterFile rf) {
        busy = true;
        op = inst.op;
        dest = inst.dest;
        imm = inst.imm;
        if (type.equals("Load") || type.equals("Store")) {
            // address = imm + src1
            int base = inst.src1 != null && rf.hasValue(inst.src1) ? (Integer) rf.getValue(inst.src1) : 0;
            address = inst.imm + base;
        }
        // Set operands
        if (inst.src1 != null && !type.equals("Load")) {
            if (rf.hasValue(inst.src1)) {
                vj = rf.getValue(inst.src1);
            } else {
                qj = rf.getTag(inst.src1);
            }
        }
        if (inst.src2 != null) {
            if (rf.hasValue(inst.src2)) {
                vk = rf.getValue(inst.src2);
            } else {
                qk = rf.getTag(inst.src2);
            }
        }
        if (!type.equals("Store")) {
            rf.setTag(inst.dest, name); // Tag register
        }
    }

    public void execute() {
        if (busy && qj == null && qk == null && cyclesLeft > 0) {
            cyclesLeft--;
            if (cyclesLeft == 0)
                ready = true;
        }
    }

    public boolean isReadyToWrite() {
        return ready;
    }

    public void writeResult(CommonDataBus cdb, RegisterFile rf, Iterable<ReservationStation> stations) {
        // For stores, write to memory
        if (op != null && op.startsWith("S.")) {
            memory.store(address, vj);
        } else if (dest != null) {
            // Broadcast result
            Object result = computeResult();
            cdb.broadcast(dest, result);
            rf.setValue(dest, result);
            // Clear tags
            for (ReservationStation rs : stations) {
                if (rs.qj != null && rs.qj.equals(this.name)) {
                    rs.vj = result;
                    rs.qj = null;
                }
                if (rs.qk != null && rs.qk.equals(this.name)) {
                    rs.vk = result;
                    rs.qk = null;
                }
            }
        }
        // For branches, perhaps print if taken
        if (op != null && (op.equals("BEQ") || op.equals("BNE"))) {
            boolean taken = (Boolean) computeResult();
            System.out.println("Branch " + op + " taken: " + taken);
        }
        reset();
    }

    private Object computeResult() {
        // Simple computation, assume double for FP
        if (op.equals("ADD.D"))
            return (Double) vj + (Double) vk;
        if (op.equals("SUB.D"))
            return (Double) vj - (Double) vk;
        if (op.equals("MUL.D"))
            return (Double) vj * (Double) vk;
        if (op.equals("DIV.D"))
            return (Double) vj / (Double) vk;
        if (op.equals("DADDI"))
            return (Integer) vj + imm;
        if (op.startsWith("L."))
            return memory.load(address);
        if (op.startsWith("S."))
            return null; // stores don't compute result
        if (op.equals("BEQ"))
            return ((Integer) vj).equals((Integer) vk);
        if (op.equals("BNE"))
            return !((Integer) vj).equals((Integer) vk);
        return 0.0;
    }

    private void reset() {
        busy = false;
        op = null;
        dest = null;
        vj = vk = null;
        qj = qk = null;
        cyclesLeft = 0;
        ready = false;
        address = 0;
        imm = 0;
    }

    public boolean isBusy() {
        return busy;
    }

    @Override
    public String toString() {
        return "Busy:" + busy + " Op:" + op + " Dest:" + dest + " Vj:" + vj + " Vk:" + vk + " Qj:" + qj + " Qk:" + qk
                + " Addr:" + address;
    }
}