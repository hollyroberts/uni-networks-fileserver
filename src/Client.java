import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Client {
    // Connection details
    Socket socket = null;
    DataInputStream in = null;
    DataOutputStream out = null;

    Client(Socket socket, DataInputStream input, DataOutputStream output) {
        this.socket = socket;
        this.in = input;
        this.out = output;
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