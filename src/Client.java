import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

class Client {
    // Connection details
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Client(Socket socket, DataInputStream input, DataOutputStream output) {
        this.socket = socket;
        this.in = input;
        this.out = output;
    }

    public boolean upload(File file, String filename) throws IOException, InterruptedException {
        Log.log("Reading file from disk");
        byte[] bytes = Files.readAllBytes(file.toPath());

        Log.log("Sending UPLD operation to server");
        out.writeUTF("UPLD");
        out.writeShort(filename.length());
        out.writeChars(filename);
        out.writeInt(bytes.length);

        Misc.waitForInput(in, 1);
        if (!in.readBoolean()) {
            Misc.waitForInput(in, 1);
            String reason = in.readUTF();
            Log.log("Server rejected request");
            Log.log("Reason: " + reason);
            return false;
        }

        Log.log("Sending data to server");

        return true;
    }

    public static Client connect(String ip, int port) {
        try {
            Log.log("Connecting to server");
            Socket socket = new Socket(ip, port);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Log.log("Connected");

            return new Client(socket, in, out);
        } catch (IOException e) {
            Log.log("Error connecting - " + e.getMessage());
        }

        return null;
    }
}