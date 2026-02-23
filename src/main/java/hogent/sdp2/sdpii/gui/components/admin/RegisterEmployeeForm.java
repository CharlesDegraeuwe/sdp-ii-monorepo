package hogent.sdp2.sdpii.gui.components.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RegisterEmployeeForm extends VBox {
    public RegisterEmployeeForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/admin/CreateEmployeeForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
