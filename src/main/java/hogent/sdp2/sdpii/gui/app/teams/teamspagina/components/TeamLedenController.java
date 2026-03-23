package hogent.sdp2.sdpii.gui.app.teams.teamspagina.components;

import domain.dto.TeamDTO;
import domain.dto.TeamLidDTO;
import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
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
import java.util.function.Consumer;

public class TeamLedenController extends VBox {
    private int teamID;
    private TeamFacade facade;
    private TeamDTO team;
    @Getter private List<TeamLidDTO> teamleden;
    private Runnable onRefresh;
    private Consumer<Integer> onNavigeerNaarUser;
    @FXML VBox container;
    @FXML Button deleteBtn;
    @FXML Label naam;
    @FXML Label locatie;
    @FXML Label manager;

    public TeamLedenController(TeamDTO team, TeamFacade facade, Runnable onRefresh, Consumer<Integer> onNavigeerNaarUser) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/teamspagina/TeamLeden.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.team = team;
        this.teamID = team.id();
        this.facade = facade;
        this.onRefresh = onRefresh;
        this.onNavigeerNaarUser = onNavigeerNaarUser;
        this.init();
    }

    private void init() {

        naam.setText(team.naam());
        locatie.setText(team.siteNaam());
        manager.setText(team.managerNaam());
        locatie.setOnMouseClicked(e -> {
            Router.getInstance().navigeerNaar(Scherm.LOCATIES);
        });
        deleteBtn.setOnMouseClicked(e -> {
            facade.verwijderTeam(teamID);
            if (onRefresh != null) onRefresh.run();
        });
        showLeden();
    }
    private void showLeden() {
        container.getChildren().clear();
        teamleden = facade.getTeamLeden(teamID);
        for (int i = 0; i < teamleden.size(); i++) {
            TeamLidController lid = new TeamLidController(
                    teamleden.get(i), i, teamID, facade, this::refresh, onNavigeerNaarUser);
            lid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lid, Priority.ALWAYS);
            container.getChildren().add(lid);
        }
    }

    private void refresh() {
        showLeden();
        if (onRefresh != null) onRefresh.run();
    }




    public void showBeschikbareWerknemers() {
        container.getChildren().clear();

        int huidigAantal = teamleden != null ? teamleden.size() : 0;
        int resterend = 4 - huidigAantal;

        if (resterend <= 0) {
            Label vol = new Label("Team zit vol (max 4 leden)");
            vol.setStyle("-fx-text-fill: #E31B35; -fx-font-weight: bold;");
            container.getChildren().add(vol);
            return;
        }

        List<WerknemerDTO> beschikbaar = facade.getBeschikbareWerknemers(teamID);
        List<WerknemerDTO> geselecteerd = new ArrayList<>();
        List<CheckBox> alleCheckboxes = new ArrayList<>();

        VBox lijst = new VBox(8);
        VBox.setVgrow(lijst, Priority.ALWAYS);

        for (WerknemerDTO w : beschikbaar) {
            CheckBox cb = new CheckBox(w.voornaam() + " " + w.naam());
            cb.setStyle("-fx-font-size: 13px;");
            alleCheckboxes.add(cb);

            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    geselecteerd.add(w);
                } else {
                    geselecteerd.remove(w);
                }
                // Blokkeer andere checkboxes als limiet bereikt
                if (geselecteerd.size() >= resterend) {
                    alleCheckboxes.forEach(c -> { if (!c.isSelected()) c.setDisable(true); });
                } else {
                    alleCheckboxes.forEach(c -> c.setDisable(false));
                }
            });

            HBox row = new HBox(10, cb);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(cb, Priority.ALWAYS);
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
            for (WerknemerDTO w : geselecteerd) {
                facade.voegLidToe(teamID, w.id());
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