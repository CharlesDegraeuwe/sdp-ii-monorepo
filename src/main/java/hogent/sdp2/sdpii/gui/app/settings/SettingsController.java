package hogent.sdp2.sdpii.gui.app.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class SettingsController extends GridPane {
    public SettingsController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/instellingen/SettingsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
