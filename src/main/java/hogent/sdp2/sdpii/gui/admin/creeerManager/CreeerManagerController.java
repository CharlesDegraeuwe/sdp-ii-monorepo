package hogent.sdp2.sdpii.gui.admin.creeerManager;

import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.admin.RegisterManagerForm;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CreeerManagerController extends BorderPane {
    private RegisterManagerForm form;

    public CreeerManagerController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/admin/create_manager/CreateManager.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        form = new RegisterManagerForm();
        setCenter(form);
    }
}
