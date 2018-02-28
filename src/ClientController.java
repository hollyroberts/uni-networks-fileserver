import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientController {
    private static String DEFAULT_IP = "localhost";
    private static int DEFAULT_PORT = 1100;

    // Connection UI
    @FXML private TextField textIP;
    @FXML private TextField textPort;
    @FXML private Button connect;
    @FXML private Button quit;

    // Operations
    @FXML private Button delf;
    @FXML private Button dwld;
    @FXML private Button list;
    @FXML private Button upld;

    // Listview
    @FXML private ListView<String> listView;

    @FXML
    public void initialize() {
        setUIState(false);
        textIP.setText(DEFAULT_IP);
        textPort.setText(String.valueOf(DEFAULT_PORT));
    }

    private void setUIState(boolean connected) {
        textIP.setDisable(connected);
        textPort.setDisable(connected);
        connect.setDisable(connected);

        quit.setDisable(!connected);
        delf.setDisable(!connected);
        dwld.setDisable(!connected);
        list.setDisable(!connected);
        upld.setDisable(!connected);
    }

    @FXML
    private void connect() {
        // Get IP/Port
        String ip = textIP.getText();
        int port = Integer.parseInt(textPort.getText());

        try {
            log("Connecting to server");
            Socket socket = new Socket(ip, port);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            log("Connected");
        } catch (IOException e) {
            log("Error connecting - " + e.getMessage());
        }
    }

    private void log(String msg) {
        listView.getItems().add(msg);
        System.out.println(msg);
    }
}
