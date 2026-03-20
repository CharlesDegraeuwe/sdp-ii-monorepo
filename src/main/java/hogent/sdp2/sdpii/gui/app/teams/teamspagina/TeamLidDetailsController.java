package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.WerknemerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TeamLidDetailsController extends VBox {
    @FXML Label naam;
    @FXML Label email;
    @FXML Label telefoon;
    @FXML Label rol;

    private WerknemerDTO werknemer;

    public TeamLidDetailsController(WerknemerDTO werknemer) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/teamspagina/TeamLidDetails.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.werknemer = werknemer;
        init();
    }

    private void init() {
        naam.setText(werknemer.voornaam() + " " + werknemer.naam());
        email.setText(werknemer.email());
        telefoon.setText(werknemer.telefoonnummer());
        rol.setText(werknemer.rol());
    }

    public void showAsPopup() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Details - " + werknemer.voornaam());
        popup.setScene(new Scene(this, 400, 300));
        popup.showAndWait();
    }
}