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

            while (true) {
                if (input.available() < 1) {
                    Thread.sleep(10);
                    continue;
                }

                byte operation = input.readByte();

                switch (operation) {
                    case 1:
                        // UPLD
                        break;
                    case 2:
                        // LIST
                        break;
                    case 3:
                        // DWLD
                        break;
                    case 4:
                        // DELF
                        break;
                    case 5:
                        // QUIT
                        break;
                    default:
                        break;
                }
            }
            
            process(input, output);

            output.close();
            input.close();
            clientSocket.close();

            long time = System.currentTimeMillis() - startTime;
            System.out.println("Request processed: " + time + "ms");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void process(DataInputStream in, DataOutputStream out) throws IOException {
        in.readB
    }
}