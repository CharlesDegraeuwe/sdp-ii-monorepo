package hogent.sdp2.sdpii.gui.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class NotificationsController extends GridPane {
    public NotificationsController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/NotificationsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

