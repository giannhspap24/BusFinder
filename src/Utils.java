import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

public class Utils
{
    public static int getMd5(String input)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32)
            {
                hashtext = "0" + hashtext;
            }
            int md5Dec = Integer.parseInt(hashtext.substring(0, 5), 16);
            return md5Dec;
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

    public static ArrayList<Broker> getTopicList(ArrayList<Broker> bl)
    {
        //Read Lines
        ArrayList<BusLine> buses_md5 = readLinesList("busLinesNew.txt", bl);

        ArrayList<ArrayList<int[]>> brokerTopics = new ArrayList<>(bl.size());
        for(Broker b: bl)
        {
            brokerTopics.add(new ArrayList<>());
            System.out.println(b.ipHash);
            for(int i = 0; i < buses_md5.size(); i++)
            {
                if(buses_md5.get(i).hashCode < b.ipHash)
                {
                    b.myTopics.add(buses_md5.get(i));
                    buses_md5.remove(i);
                    i--;
                }
            }
        }
        return bl;
    }

    public static ArrayList<BusLine> readLinesList(String filename, ArrayList<Broker> brokers)
    {
        ArrayList<BusLine> importedBusLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");

                BusLine tempLine;
                if (!brokers.isEmpty()){
                    tempLine = new BusLine(values[0],values[1],values[2],getMd5(values[1]) % brokers.get(brokers.size()-1).ipHash);

                } else {
                    tempLine = new BusLine(values[1],values[0],values[2],getMd5(values[1]));
                }

                importedBusLines.add(tempLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return importedBusLines;
    }

    public static void sendPacket(Object b, String ip, int port, String text)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try
        {
            requestSocket = new Socket(InetAddress.getByName(ip), port);
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
