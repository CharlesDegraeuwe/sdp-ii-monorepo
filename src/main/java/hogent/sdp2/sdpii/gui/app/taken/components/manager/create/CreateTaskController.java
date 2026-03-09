package hogent.sdp2.sdpii.gui.app.taken.components.manager.create;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CreateTaskController extends BorderPane {
    public CreateTaskController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/manager/create/CreateTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
