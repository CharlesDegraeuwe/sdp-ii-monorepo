package hogent.sdp2.sdpii.gui.app.notificaties;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class NotificatiesController extends BorderPane {
    public NotificatiesController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/NotificationsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTop(new PageTitleController("Notificaties"));
    }
}

