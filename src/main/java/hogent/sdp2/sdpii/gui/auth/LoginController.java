package hogent.sdp2.sdpii.gui.auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class LoginController extends BorderPane {
    public void LoginController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/auth/Login.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

    }
}
