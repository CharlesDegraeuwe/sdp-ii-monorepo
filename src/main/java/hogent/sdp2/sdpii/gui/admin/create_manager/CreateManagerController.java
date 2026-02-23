package hogent.sdp2.sdpii.gui.admin.create_manager;

import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.admin.AdminHomeMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CreateManagerController extends BorderPane {
    public CreateManagerController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/admin/create_manager/CreateManager.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
