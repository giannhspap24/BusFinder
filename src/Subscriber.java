import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Subscriber extends Node
{
    public String subscriberID;

    public Subscriber(String subscriberID)
    {
        this.subscriberID = subscriberID;
    }

    public static void register(String ip, String port, String topic, Subscriber s) {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String broker_ip = "";
        try
        {
            requestSocket = new Socket(InetAddress.getByName(ip),Integer.parseInt(port));
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("i_want_bus");
            out.flush();

            out.reset();
            out.writeUTF(topic);
            out.flush();

            String returned = in.readUTF();

            if (returned.equals("bus_is_here"))
            {
                System.out.println("This broker has your data.");
                out.reset();
                out.writeUnshared(s);

            }
            else if (returned.equals("bus_is_not_here"))
            {
                broker_ip = in.readUTF();
                System.out.println(broker_ip + " has your data.");
            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        register(broker_ip, port,topic,s);

    }





    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter bus line\n> ");
        String busLine = in.nextLine();

        System.out.println("Enter broker's IP\n> ");
        String brokerIP = in.nextLine();

        System.out.println("Enter broker's port\n> ");
        String brokerPort = in.nextLine();


        Subscriber s = new Subscriber(args[0]);
        register(brokerIP,brokerPort,busLine, s);



    }
}
