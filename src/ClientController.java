import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.function.UnaryOperator;

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

    // Formatter to restrict inputs to only numbers
    // https://stackoverflow.com/q/40472668
    private UnaryOperator<TextFormatter.Change> integerFilter = textField -> {
        String input = textField.getText();
        if (input.matches("[0-9]*")) {
            return textField;
        }
        return null;
    };

    // Connection info
    private Client conn = null;

    @FXML
    public void initialize() {
        setUIState(false);
        textIP.setText(DEFAULT_IP);
        textPort.setText(String.valueOf(DEFAULT_PORT));
        textPort.setTextFormatter(new TextFormatter<String>(integerFilter));

        Log.init(listView);
    }

    private void setUIState(boolean connected) {
        disableConnectionGUI(connected);
        disableOperations(!connected);
    }

    private void disableAllUI() {
        disableOperations(true);
        disableConnectionGUI(true);
    }

    private void disableConnectionGUI(boolean disable) {
        textIP.setDisable(disable);
        textPort.setDisable(disable);
        connect.setDisable(disable);
    }

    private void disableOperations(boolean disable) {
        quit.setDisable(disable);
        delf.setDisable(disable);
        dwld.setDisable(disable);
        list.setDisable(disable);
        upld.setDisable(disable);
    }

    @FXML
    private void connect() {
        // Get IP/Port
        String ip = textIP.getText();
        int port = Integer.parseInt(textPort.getText());

        Task<Client> task = new Task<Client>() {
            @Override protected Client call() {
                return Client.connect(ip, port);
            }
        };

        task.setOnSucceeded(event -> {
            conn = task.getValue();
            setUIState(conn != null);
        });

        startTask(task);
    }

    @FXML private void quit() {
    }

    @FXML private void upload() {
        // Get file
        FileChooser fc = new FileChooser();
        fc.setTitle("Select file");
        File file = fc.showOpenDialog(getStage());

        if (file == null) {
            return;
        }

        // Get filename
        TextInputDialog dialog = new TextInputDialog(file.getName());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setHeaderText("Enter filename");
        dialog.setContentText("Filename to save on server:");
        dialog.initOwner(getStage());
        dialog.initModality(Modality.WINDOW_MODAL);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }

        Task<Boolean> task = new Task<Boolean>() {
            @Override protected Boolean call() {
                return conn.upload(file, result.get());
            }
        };

        task.setOnSucceeded(event -> {
            setUIState(conn != null);
        });
        startTask(task);
    }

    private Stage getStage() {
        return (Stage) listView.getScene().getWindow();
    }

    private void startTask(Task task) {
        disableAllUI();
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }
}

class Log {
    private static ListView<String> list;

    public static void init(ListView list) {
        Log.list = list;
    }

    public static void log(String msg) {
        System.out.println(msg);
        Platform.runLater(() -> list.getItems().add(msg));
    }
}
