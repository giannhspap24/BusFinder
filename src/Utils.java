import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    public static String getMd5(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static ArrayList<ArrayList<String[]>> getTopicList(ArrayList<Broker> bl)
    {
        ArrayList<String[]> buses_md5 = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("busLinesNew.txt")))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");
                buses_md5.add(new String[]{values[0], Utils.getMd5(values[1])});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<String[]>> brokerTopics = new ArrayList<>(bl.size());
        int counter = 0;
        for(Broker b: bl)
        {
            brokerTopics.add(new ArrayList<String[]>());
            for(String[] hash: buses_md5)
            {
                if(hash[1].compareTo(b.ipHash) < 0)
                {
                    brokerTopics.get(counter).add(hash);
                }
            }
            counter++;
        }
        return brokerTopics;
    }
}