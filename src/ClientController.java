import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
}
