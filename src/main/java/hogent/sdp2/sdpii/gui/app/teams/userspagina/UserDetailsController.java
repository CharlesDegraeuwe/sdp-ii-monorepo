package hogent.sdp2.sdpii.gui.app.teams.userspagina;

import domain.dto.WerknemerDTO;
import domain.facades.WerknemersFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class UserDetailsController extends VBox {
    @FXML Label naam;
    @FXML Label email;
    @FXML Label telefoon;
    @FXML Label status;
    @FXML Button demoteBtn;
    @FXML Button blockBtn;

    private WerknemerDTO werknemer;
    private WerknemersFacade facade;
    private Runnable onUpdate;

    public UserDetailsController(WerknemerDTO werknemer, WerknemersFacade facade, Runnable onUpdate) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/userspagina/UserDetails.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.werknemer = werknemer;
        this.facade = facade;
        this.onUpdate = onUpdate;
        init();
    }

    private void init() {
        naam.setText(werknemer.voornaam() + " " + werknemer.naam());
        email.setText(werknemer.email());
        telefoon.setText(werknemer.telefoonnummer());
        status.setText(werknemer.status());

        demoteBtn.setOnAction(e -> {
            if (facade.veranderStatus(werknemer.id(), "demote")) {
                onUpdate.run();
            }
        });

        blockBtn.setOnAction(e -> {
            if (facade.veranderStatus(werknemer.id(), "blokkeer")) {
                onUpdate.run();
            }
        });
    }
}