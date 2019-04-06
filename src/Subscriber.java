import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class Subscriber extends Node
{
    public String subscriberID, topic;
    public ObjectOutputStream brokerOut;
    public volatile Broker currentBroker;
    public static volatile boolean stopDownload = false;

    public Subscriber(String subscriberID)
    {
        this.subscriberID = subscriberID;

//        while (!topic.toLowerCase().equals("stop"))
//        {
//            System.out.print("Enter bus line\n> ");
//            topic = in.nextLine();
//        }
//
//        //unregister from all the brokers i am connected to
//        for (Broker b : s.currentRegisteredBrokers)
//        {
//            Utils.sendPacket(s,b.IP,b.port,"remove_me");
//        }
    }

    public void register(String ip, int port, String topic) {
        this.topic = topic;
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try
        {
            requestSocket = new Socket(InetAddress.getByName(ip),port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("i_want_bus");
            out.flush();



            out.reset();
            out.writeUnshared(this);
            out.flush();

            String returned = in.readUTF();
            if (returned.equals("bus_is_here"))
            {
                System.out.println("This broker has your data.");

                currentBroker = (Broker) in.readObject();

                ArrayList<Bus> ret = (ArrayList<Bus>) in.readObject();
                visualizeData(ret);


                while (!stopDownload) {
                    ret = (ArrayList<Bus>) in.readObject();
                    visualizeData(ret);
                }

            }
            else if (returned.equals("bus_not_here"))
            {
                System.out.println("Line not found in broker 1");
                ArrayList<Broker> brokers = (ArrayList<Broker>) in.readObject();

                for(Broker b: brokers)
                {
                    if( b.containsTopic(topic) != null )
                    {
                        System.out.println(b.IP + " has your data.");
                        register(b.IP, b.port,topic);
                    }
                }
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

    public void visualizeData(ArrayList<Bus> ret)
    {
        System.out.println("Bus Line " + topic +" details:");
        for (Bus b : ret)
        {
            System.out.println("|-----------------------|\n" + b.toString());
        }
    }

    public void disconnect (String ip, int port)
    {
        Utils.sendPacket(this,ip,port,"remove_me");
    }

    public static void main(String[] args)
    {
        System.out.println("While information is downloaded, press \"y\" to close the current connection");

        Scanner inpt = new Scanner(System.in);
        System.out.print("Enter broker's IP\n> ");
        String brokerIP = inpt.nextLine();
        System.out.print("Enter broker's port\n> ");
        int brokerPort = inpt.nextInt();
        inpt.nextLine();
        System.out.print("Enter bus line\n> ");
        String topic = inpt.nextLine();
        Subscriber s = new Subscriber(args[0]);

        while ( !topic.equals("stop") )
        {
            s.start();
            s.register(brokerIP, brokerPort, topic);
            System.out.print("Enter bus line\n> ");
            topic = inpt.nextLine();
        }


    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (input.equals("y")) {
            stopDownload = true;
            disconnect(currentBroker.IP,currentBroker.port);
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}