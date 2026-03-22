package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeamLedenController extends VBox {
    private int teamID;
    private TeamFacade facade;
    @Getter private List<WerknemerDTO> teamleden;
    private Runnable onRefresh;

    @FXML VBox container;

    public TeamLedenController(int teamId, TeamFacade facade, Runnable onRefresh) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/teamspagina/TeamLeden.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.teamID = teamId;
        this.facade = facade;
        this.onRefresh = onRefresh;
        showLeden();
    }

    private void showLeden() {
        container.getChildren().clear();
        teamleden = facade.getTeamLeden(teamID);
        for (int i = 0; i < teamleden.size(); i++) {
            container.getChildren().add(new TeamLidController(teamleden.get(i), i));
        }
    }

    public void showBeschikbareWerknemers() {
        container.getChildren().clear();

        if (teamleden != null && teamleden.size() >= 4) {
            Label vol = new Label("Team zit vol (max 4 leden)");
            vol.setStyle("-fx-text-fill: #E31B35; -fx-font-weight: bold;");
            container.getChildren().add(vol);
            return;
        }

        List<WerknemerDTO> beschikbaar = facade.getBeschikbareWerknemers(teamID);
        List<WerknemerDTO> geselecteerd = new ArrayList<>();

        VBox lijst = new VBox(8);
        VBox.setVgrow(lijst, Priority.ALWAYS);

        for (WerknemerDTO w : beschikbaar) {
            CheckBox cb = new CheckBox(w.voornaam() + " " + w.naam());
            cb.setStyle("-fx-font-size: 13px;");
            cb.setOnAction(e -> {
                if (cb.isSelected()) geselecteerd.add(w);
                else geselecteerd.remove(w);
            });

            HBox row = new HBox(10, cb);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20; -fx-padding: 10 14;");
            lijst.getChildren().add(row);
        }

        if (beschikbaar.isEmpty()) {
            lijst.getChildren().add(new Label("Geen beschikbare werknemers"));
        }

        Button toevoegBtn = new Button("Toevoegen");
        toevoegBtn.getStyleClass().add("create-btn");
        toevoegBtn.setMaxWidth(Double.MAX_VALUE);
        toevoegBtn.setOnAction(e -> {
            int resterend = 4 - (teamleden != null ? teamleden.size() : 0);
            int count = 0;
            for (WerknemerDTO w : geselecteerd) {
                if (count >= resterend) break;
                facade.voegLidToe(teamID, w.id());
                count++;
            }
            showLeden();
            if (onRefresh != null) onRefresh.run();
        });

        Button annuleerBtn = new Button("Annuleer");
        annuleerBtn.getStyleClass().add("btn-outline");
        annuleerBtn.setMaxWidth(Double.MAX_VALUE);
        annuleerBtn.setOnAction(e -> showLeden());

        VBox buttons = new VBox(8, toevoegBtn, annuleerBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        container.getChildren().addAll(lijst, buttons);
    }
}