import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

public class Utils
{
    public static String getSystemIP()
    {
        String current_ip = null;
        try(final DatagramSocket socket = new DatagramSocket())
        {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            current_ip = socket.getLocalAddress().getHostAddress();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        return current_ip;
    }

    public static ArrayList<ArrayList<int[]>> getTopicList(ArrayList<Broker> bl)
    {
        ArrayList<int[]> buses_md5 = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("busLinesNew.txt")))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");
                buses_md5.add(new int[]{Integer.parseInt(values[0]), values[1].hashCode() % bl.get(bl.size()-1).ipHash});

                System.out.println(buses_md5.get(buses_md5.size()-1)[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<int[]>> brokerTopics = new ArrayList<>(bl.size());
        int counter = 0;
        for(Broker b: bl)
        {
            brokerTopics.add(new ArrayList<>());
            for(int i = 0; i < buses_md5.size(); i++)
            {
                if(buses_md5.get(i)[1] < b.ipHash)
                {
                    brokerTopics.get(counter).add(buses_md5.get(i));
                    buses_md5.remove(i);
                }
            }
            counter++;
        }
        return brokerTopics;
    }

    public static ArrayList<Broker> getDumpTopic(ArrayList<Broker> brokers) {

        ArrayList<int[]> buses_md5 = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("busLinesNew.txt")))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");

                buses_md5.add(new int[]{Integer.parseInt(values[0]), values[1].hashCode() % brokers.get(brokers.size()-1).ipHash});

                System.out.println(buses_md5.get(buses_md5.size()-1)[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int broker1_hash = brokers.get(0).ipHash;
        int broker2_hash = brokers.get(1).ipHash;


        for (int[] bus : buses_md5) {

            if (bus[1] <= broker1_hash)
            {
                brokers.get(0).myTopics.add(bus);
            }
            else if (bus[1] <= broker2_hash)
            {
                brokers.get(1).myTopics.add(bus);
            }
            else
            {
                brokers.get(2).myTopics.add(bus);
            }

        }



        return brokers;
    }

}