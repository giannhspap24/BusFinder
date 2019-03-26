import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Broker extends Node {

    public String ipHash, topicHash, IP;
    int port;

    public ArrayList<Subscriber> registeredSubs;
    public ArrayList<Publisher> registeredPubs;


    public Broker(String ipHash, String IP, int port) {
        this.ipHash = ipHash;
        this.IP = IP;
        this.port = port;
    }

    public void run() {
        ServerSocket providerSocket = null;
        Socket connection = null;
        String message = null;
        try {
            providerSocket = new ServerSocket(port);

            while (true) {
                connection = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                out.writeObject("Connection Successful!");
                out.flush();

                do {
                    try {
                        message = (String) in.readObject();
                        System.out.println(connection.getInetAddress().getHostAddress() + ">" + message);

                    } catch (ClassNotFoundException classnot) {
                        System.err.println("Data received in unknown format");
                    }
                } while (!message.equals("bye"));
                in.close();
                out.close();
                connection.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }



























    }


}
