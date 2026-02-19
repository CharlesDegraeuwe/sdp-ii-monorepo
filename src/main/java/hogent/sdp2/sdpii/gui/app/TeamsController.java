package hogent.sdp2.sdpii.gui.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class TeamsController extends GridPane {
    public TeamsController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/TeamsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
