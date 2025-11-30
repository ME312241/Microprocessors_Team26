package simulator;

public class Instruction {
    public String op;
    public String dest;
    public String src1;
    public String src2;
    public int imm;

    public Instruction(String op, String dest, String src1, String src2, int imm) {
        this.op = op;
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
        this.imm = imm;
    }

    public static Instruction parse(String line) {
        String[] parts = line.split("[ ,]+");
        String op = parts[0];
        if (op.equals("L.D") || op.equals("L.S") || op.equals("S.D") || op.equals("S.S")) {
            String dest = parts[1];
            String[] addr = parts[2].split("\\(");
            int imm = Integer.parseInt(addr[0]);
            String src1 = addr[1].substring(0, addr[1].length() - 1);
            return new Instruction(op, dest, src1, null, imm);
        } else if (op.equals("ADD.D") || op.equals("SUB.D") || op.equals("MUL.D") || op.equals("DIV.D") ||
                op.equals("ADD.S") || op.equals("SUB.S") || op.equals("MUL.S") || op.equals("DIV.S")) {
            return new Instruction(op, parts[1], parts[2], parts[3], 0);
        } else if (op.equals("DADDI")) {
            return new Instruction(op, parts[1], parts[2], null, Integer.parseInt(parts[3]));
        } else if (op.equals("BEQ") || op.equals("BNE")) {
            return new Instruction(op, null, parts[1], parts[2], Integer.parseInt(parts[3]));
        }
        // Add more if needed
        return null;
    }

    @Override
    public String toString() {
        return op + " " + dest + " " + src1 + (src2 != null ? " " + src2 : "") + (imm != 0 ? " " + imm : "");
    }
}