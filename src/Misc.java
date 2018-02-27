import java.io.DataInputStream;
import java.io.IOException;

public class Misc {
    private static int MILLIS_TO_WAIT = 1;

    public static void waitForInput(DataInputStream stream, int numBytes) throws InterruptedException, IOException {
        while (true) {
            if (stream.available() >= numBytes) {
                return;
            } else {
                Thread.sleep(MILLIS_TO_WAIT);
            }
        }
    }
}
