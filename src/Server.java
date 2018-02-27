import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int PORT_NUMBER = 1100;
    public static String BASE_DIR = "files/";

    private void run() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);

        int curID = 1;

        while(true){
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServerConnection(clientSocket, curID)).start();
                curID++;
            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().run();
    }
}
