import java.io.*;
import java.net.Socket;

/**

 */
public class ServerConnection implements Runnable{
    private Socket clientSocket = null;
    private int id;

    ServerConnection(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.id = id;
    }

    public void run() {
        log("Connected");

        try {
            long startTime = System.currentTimeMillis();

            DataInputStream input  = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            wait: while (true) {
                Misc.waitForInput(input, 1);

                String operation = input.readUTF();
                System.out.println(operation);

                switch (operation) {
                    case "UPLD":
                        try {
                            upload(input, output);
                        } catch (ClientUploadException e) {
                            uploadError(e, output);
                        }
                        break;
                    case "LIST":
                        break;
                    case "DWLD":
                        break;
                    case "DELF":
                        break;
                    case "QUIT":
                        break;
                    default:
                        break wait;
                }
            }

            output.close();
            input.close();
            clientSocket.close();

            long time = System.currentTimeMillis() - startTime;
            log("Request processed: " + time + "ms");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void upload(DataInputStream in, DataOutputStream out) throws IOException, InterruptedException, ClientUploadException {
        log("Client is requesting to upload a file");

        // Get length of filename
        short fileNameLen = in.readShort();
        if (fileNameLen < 1) {
            throw new ClientUploadException("Length of filename to upload is less than 0!", false);
        }

        // Wait for filename to be in buffer then read
        char[] fileNameChar = new char[fileNameLen];
        waitForInput(in, fileNameLen * 2);
        for (int i = 0; i < fileNameLen; i++) {
            fileNameChar[i] = in.readChar();
        }

        String fileName = new String(fileNameChar);
        log("Filename: " + fileName);

        // Get filesize
        waitForInput(in, 4);
        int fileSize = in.readInt();
        if (fileSize < 0) {
            throw new ClientUploadException("File size is less than 0 (" + fileSize + ")", true);
        }
        log("Filesize: " + fileSize);

        log("Ready to receive data");
        out.writeBoolean(true);
    }

    private void uploadError(ClientUploadException e, DataOutputStream out) throws IOException {
        log(e.getMessage());

        if (e.needsCleanup()) {
            log("Deleting temporary file");
        } else {
            log("No data to cleanup");
        }

        log("Sending error message to client");
        out.writeBoolean(false);
        out.writeUTF(e.getMessage());
    }

    private void log(String msg) {
        System.out.println("[Connection " + id + "] " + msg);
    }
}

class ClientUploadException extends Exception {
    private boolean tempFile;

    ClientUploadException(String message, boolean tempFile) {
        super(message);
        this.tempFile = tempFile;
    }

    public boolean needsCleanup() {
        return tempFile;
    }
}
