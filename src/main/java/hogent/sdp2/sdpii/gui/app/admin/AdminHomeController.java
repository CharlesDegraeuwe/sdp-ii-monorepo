package hogent.sdp2.sdpii.gui.app.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdminHomeController extends GridPane {
    public AdminHomeController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/AdminDashboard.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}