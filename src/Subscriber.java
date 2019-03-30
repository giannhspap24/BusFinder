import sun.plugin2.message.Conversation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
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

    public static void register(String ip, int port, int topic, Subscriber s) {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String broker_ip = "";
        try
        {
            requestSocket = new Socket(InetAddress.getByName(ip),port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("i_want_bus");
            out.flush();

            out.reset();
            out.writeInt(topic);
            out.flush();

            out.reset();
            out.writeUnshared(s);
            out.flush();


            String returned = in.readUTF();

            if (returned.equals("bus_is_here"))
            {
                System.out.println("This broker has your data.");




                System.out.println("Bus position: 0,0");

                return;
            }
            else if (returned.equals("bus_not_here"))
            {

                ArrayList<Broker> brokers = (ArrayList<Broker>) in.readObject();

                for(Broker b: brokers)
                {
                    System.out.println("reached");
                    if( b.containsTopic(topic) ) {
                        System.out.println(b.IP + " has your data.");
                        register(b.IP, b.port,topic,s);
                        return;
                    }
                }
                System.out.println("Line not found");




                return;
            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }





    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter bus line\n> ");
        int busLine = in.nextLine().hashCode();

        System.out.print("Enter broker's IP\n> ");
        String brokerIP = in.nextLine();

        System.out.print("Enter broker's port\n> ");
        int brokerPort = in.nextInt();


        Subscriber s = new Subscriber(args[0]);
        register(brokerIP,brokerPort,busLine, s);



    }
}
