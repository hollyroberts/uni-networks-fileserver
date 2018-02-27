import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int PORT_NUMBER = 1100;

    private void run() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);

        while(true){
            Socket clientSocket = null;

            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }

            new Thread(new ServerConnection(clientSocket)).start();

        }
    }

    public static void main(String[] args) throws Exception {
        new Server().run();
    }
}
