package hogent.sdp2.sdpii.gui.components.auth;

import hogent.sdp2.sdpii.gui.MainFrameController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ActivatieFormController extends VBox {

    @FXML private TextField txtCode;
    @FXML private Label lblBericht;
    @FXML private Button btnActiveer;

    private MainFrameController mf;
    private Stage stage;

    public ActivatieFormController(Stage stage, MainFrameController mf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/auth/ActivatieForm.fxml"));
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
    private void handleActiveer() {
    }
}
