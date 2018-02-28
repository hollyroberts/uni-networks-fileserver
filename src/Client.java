import java.io.*;
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

    public boolean download(String filename) {
        long startTime = System.currentTimeMillis();

        // Download bytes from server
        byte[] bytes;
        try {
            bytes = downloadFromServer(filename);
        } catch (IOException e) {
            Log.log(e.getMessage());
            return false;
        }

        // If bytes is null then some error has occurred, but it's not fatal
        if (bytes == null) {
            return true;
        }

        // Save to disk
        // Write file out
        File outFile = new File(filename);
        //noinspection ResultOfMethodCallIgnored
        outFile.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(outFile)) {
            stream.write(bytes);

            // Gather statistics
            long endTime = System.currentTimeMillis();
            double timeTaken = (endTime - startTime) / 1000;
            Log.log(String.format("%,d bytes transferred in %,.2fs", bytes.length, timeTaken));
        } catch (IOException e) {
            Log.log("Error writing file to disk");
            Log.log(e.getMessage());
        }

        return true;
    }

    private byte[] downloadFromServer(String filename) throws IOException {
        Log.log("Sending DWLD operation to server");
        out.writeUTF("DWLD");
        out.writeShort(filename.length());
        out.writeChars(filename);

        int fileSize = in.readInt();
        if (fileSize == -1) {
            Log.log("File does not exist on server");
            return null;
        } else if (fileSize < 0) {
            Log.log("Negative integer returned for filesize that was not -1. Download cancelled");
            return null;
        }

        Log.log("Downloading from server");

        // Declare our array of bytes
        byte[] bytes = new byte[fileSize];
        int totBytesRead = 0;

        // Read as many bytes as possible until buffer is full
        while (totBytesRead < fileSize) {
            int bytesRead = in.read(bytes, totBytesRead, fileSize - totBytesRead);
            totBytesRead += bytesRead;
        }

        return bytes;
    }

    public boolean list() {
        String[] listings;

        try {
            listings = retrieveListings();
        } catch (IOException | InterruptedException e) {
            Log.log(e.getMessage());
            return false;
        }

        // Once listings are retrieved, display to client
        Log.log("Listings:");
        for (String listing : listings) {
            Log.log(listing);
        }

        return true;
    }

    private String[] retrieveListings() throws IOException, InterruptedException {
        Log.log("Retrieving listings");
        out.writeUTF("LIST");

        // Number of listings to retrieve
        int numListings = in.readInt();
        if (numListings <= 0) {
            Log.log("Server contains no listings");
            return new String[0];
        }

        // Retrieve listings
        String[] listings = new String[numListings];
        for (int i = 0; i < numListings; i++) {
            listings[i] = in.readUTF();
        }

        return listings;
    }

    // Attempts to quit gracefully using operations
    public void quit() {
        try {
            out.writeUTF("QUIT");
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            Log.log("Error quitting gracefully (" + e.getMessage() + ")");
        }
        Log.log("Session closed");
    }

    // Returns false if there is a SERVER error
    // Client errors (eg. IOException on file read, will still return true)
    public boolean upload(File file, String filename)  {
        // Read the file as bytes from disk first
        Log.log("Reading file from disk");
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            Log.log(e.getMessage());
            return true;
        }

        // Send the file to the server
        try {
            uploadFile(filename, bytes);
        } catch (IOException e) {
            Log.log(e.getMessage());
            return false;
        }

        return true;
    }

    // The code that performs the upload (wrapped in upload to handle errors)
    private void uploadFile(String filename, byte[] bytes) throws IOException {
        Log.log("Sending UPLD operation to server and waiting for response");
        out.writeUTF("UPLD");
        out.writeShort(filename.length());
        out.writeChars(filename);
        out.writeInt(bytes.length);

        if (!in.readBoolean()) {
            String reason = in.readUTF();
            Log.log("Server rejected request");
            Log.log("Reason: " + reason);
            return;
        }

        Log.log("Sending data to server");
        out.write(bytes);
        Log.log(in.readUTF());
    }

    public static Client connect(String ip, int port, int timeout) {
        try {
            Log.log("Connecting to server");
            Socket socket = new Socket(ip, port);
            socket.setSoTimeout(timeout);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Log.log("Connected");

            return new Client(socket, in, out);
        } catch (IOException e) {
            Log.log(e.getMessage());
        }

        return null;
    }
}