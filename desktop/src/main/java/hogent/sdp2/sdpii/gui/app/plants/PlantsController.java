package hogent.sdp2.sdpii.gui.app.plants;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class PlantsController extends GridPane {
    public PlantsController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/PlanningPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
