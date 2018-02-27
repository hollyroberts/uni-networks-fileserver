import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private static int PORT_NUMBER = 1100;
    private static String[] COMMANDS = {"DELF", "DWLD", "UPLD", "QUIT"};

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", PORT_NUMBER);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            out.writeUTF("UPLD");
            out.writeShort(12);
            out.writeChars("Hello World!");

            socket.close();
            System.out.println("Socket closed");
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }
}
