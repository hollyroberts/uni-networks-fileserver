import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**

 */
public class ServerConnection implements Runnable{
    private static int MILLIS_TO_WAIT = 1;

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
                waitForInput(input, 1);

                String operation = input.readUTF();
                System.out.println(operation);

                switch (operation) {
                    case "UPLD":
                        upload(input, output);
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
        } catch (UploadException e) {
            log(e.getMessage());
            if (e.needsCleanup()) {
                log("Deleting temporary file");
            } else {
                log("No data to cleanup");
            }
        }
    }

    private void upload(DataInputStream in, DataOutputStream out) throws IOException, InterruptedException, UploadException {
        log("Client is requesting to upload a file");

        // Get length of filename
        short fileNameLen = in.readShort();
        if (fileNameLen < 1) {
            throw new UploadException("Length of filename to upload is less than 0!", false);
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
            throw new UploadException("File size is less than 0 (" + fileSize + ")", true);
        }

        log("Filesize: " + fileSize);
    }

    private void waitForInput(DataInputStream stream, int numBytes) throws InterruptedException, IOException {
        while (true) {
            if (stream.available() >= numBytes) {
                return;
            } else {
                Thread.sleep(MILLIS_TO_WAIT);
            }
        }
    }

    private void log(String msg) {
        System.out.println("[Connection " + id + "] " + msg);
    }

    class UploadException extends Exception {
        private boolean tempFile;

        UploadException(String message, boolean tempFile) {
            super(message);
            this.tempFile = tempFile;
        }

        public boolean needsCleanup() {
            return tempFile;
        }
    }
}

