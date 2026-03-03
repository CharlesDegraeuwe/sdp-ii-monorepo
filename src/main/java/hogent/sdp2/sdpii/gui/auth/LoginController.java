package hogent.sdp2.sdpii.gui.auth;

import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.components.app.header.StageHeaderController;
import hogent.sdp2.sdpii.gui.components.auth.LoginFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

public class LoginController extends BorderPane {
    @Getter
    LoginFormController form;

    public LoginController(Stage stage, MainFrameController mf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/auth/Login.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        form = new LoginFormController(mf, stage);
        StageHeaderController controls = new StageHeaderController(stage);
        setCenter(form);
        setTop(controls);

    }
}
