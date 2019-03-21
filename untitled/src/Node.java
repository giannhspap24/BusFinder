import java.util.ArrayList;
import java.util.Scanner;

public class Node
{
    public ArrayList<Broker> brokers;

    public void init(int brokerNum)
    {
        Scanner scanner = new Scanner("DS_project_dataset/busLines.txt");
        brokers = new ArrayList<>();
        for(int i = 0; i < brokerNum; i++)
        {
            Broker b = new Broker();
            brokers.add(b);
        }
    }

    public void connect()
    {

    }

    public void disconnect()
    {

    }

    public void updateNodes()
    {

    }
}
