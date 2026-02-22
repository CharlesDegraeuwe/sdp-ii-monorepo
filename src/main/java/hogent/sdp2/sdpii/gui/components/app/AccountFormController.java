package hogent.sdp2.sdpii.gui.components.app;

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

public class AccountFormController extends VBox {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtWachtwoord;
    @FXML private Label lblFout;
    private AppController app;
    private Stage stage;
    private final WerknemerService service = new WerknemerService();

    public AccountFormController(AppController app, Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/AccountForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.app = app;
        this.stage = stage;

    }

}
