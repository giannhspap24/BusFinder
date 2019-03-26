import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Node extends Thread {


    public ArrayList<Broker> brokers;
    public Node() {

    }

    public void init(int b) {
        int port = 8080;
        String ip = "127.0.0.1";
        brokers = new ArrayList<Broker>();

        ArrayList<String> buses_md5 = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("busLinesNew.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                buses_md5.add(MD5.getMd5(values[1]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i =0; i<b; i++) {
            String port_ip = MD5.getMd5(ip + Integer.toString(port));
            Broker b1 = new Broker(port_ip,ip,port);
            brokers.add(b1);
            b1.start();
            port+=30;
        }
    }
}
