import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private static int PORT_NUMBER = 1099;
    private static int[] NUMBERS = {34, 8, 1, 90, 400};

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", PORT_NUMBER);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            for (int number : NUMBERS) {
                out.writeInt(number);
            }
            out.writeInt(-1);

            System.out.println(in.readUTF());

            socket.close();
            System.out.println("Data sent");
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }
}
