package hogent.sdp2.sdpii.gui.components.admin;

import domain.WerknemerService;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RegisterManagerForm extends VBox {
    public RegisterManagerForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/admin/CreateManager.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
