import java.io.*;
import java.net.Socket;

public class ServerConnection implements Runnable{
    private Socket clientSocket = null;
    private int id;

    ServerConnection(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.id = id;
    }

    public void run() {
        log("Connected");

        try (DataInputStream input  = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            wait: while (true) {
                Misc.waitForInput(input, 1);

                String operation = input.readUTF();
                System.out.println(operation);

                switch (operation) {
                    case "UPLD":
                        try {
                            upload(input, output);
                        } catch (ClientUploadMetaData e) {
                            uploadError(e, output);
                            log("Terminating connection due to client error");
                            break wait;
                        }
                        break;
                    case "LIST":
                        break;
                    case "DWLD":
                        break;
                    case "DELF":
                        break;
                    case "QUIT":
                        log("QUIT triggered by client");
                        break wait;
                    default:
                        log("Operation unknown: " + operation);
                        log("Terminating connection due to client error");
                        break wait;
                }
            }

            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        log("Disconnected");
    }

    private void upload(DataInputStream in, DataOutputStream out) throws IOException, InterruptedException, ClientUploadMetaData {
        log("Client is requesting to upload a file");

        // Start timer
        long startTime = System.currentTimeMillis();

        // Get length of filename
        short fileNameLen = in.readShort();
        if (fileNameLen < 1) {
            throw new ClientUploadMetaData("Length of filename to upload is less than 0");
        }

        // Wait for filename to be in buffer then read
        char[] fileNameChar = new char[fileNameLen];
        Misc.waitForInput(in, fileNameLen * 2);
        for (int i = 0; i < fileNameLen; i++) {
            fileNameChar[i] = in.readChar();
        }

        String fileName = new String(fileNameChar);
        String fullPath = Server.BASE_DIR + fileName;
        log("Filename: " + fileName);

        // Get filesize
        Misc.waitForInput(in, 4);
        int fileSize = in.readInt();
        if (fileSize < 0) {
            throw new ClientUploadMetaData("File size is less than 0 (" + fileSize + ")");
        }
        log("Filesize: " + fileSize);

        // Receive data from client
        log("Ready to receive data");
        out.writeBoolean(true);

        // Declare our array of bytes
        byte[] bytes = new byte[fileSize];
        int totBytesRead = 0;

        // Read as many bytes as possible until buffer is full
        while (totBytesRead < fileSize) {
            int bytesRead = in.read(bytes, totBytesRead, fileSize - totBytesRead);

            // If end of stream has been reached then wait for more data
            if (bytesRead == -1) {
                Misc.waitForInput(in, 1);
                continue;
            }

            totBytesRead += bytesRead;
        }

        // Write file out
        File outFile = new File(fullPath);
        //noinspection ResultOfMethodCallIgnored
        outFile.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(fullPath)) {
            stream.write(bytes);
        }

        // Gather statistics
        long endTime = System.currentTimeMillis();
        double timeTaken = (endTime - startTime) / 1000;
        String response = String.format("%d bytes transferred in %,.2fs", fileSize, timeTaken);

        log(response);
        out.writeUTF(response);
        log("Upload finished");
    }

    private void uploadError(ClientUploadMetaData e, DataOutputStream out) throws IOException {
        log(e.getMessage());

        log("Sending error message to client");
        out.writeBoolean(false);
        out.writeUTF(e.getMessage());
    }

    private void log(String msg) {
        System.out.println("[Connection " + id + "] " + msg);
    }
}

class ClientUploadMetaData extends Exception {
    ClientUploadMetaData(String message) {
        super(message);
    }
}
