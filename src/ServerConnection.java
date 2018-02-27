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
        List<Integer> numbers = new ArrayList<Integer>();

        try {
            while (true) {
                int num = in.readInt();

                if (num == -1) { break; }
                numbers.add(num);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Numbers received: " + numbers.toString());

        if (numbers.size() != 5) {
            out.writeUTF("Did not receive 5 numbers");
            return;
        }

        out.writeUTF("Received 5 numbers");
    }
}