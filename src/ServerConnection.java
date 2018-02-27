import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**

 */
public class ServerConnection implements Runnable{
    private Socket clientSocket = null;
    private static int MILLIS_TO_WAIT = 1;

    ServerConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            long startTime = System.currentTimeMillis();

            DataInputStream input  = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            wait: while (true) {
                if (input.available() < 1) {
                    Thread.sleep(10);
                    continue;
                }

                String operation = input.readUTF();
                System.out.println(operation);

                switch (operation) {
                    case "UPLD":
                        break;
                    case "LIST":
                        break;
                    case "DWLD":
                        break;
                    case "DELF":
                        break;
                    case "QUIT":
                        break;
                    default:
                        break wait;
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

    private void upload(DataInputStream in, DataOutputStream out) throws IOException {
        short fileNameLen = in.readShort();
        // TODO fileNameLen < 1
        char[] fileName = new char[fileNameLen];

        int charPos = 0;
        while (charPos <= fileNameLen) {


            charPos++;
        }
    }

    private void waitForInput(DataInputStream stream, int numBytes) throws InterruptedException, IOException {
        while (true) {
            if (stream.available() >= numBytes) {
                return;
            } else {
                Thread.sleep(MILLIS_TO_WAIT);
            }
        }
    }
}