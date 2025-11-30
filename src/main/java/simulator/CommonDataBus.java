package simulator;

public class CommonDataBus {
    public String dest;
    public Object value;

    public void broadcast(String dest, Object value) {
        this.dest = dest;
        this.value = value;
    }
}