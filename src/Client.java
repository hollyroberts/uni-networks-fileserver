import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static int PORT_NUMBER = 1100;
    private static String[] COMMANDS = {"DELF", "DWLD", "UPLD", "QUIT"};

    // Don't worry about closing this, since there is no way to quit the client (without external input)
    private static Scanner reader = new Scanner(System.in);

    // Connection details
    static Socket socket = null;
    static DataInputStream in = null;
    static DataOutputStream out = null;

    public static void main(String[] args) throws InterruptedException {
        boolean connected = false;

        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.print(" > ");
            String input = reader.nextLine();

            if (connected) {
                switch(input.toUpperCase()) {
                    case "UPLD":
                        break;
                    case "DELF":
                        break;
                    case "DWLD":
                        break;
                    case "QUIT":
                        break;
                    default:
                        System.out.println("Command not recognised. Available commands: DELF, DWLD, LIST, UPLD, QUIT");
                        break;
                }
            } else {
                if (!input.toUpperCase().equals("CONN")) {
                    System.out.println("Command not recognised. Available commands: CONN");
                } else {
                    connected = connect();
                }
            }
        }
    }

    private static boolean connect() {
        try {
            System.out.println("Connecting to server");
            socket = new Socket("localhost", PORT_NUMBER);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected");

            return true;
        } catch (IOException e) {
            System.out.println("Error connecting - " + e.getMessage());

            return false;
        }
    }

    private void upload() throws IOException, InterruptedException {
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
    }
}
