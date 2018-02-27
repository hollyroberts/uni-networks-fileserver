import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private static int PORT_NUMBER = 1100;
    private static String[] COMMANDS = {"DELF", "DWLD", "UPLD", "QUIT"};

    public static void main(String[] args) throws InterruptedException {
        try {
            Socket socket = new Socket("localhost", PORT_NUMBER);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            out.writeUTF("UPLD");
            out.writeShort(12);
            out.writeChars("Hello World!");
            out.writeInt(-500);

            Misc.waitForInput(in, 1);
            boolean status = in.readBoolean();
            if (!status) {
                Misc.waitForInput(in, 1);
                String reason = in.readUTF();
                System.out.println("Server rejected request");
                System.out.println("Reason: " + reason);
            }

            socket.close();
            System.out.println("Socket closed");
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }
}
