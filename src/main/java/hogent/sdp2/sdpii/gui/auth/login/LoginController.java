package hogent.sdp2.sdpii.gui.auth.login;

import hogent.sdp2.sdpii.gui.components.app.header.StageHeaderController;
import hogent.sdp2.sdpii.gui.components.auth.LoginFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController extends BorderPane {
    public LoginController(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/auth/Login.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        LoginFormController form = new LoginFormController();
        StageHeaderController controls = new StageHeaderController(stage);
        setCenter(form);
        setTop(controls);
    }

}
