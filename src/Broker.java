import javax.rmi.CORBA.Util;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Broker extends Node
{
    public String IP;
    public int ipHash, id, port;
    public ArrayList<Subscriber> registeredSubs;
    public ArrayList<Publisher> registeredPubs;
    public ArrayList<BusLine> myTopics;


    private boolean gotTopics = false;

    //Normal Constructor
    public Broker(String IP, int port, String id)
    {
        ipHash = Utils.getMd5(IP + port);
        this.IP = IP;
        this.port = port;
        this.id =Integer.parseInt(id);
        registeredSubs = new ArrayList<>();
        myTopics = new ArrayList<>();

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
            Utils.sendPacket(this, broker1_ip, this.port, "add_me");
        }
        String ans = "n";
        do
        {
            if(!gotTopics)
            {
                System.out.print("Start sharing topics? (y)\n> ");
                ans = in.nextLine();
            }
        }while(!ans.toLowerCase().equals("y"));
        gotTopics = false;
        if(ans.toLowerCase().equals("y"))
        {
            brokers = Utils.getTopicList(brokers);
            for (Broker bl : brokers)
            {
                Utils.sendPacket(bl.myTopics, bl.IP, bl.port, "add_topics_list");
                Utils.sendPacket(brokers, bl.IP, bl.port, "add_list");
            }
        }
    }

    //Copy Constructor
    public Broker(Broker b)
    {
        super(true);
        this.ipHash = b.ipHash;
        this.IP = b.IP;
        this.port = b.port;
        this.id = b.id;
        this.registeredSubs = b.registeredSubs;
        this.registeredPubs = b.registeredPubs;
        this.myTopics = b.myTopics;
    }

    public boolean containsTopic(String topic)
    {
        if (myTopics.size() == 0) return false;
        for (BusLine top : myTopics) {
            if (top.lineID.equals(topic)) return true;
        }
        return false;
    }

    public boolean containsLineCode(String code)
    {
        if (myTopics.size() == 0) return false;
        for (BusLine top : myTopics) {
            if (top.lineCode.equals(code)) return true;
        }
        return false;
    }

    public static void main(String[] args)
    {
        new Broker(Utils.getSystemIP(), 8080, args[0]);
    }

    //Parallel Server
    public void run()
    {
        ServerSocket providerSocket = null;
        try
        {
            providerSocket = new ServerSocket(port);
            while (true)
            {
                Socket requestSocket = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());

                String sendtext = in.readUTF();
                if (sendtext.equals("add_me"))
                {
                    System.out.println("add me");
                    Broker b2 = (Broker) in.readObject();
                    if(!contains_broker(b2))
                    {
                        for(int i = 0; i < brokers.size(); i++)
                        {
                            if(b2.ipHash < brokers.get(i).ipHash)
                            {
                                brokers.add(i, b2);
                                break;
                            }
                            else if(i == brokers.size()-1)
                            {
                                brokers.add(b2);
                                break;
                            }
                        }
                        for(Broker test: brokers)
                        {
                            System.out.println(test.IP);
                        }
                        for (Broker b : brokers)
                        {
                            if (b.IP != this.IP)
                            {
                                Utils.sendPacket(brokers, b.IP, b.port, "add_list");
                            }
                        }
                    }
                    else{
                        System.err.println("Brokers already exists");
                    }
                }
                else if (sendtext.equals("add_list"))
                {
                    ArrayList<Broker> bl = (ArrayList<Broker>) in.readObject();
                    brokers = bl;
                    for (Broker b : brokers)
                    {
                        System.out.println(b.IP);
                    }
                }
                else if (sendtext.equals("i_want_bus"))
                {
                    String topic =  in.readUTF();
                    Subscriber s = (Subscriber) in.readObject();

                    if (containsTopic(topic))
                    {
                        if(registeredSubs.isEmpty() || !registeredSubs.contains(s))
                        {
                            registeredSubs.add(s);
                        }
//                        for (Subscriber rb : registeredSubs) {
//                            System.out.println(rb.subscriberID);
//                        }
                        out.reset();
                        out.writeUTF("bus_is_here");
                        out.flush();

                        for (BusLine busL : myTopics) {
                            if (busL.lineID.equals(topic)) {
                                out.reset();
                                out.writeUnshared(busL.runningBuses);
                                System.out.println("Sent " + busL.toString() + "Subcriber " + s.subscriberID);
                            }
                        }
                    }
                    else
                    {
                        out.reset();
                        out.writeUTF("bus_not_here");
                        out.flush();

                        out.reset();
                        out.writeUnshared(brokers);
                        out.flush();

                    }
                }
                else if(sendtext.equals("add_topics_list"))
                {
                    System.out.println("Got Topics");
                    gotTopics = true;
                    myTopics = (ArrayList<BusLine>) in.readObject();
                    for(BusLine s : myTopics)
                    {
                        System.out.println(s.lineID);
                    }
                }
                else if(sendtext.equals("give_me_brokers_list"))
                {
                    out.reset();
                    out.writeUnshared(brokers);
                    out.flush();
                    out.flush();
                }
                else if(sendtext.equals("update_times"))
                {
                    Bus leoforeio = (Bus) in.readObject();
                    for (BusLine busL : myTopics) {
                        if (busL.lineCode.equals(leoforeio.LineCode)) {
                            busL.updateBus(leoforeio);

                        }
                    }

                }
                else if(sendtext.equals("broker_down"))
                {
                    Broker b = (Broker) in.readObject();

                    for (int i = 0; i < brokers.size(); i++)
                    {
                        if (brokers.get(i).id == b.id)
                        {
                            System.out.println("Broker " + b.id + " is down!");
                            brokers.remove(i);
                            break;
                        }
                    }
                    brokers = Utils.getTopicList(brokers);
                    for(Broker br: brokers)
                    {
                        System.out.println(br.id + " " + br.IP);
                        Utils.sendPacket(brokers, br.IP, br.port, "add_list");
                        Utils.sendPacket(br.myTopics, br.IP, br.port, "add_topics_list");
                    }
                    out.reset();
                    out.writeUnshared(brokers);
                    out.flush();
                }
                in.close();
                out.close();
                requestSocket.close();
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

    public void pull(int topic)
    {


    }

    public boolean contains_broker(Broker bin){
        for(Broker b:brokers)
        {
            if(b.IP.equals(bin.IP))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Broker{" +
                "IP='" + IP + '\'' +
                ", id=" + id +
                ", port=" + port +
                ", ipHash=" + ipHash +
                '}';
    }
}
