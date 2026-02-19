package hogent.sdp2.sdpii.gui.auth.login;

import domain.Sessie;
import domain.Werknemer;
import domain.WerknemerService;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.app.header.StageHeaderController;
import hogent.sdp2.sdpii.gui.components.auth.LoginFormController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController extends BorderPane {


    public LoginController(Stage stage, MainFrameController mf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/auth/Login.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LoginFormController form = new LoginFormController(mf, stage);
        StageHeaderController controls = new StageHeaderController(stage);
        setCenter(form);
        setTop(controls);

    }
}
