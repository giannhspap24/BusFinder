import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Publisher extends Node
{
    int id, broker1_port;
    private List<BusLine> lines;
    String broker1_ip;
    Scanner in = new Scanner(System.in);


    public Publisher(int id)
    {

        this.id = id;
        System.out.print("Enter Broker 1 IP\n>");
        broker1_ip = in.nextLine();
        System.out.print("Enter Broker 1 port\n>");
        broker1_port = in.nextInt();
        in.nextLine();


        brokers = getBrokersList(broker1_ip,broker1_port);

        //new Publisher(this).start();

        sendLines();
        sendTimes();

    }

    public Publisher(Publisher p)
    {
        this.id = p.id;

    }

    public static void main(String[] args)
    {
        new Publisher(Integer.parseInt(args[0]));
    }

    //Parallel Server
    public void run()
    {
        ServerSocket providerSocket = null;
        Socket connection = null;
        try
        {
            providerSocket = new ServerSocket(8080);
            while (true)
            {
                connection = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                String sendtext = in.readUTF();
                if (sendtext.equals("add_lines"))
                {
                    lines = (ArrayList<BusLine>) in.readObject();
                    join();
                }

                in.close();
                out.close();
                connection.close();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Server closed");
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void sendLines() {
        ArrayList<BusLine> busLines = Utils.readLinesList("busLinesNew.txt", brokers);

        ArrayList<String> publishers_count = new ArrayList<>();

        System.out.print("Enter number of publishers\n> ");
        int c = in.nextInt();
        in.nextLine();

        int step = busLines.size() / c;
        int idNorm = id - 1;
        if (idNorm == c - 1)
        {
            lines = busLines.subList(idNorm * step, busLines.size());
        }
        else
        {
            lines = busLines.subList(idNorm * step, (idNorm + 1) * step);
        }
    }

    public Broker hashTopic(String busLID) {
        for (BusLine busL: lines)
        {
            if (busLID.equals(busL.lineID))
            {
                for (Broker b : brokers)
                {
                    if (b.containsLineCode(busLID))
                    {
                        return b;
                    }
                }
            }
        }

        return null;
    }

    public void push(Bus leoforeio, Broker b)
    {
        Utils.sendPacket(leoforeio,b.IP,b.port,"update_times");
    }

    public void sendTimes() {
        try (BufferedReader br = new BufferedReader(new FileReader("busPositionsNew.txt"))){
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");
                Broker b = hashTopic(values[0]);

                if (b != null) {
                    Bus tempLine = new Bus(values[0], values[1], values[2], Double.parseDouble(values[3]), Double.parseDouble(values[4]), values[5]);
                    push(tempLine, b);
                    sleep(1000);
                }

            }

        }catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Broker> getBrokersList(String ip,int port)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try
        {
            requestSocket = new Socket(InetAddress.getByName(ip), port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("give_me_brokers_list");
            out.flush();

            return (ArrayList<Broker>) in.readObject();

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

        return null;

    }

}