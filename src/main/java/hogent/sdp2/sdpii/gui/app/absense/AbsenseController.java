package hogent.sdp2.sdpii.gui.app.absense;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class AbsenseController extends GridPane {
    public AbsenseController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/AbsensePage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
