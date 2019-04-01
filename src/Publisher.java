import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Publisher extends Node {

    String id;
    private ArrayList<BusLine> lines = new ArrayList<>();

    public Publisher(String id) {
        this.id = id;
    }

    public static void main(String[] args) {
        Publisher p = new Publisher(args[0]);
        p.start();
    }

    //Parallel Server
    public void run()
    {
        ServerSocket providerSocket = null;
        Socket connection = null;
        try
        {
            providerSocket = new ServerSocket(8080);
            while (true) {
                connection = providerSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                String sendtext = in.readUTF();
                if (sendtext.equals("add_lines")) {
                    lines = (ArrayList<BusLine>) in.readObject();
                }
                else if (sendtext.equals("get_positions"))
                {
                    String topic = in.readUTF();

                    //find topic in database
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


}
