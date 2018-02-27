import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**

 */
public class ServerConnection implements Runnable{
    private Socket clientSocket = null;

    ServerConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            long startTime = System.currentTimeMillis();

            DataInputStream input  = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            process(input, output);

            output.close();
            input.close();
            clientSocket.close();

            long time = System.currentTimeMillis() - startTime;
            System.out.println("Request processed: " + time + "ms");
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    private void process(DataInputStream in, DataOutputStream out) throws IOException {
    }
}