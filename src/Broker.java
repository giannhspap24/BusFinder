import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TopLevelAttribute;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Broker extends Node
{
    public String ipHash, IP;
    public int id, port;
    public ArrayList<Subscriber> registeredSubs;
    public ArrayList<Publisher> registeredPubs;
    public ArrayList<String[]> myTopics;

    //Normal Constructor
    public Broker(String IP, String port, String id)
    {
        this.ipHash = Utils.getMd5(IP + port);
        this.IP = IP;
        this.port = Integer.parseInt(port);
        this.id =Integer.parseInt(id);
        this.brokers = new ArrayList<Broker>();
        this.registeredSubs = new ArrayList<Subscriber>();

        Scanner in = new Scanner(System.in);
        brokers.add(this);
        new Broker(this).start();
        if (this.id == 1)
        {
            System.out.println("You are initializing the app for the first time.");
            System.out.println("Use this ip to help other brokers connect: " + Utils.getSystemIP());
        }
        else
        {
            System.out.print("Enter Broker 1 IP\n> ");
            String broker1_ip = in.nextLine();
            updateNode(this, broker1_ip, 8080, "add_me");
        }
        String ans = "n";
        do
        {
            System.out.print("Start sharing topics? (y)\n> ");
            ans = in.nextLine();
        }while(!ans.equals("y"));
        Utils.getTopicList(brokers);
        for(Broker bl: brokers)
        {
            updateNode(null, bl.IP, bl.port, "add_topics_list");
        }
    }

    //Copy Constructor
    public Broker(Broker b)
    {
        this.ipHash = b.ipHash;
        this.IP = b.IP;
        this.port = b.port;
        this.id = b.id;
        this.brokers = b.brokers;
        this.registeredSubs = b.registeredSubs;
        this.registeredPubs = b.registeredPubs;
    }

    public static void main(String[] args)
    {
        new Broker(Utils.getSystemIP(), "8080", args[0]);

    }

    //Parallel Server
    public void run()
    {
        ServerSocket providerSocket = null;
        Socket connection = null;
        try
        {
            providerSocket = new ServerSocket(port);
            while (true) {
                connection = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                String sendtext = in.readUTF();
                if (sendtext.equals("add_me"))
                {
                    System.out.println("add me");
                    System.out.printf(this.IP);
                    Broker b2 = (Broker) in.readObject();
                    System.out.println(b2.IP);
                    brokers.add(b2);
                    for (Broker b : brokers)
                    {
                        if (b.IP != this.IP)
                        {
                            updateNode(brokers, b.IP, 8080, "add_list");
                        }
                    }
                }
                else if (sendtext.equals("add_list"))
                {
                    System.out.println("add me too");
                    ArrayList<Broker> bl = (ArrayList<Broker>) in.readObject();
                    brokers = bl;
                    for (Broker b : brokers)
                    {
                        System.out.println(b.IP);
                    }
                }
                else if (sendtext.equals("i_want_bus"))
                {
                    String topic = (String) in.readUTF();
                    String topicMD5 = Utils.getMd5(topic);
                    if (topicMD5.compareTo(this.ipHash) < 0)
                    {
                        out.writeUTF("bus_is_here");
                        Subscriber s = (Subscriber) in.readObject();
                        if(registeredPubs.contains(s))
                        {
                            registeredSubs.add((Subscriber) in.readObject());
                        }
                        pull(topic);
                    }
                    else {
                        for (Broker b : brokers)
                        {
                            if (topic.compareTo(b.ipHash) < 0)
                            {
                                out.writeUTF("bus_is_not_here");
                                out.writeUTF(b.IP);
                            }
                        }
                    }
                }
                in.close();
                out.close();
                connection.close();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public  static void pull(String topic)
    {

    }

    private static void updateNode(Object b, String broker_ip, int broker_port, String text)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try
        {
            requestSocket = new Socket(InetAddress.getByName(broker_ip), broker_port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF(text);
            out.flush();

            out.reset();
            out.writeUnshared(b);
            out.flush();

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

    }
}
