package hogent.sdp2.sdpii.gui.components.auth;

import domain.Sessie;
import domain.Werknemer;
import domain.WerknemerService;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginFormController extends VBox {
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtWachtwoord;
    @FXML private Label lblFout;
    private MainFrameController mf;
    private Stage stage;
    private final WerknemerService service = new WerknemerService();

    public LoginFormController(MainFrameController mf, Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/auth/LoginForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.mf = mf;
        this.stage = stage;

    }

    @FXML
    private void login(javafx.event.ActionEvent event) {
        String email = txtEmail.getText();
        String wachtwoord = txtWachtwoord.getText();

        Werknemer werknemer = service.zoekOpEmailEnWachtwoord(email, wachtwoord);

        if (werknemer == null) {
            lblFout.setText("Ongeldig email of wachtwoord!");
            return;
        }

        Sessie.setIngelogdeWerknemer(werknemer);
        navigeerNaarActivatie();
    }
    private void navigeerNaarActivatie() {
        this.mf.setCenter(new AppController(stage, mf));
    }
}

//