import java.io.*;
import java.util.ArrayList;

public class Node extends Thread implements Serializable
{
    public ArrayList<Broker> brokers;

    public Node() {
        this.brokers = new ArrayList<>();
    }
}
