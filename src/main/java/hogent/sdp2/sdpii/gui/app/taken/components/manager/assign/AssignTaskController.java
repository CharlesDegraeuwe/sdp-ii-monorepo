package hogent.sdp2.sdpii.gui.app.taken.components.manager.assign;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AssignTaskController extends BorderPane {
    public AssignTaskController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/manager/assign/AssignTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
