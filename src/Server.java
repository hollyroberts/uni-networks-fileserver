import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int PORT_NUMBER = 1234;
    private static int TIMEOUT = 5000;
    public static String BASE_DIR = "server_files/";

    private void run() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);

        int curID = 1;

        //noinspection InfiniteLoopStatement
        while(true){
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(TIMEOUT);
                new Thread(new ServerConnection(clientSocket, curID)).start();
                curID++;
            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Create base dir if it doesn't exist
        File bd = new File(BASE_DIR);
        if (!bd.exists()) {
            System.out.println("Base directory '" + BASE_DIR + "' does not exist");
            if (bd.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Could not create directory. Errors will probably occur from now on");
            }
        }


        new Server().run();
    }
}
