package hogent.sdp2.sdpii.gui.app.teams.userspagina.components;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import domain.facades.WerknemersFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.function.Consumer;

public class UserDetailsController extends VBox {
    @FXML Label naam;
    @FXML Label email;
    @FXML Label telefoon;
    @FXML Label status;
    @FXML Button blockBtn;
    @FXML Label rol;
    @FXML VBox teamsContainer;

    private WerknemerDTO werknemer;
    private WerknemersFacade facade;
    private TeamFacade teamFacade;
    private Runnable onUpdate;
    private Consumer<Integer> onNavigeerNaarTeam;

    public UserDetailsController(WerknemerDTO werknemer, WerknemersFacade facade, Runnable onUpdate, Consumer<Integer> onNavigeerNaarTeam) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/userspagina/UserDetails.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.werknemer = werknemer;
        this.facade = facade;
        this.teamFacade = new TeamFacade();
        this.onUpdate = onUpdate;
        this.onNavigeerNaarTeam = onNavigeerNaarTeam;
        init();
    }

    private void init() {
        naam.setText(werknemer.voornaam() + " " + werknemer.naam());
        email.setText(werknemer.email());
        telefoon.setText(werknemer.telefoonnummer());
        rol.setText(werknemer.rol());
        status.setText(werknemer.status());

        loadTeams();

        if (werknemer.equals(Sessie.getInstance().getIngelogdeWerknemer())) {
            blockBtn.setVisible(false);
        }
        if (werknemer.status().equals("Geblokkeerd")) {
            blockBtn.getStyleClass().add("btn-deblokkeer");
            blockBtn.setText("deblokkeer");
            blockBtn.setOnAction(e -> {
                if (facade.veranderStatus(werknemer.id(), "activeer")) onUpdate.run();
            });
        } else {
            blockBtn.getStyleClass().add("btn-blokkeer");
            blockBtn.setText("blokkeer");
            blockBtn.setOnAction(e -> {
                if (facade.veranderStatus(werknemer.id(), "blokkeer")) onUpdate.run();
            });
        }
    }

    private void loadTeams() {
        teamsContainer.getChildren().clear();
        var teams = teamFacade.getTeamsVanWerknemer(werknemer.id());
        if (teams.isEmpty()) {
            teamsContainer.getChildren().add(new Label("Geen teams"));
        } else {
            for (var team : teams) {
                Label teamLabel = new Label(team.naam());
                teamLabel.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20; -fx-padding: 8 14; -fx-font-size: 13px; -fx-cursor: hand;");
                teamLabel.setOnMouseClicked(e -> onNavigeerNaarTeam.accept(team.id()));
                teamsContainer.getChildren().add(teamLabel);
            }
        }
    }
}