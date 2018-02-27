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
                waitForInput(input, 1);

                String operation = input.readUTF();
                System.out.println(operation);

                switch (operation) {
                    case "UPLD":
                        upload(input, output);
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

            output.close();
            input.close();
            clientSocket.close();

            long time = System.currentTimeMillis() - startTime;
            System.out.println("Request processed: " + time + "ms");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void upload(DataInputStream in, DataOutputStream out) throws IOException, InterruptedException {
        short fileNameLen = in.readShort();
        // TODO fileNameLen < 1
        char[] fileNameChar = new char[fileNameLen];

        waitForInput(in, fileNameLen * 2);
        for (int i = 0; i < fileNameLen; i++) {
            fileNameChar[i] = in.readChar();
        }

        String fileName = new String(fileNameChar);
        System.out.println("Filename: " + fileName);
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