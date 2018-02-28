import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection implements Runnable{
    private int id;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    ServerConnection(Socket clientSocket, int id) {
        this.socket = clientSocket;
        this.id = id;
    }

    public void run() {
        log("Client connected");

        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            wait: while (true) {
                String operation;
                try {
                    operation = input.readUTF();
                } catch (SocketTimeoutException e) {
                    continue;
                }

                switch (operation) {
                    case "UPLD":
                        try {
                            upload();
                        } catch (ClientUploadException e) {
                            uploadError(e);
                            log("Terminating connection due to client error");
                            break wait;
                        }
                        break;
                    case "LIST":
                        list();
                        break;
                    case "DWLD":
                        download();
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
            socket.close();
        } catch (IOException | InterruptedException e) {
            log(e.getMessage());
        }

        log("Client disconnected");
    }

    private void list() throws IOException {
        log("Sending listings to client");
        List<String> listings = new ArrayList<>();

        // Get listings by traversing through source directory
        Files.walk(Paths.get(Server.BASE_DIR))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String listing = path.toString().substring(Server.BASE_DIR.length());
                    listings.add(listing);
                });

        // Send listings to client
        output.writeInt(listings.size());
        for (String listing : listings) {
            //out.writeUTF(listing);
        }

        log("Sent listings to client");
    }

    private void upload() throws IOException, InterruptedException, ClientUploadException {
        log("Client is requesting to upload a file");

        // Start timer
        long startTime = System.currentTimeMillis();

        // Get length of filename
        short fileNameLen = input.readShort();
        if (fileNameLen < 1) {
            throw new ClientUploadException("Length of filename to upload is less than 0");
        }

        // Wait for filename to be in buffer then read
        char[] fileNameChar = new char[fileNameLen];
        for (int i = 0; i < fileNameLen; i++) {
            fileNameChar[i] = input.readChar();
        }

        String fileName = new String(fileNameChar);
        String fullPath = Server.BASE_DIR + fileName;
        log("Filename: " + fileName);

        // Get filesize
        int fileSize = input.readInt();
        if (fileSize < 0) {
            throw new ClientUploadException("File size is less than 0 (" + fileSize + ")");
        }
        log("Filesize: " + fileSize);

        // Receive data from client
        log("Ready to receive data");
        output.writeBoolean(true);

        // Declare our array of bytes
        byte[] bytes = new byte[fileSize];
        int totBytesRead = 0;

        // Read as many bytes as possible until buffer is full
        while (totBytesRead < fileSize) {
            int bytesRead = input.read(bytes, totBytesRead, fileSize - totBytesRead);
            totBytesRead += bytesRead;
        }

        // Write file out
        File outFile = new File(fullPath);
        //noinspection ResultOfMethodCallIgnored
        outFile.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(outFile)) {
            stream.write(bytes);
        } catch (IOException e) {
            log("Error writing file to disk");
            log(e.getMessage());
            output.writeUTF("Server error, could not write to disk (" + e.getMessage() + ")");
        }

        // Gather statistics
        long endTime = System.currentTimeMillis();
        double timeTaken = (endTime - startTime) / 1000;
        String response = String.format("%,d bytes transferred in %,.2fs", fileSize, timeTaken);

        log(response);
        output.writeUTF(response);
        log("Upload finished");
    }

    private void uploadError(ClientUploadException e) throws IOException {
        log(e.getMessage());

        log("Sending error message to client");
        output.writeBoolean(false);
        output.writeUTF(e.getMessage());
    }

    private void log(String msg) {
        System.out.println("[Connection " + id + "] " + msg);
    }
}

class ClientUploadException extends Exception {
    ClientUploadException(String message) {
        super(message);
    }
}
