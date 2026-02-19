package hogent.sdp2.sdpii.gui.components.auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginFormController extends VBox {
    public LoginFormController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/auth/LoginForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
    }
}

// onAction="#login"