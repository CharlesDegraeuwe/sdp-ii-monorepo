package hogent.sdp2.sdpii.gui.app.planning;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class PlanningController extends BorderPane {
    public PlanningController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/PlanningPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTop(new PageTitleController("Planning"));
    }
}
