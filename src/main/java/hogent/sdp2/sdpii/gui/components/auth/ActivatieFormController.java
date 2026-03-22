package hogent.sdp2.sdpii.gui.components.auth;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class ActivatieFormController extends VBox {

    @FXML private TextField txtCode;
    @FXML private Label lblBericht;
    @FXML private Button btnActiveer;
    @FXML private Button btnAnnuleer;

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
        String ingevuldeCode = txtCode.getText();

        if (ingevuldeCode == null || ingevuldeCode.isEmpty()) {
            lblBericht.setText("Vul aub een code in.");
            lblBericht.setStyle("-fx-text-fill: red;");
            return;
        }

        btnActiveer.setText("Bezig met activeren...");
        btnActiveer.setDisable(true);

        WerknemerDTO ingelogdeWerknemer = Sessie.getInstance().getIngelogdeWerknemer();

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return Beheerder.getInstance().getAuthFacade().activeerAccount(
                        ingelogdeWerknemer.id(),
                        ingevuldeCode
                );
            }
        };

        task.setOnSucceeded(e -> {
            boolean succes = task.getValue();
            if (succes) {
                lblBericht.setText("Account succesvol geactiveerd!");
                lblBericht.setStyle("-fx-text-fill: green;");

                PauseTransition pauze = new PauseTransition(Duration.seconds(1));
                pauze.setOnFinished(event -> {
                    this.mf.setCenter(new AppController(stage, mf));
                });
                pauze.play();
            } else {
                lblBericht.setText("Ongeldige code. Probeer het opnieuw.");
                lblBericht.setStyle("-fx-text-fill: red;");
                btnActiveer.setText("Activeer Account");
                btnActiveer.setDisable(false);
            }
        });

        task.setOnFailed(e -> {
            lblBericht.setText("Er ging iets mis met de verbinding.");
            lblBericht.setStyle("-fx-text-fill: red;");
            btnActiveer.setText("Activeer Account");
            btnActiveer.setDisable(false);
        });

        new Thread(task).start();
    }
}