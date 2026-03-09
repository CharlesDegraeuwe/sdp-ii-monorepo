package hogent.sdp2.sdpii.gui.app.taken.components.werknemer;

import domain.auth.Sessie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class OwnTaskController extends BorderPane {
    @FXML
    Label titel_taken;

        public OwnTaskController() {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/werknemer/OwnTaskLayout.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Init();
        }

        public void Init() {
            titel_taken.setText(Sessie.getInstance().getIngelogdeWerknemer().voornaam() + "'s taken:");
        }
}
