import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Broker extends Node
{
    public String IP;
    public int id, port, ipHash;
    public ArrayList<Subscriber> registeredSubs;
    public ArrayList<Publisher> registeredPubs;
    public ArrayList<int[]> myTopics;

    private boolean gotTopics = false;

    //Normal Constructor
    public Broker(String IP, int port, String id)
    {
        this.ipHash = Utils.getMd5(IP + port);
        this.IP = IP;
        this.port = port;
        this.id =Integer.parseInt(id);
        this.brokers = new ArrayList<>();
        this.registeredSubs = new ArrayList<>();
        this.myTopics = new ArrayList<>();

        Scanner in = new Scanner(System.in);
        brokers.add(this);
        new Broker(this).start();
        if (this.id == 1)
        {
            System.out.println("You are initializing the app for the first time.");
            System.out.println("Use this ip to help other brokers connect: " + Utils.getSystemIP());

//            sendLines();
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
            if(!gotTopics)
            {
                System.out.print("Start sharing topics? (y)\n> ");
                ans = in.nextLine();
            }
        }while(!ans.toLowerCase().equals("y"));
        gotTopics = false;
        if(ans.toLowerCase().equals("y"))
        {
            ArrayList<Broker> topics = Utils.getTopicList(brokers);
            for (Broker bl : topics)
            {
                updateNode(bl.myTopics, bl.IP, bl.port, "add_topics_list");
            }
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

    public boolean containsTopic(int topic) {

        if (myTopics.size() == 0) return false;

        for (int[] top : myTopics) {
            if (top[1] == topic) return true;
        }

        return false;
    }

    public static void main(String[] args)
    {
        new Broker(Utils.getSystemIP(), 8080, args[0]);
    }

    public void sendLines()
    {
        ArrayList<BusLine> busLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("busLinesNew.txt")))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");
                busLines.add(new BusLine(values[1]));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner in = new Scanner(System.in);

        ArrayList<String> publishers_count = new ArrayList<>();

        System.out.print("Enter number of publishers\n> ");
        int c = in.nextInt();

        for (int i=0; i<c; i++) {
            System.out.print("Enter publisher " + (i+1)+ " IP\n> ");
            String inpt = in.nextLine();
            publishers_count.add(inpt);
        }

        int counter=0;
        int step = busLines.size()/c;
        for (String _ip : publishers_count) {

            ArrayList<BusLine> sendlist;
            if (counter == c-1)
            {
                sendlist = (ArrayList<BusLine>) busLines.subList(counter * step, busLines.size()-1);
            }
            else {
                sendlist = (ArrayList<BusLine>) busLines.subList(counter * step, (counter+1)*step-1);
            }

            updateNode(sendlist, _ip, 8080, "add_lines");
            counter++;
        }




























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
                    for(int i = 0; i < brokers.size(); i++)
                    {
                        if(b2.ipHash < brokers.get(i).ipHash)
                        {
                            brokers.add(i, b2);
                            break;
                        }
                    }
                    for(Broker test: brokers)
                    {
                        System.out.println(test.ipHash);
                    }
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
                    int topic =  in.readInt();
                    Subscriber s = (Subscriber) in.readObject();

                    if (true)//containsTopic(topic))
                    {
                        if(registeredSubs.isEmpty() || !registeredSubs.contains(s))
                        {
                            registeredSubs.add(s);
                        }
//
//                        for (Subscriber rb : registeredSubs) {
//                            System.out.println(rb.subscriberID);
//                        }
                        out.reset();
                        out.writeUTF("bus_is_here");
                        out.flush();
                        pull(topic);
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
                    myTopics = (ArrayList<int[]>) in.readObject();
                    for(int[] s : myTopics)
                    {
                        System.out.println(s[0]);
                    }
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

    public  static void pull(int topic)
    {


    }

    private void updateNode(Object b, String broker_ip, int broker_port, String text)
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
