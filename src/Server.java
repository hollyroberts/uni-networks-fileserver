import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static int PORT_NUMBER = 1234;
    public static String BASE_DIR = "server_files/";
    public static int TIMEOUT = 5000;

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

        // Enter timeout
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter socket timeout in ms (Leave empty for default): ");
        String timeoutStr = scanner.nextLine();

        try {
            TIMEOUT = Integer.valueOf(timeoutStr);
            System.out.println(String.format("Set timeout to %,dms", TIMEOUT));
        } catch (NumberFormatException e) {
            System.out.println(String.format("Timeout set to default value of %,dms", TIMEOUT));
        }

        new Server().run();
    }
}
