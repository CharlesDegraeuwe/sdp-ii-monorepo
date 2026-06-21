package hogent.sdp2.sdpii.gui.app.teams.teamspagina.components;

import domain.auth.Sessie;
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
import javafx.scene.layout.Region;
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
        locatie.setText(team.siteNaam() != null ? team.siteNaam() : "Geen locatie");
        manager.setText(team.managerNaam() != null ? team.managerNaam() : "Geen manager");

        locatie.setOnMouseClicked(e -> {
            Router.getInstance().navigeerNaar(Scherm.LOCATIES);
        });

        boolean isSupervisor = Sessie.getInstance().isSuperVisor();

        // Supervisor mag team niet verwijderen
        if (isSupervisor) {
            deleteBtn.setVisible(false);
            deleteBtn.setManaged(false);
        } else {
            deleteBtn.setOnMouseClicked(e -> {
                facade.verwijderTeam(teamID);
                if (onRefresh != null) onRefresh.run();
            });
        }

        showLeden();
    }

    private void showLeden() {
        container.getChildren().clear();
        teamleden = facade.getTeamLeden(teamID);
        for (int i = 0; i < teamleden.size(); i++) {
            TeamLidController lidCtrl = new TeamLidController(
                    teamleden.get(i), i, teamID, teamleden.size(), facade, this::refresh, onNavigeerNaarUser);
            lidCtrl.setMaxWidth(Double.MAX_VALUE);
            container.getChildren().add(lidCtrl);
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

        boolean heeftAlSupervisor = teamleden.stream().anyMatch(TeamLidDTO::isSupervisor);

        List<WerknemerDTO> beschikbaar = facade.getBeschikbareWerknemers(teamID);
        List<SelectedLid> geselecteerd = new ArrayList<>();
        List<CheckBox> alleSelectCbs = new ArrayList<>();
        List<CheckBox> alleSupervisorCbs = new ArrayList<>();

        VBox lijst = new VBox(8);
        VBox.setVgrow(lijst, Priority.ALWAYS);

        for (WerknemerDTO w : beschikbaar) {
            SelectedLid sl = new SelectedLid(w);

            CheckBox selectCb = new CheckBox();
            alleSelectCbs.add(selectCb);

            Label naam = new Label(w.voornaam() + " " + w.naam());
            naam.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a1a1a;");
            naam.setMinWidth(Region.USE_PREF_SIZE);

            CheckBox supervisorCb = new CheckBox("supervisor");
            alleSupervisorCbs.add(supervisorCb);
            supervisorCb.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444;");
            supervisorCb.setDisable(true);

            selectCb.setOnAction(e -> {
                sl.selected = selectCb.isSelected();
                supervisorCb.setDisable(!selectCb.isSelected());
                if (!selectCb.isSelected()) {
                    supervisorCb.setSelected(false);
                    sl.supervisor = false;
                }

                long aantalGeselecteerd = geselecteerd.stream().filter(l -> l.selected).count();
                alleSelectCbs.forEach(cb -> {
                    if (!cb.isSelected()) cb.setDisable(aantalGeselecteerd >= resterend);
                });
                updateSupervisorCbs(alleSupervisorCbs, geselecteerd, heeftAlSupervisor);
            });

            supervisorCb.setOnAction(e -> {
                sl.supervisor = supervisorCb.isSelected();
                updateSupervisorCbs(alleSupervisorCbs, geselecteerd, heeftAlSupervisor);
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(10, selectCb, naam, spacer, supervisorCb);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMaxWidth(Double.MAX_VALUE);
            row.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20; -fx-padding: 10 14;");
            lijst.getChildren().add(row);
            geselecteerd.add(sl);
        }

        if (beschikbaar.isEmpty()) {
            lijst.getChildren().add(new Label("Geen beschikbare werknemers"));
        }

        Button toevoegBtn = new Button("Toevoegen");
        toevoegBtn.getStyleClass().add("create-btn");
        toevoegBtn.setMaxWidth(Double.MAX_VALUE);
        toevoegBtn.setOnAction(e -> {
            for (SelectedLid sl : geselecteerd) {
                if (!sl.selected) continue;
                facade.voegLidToe(teamID, sl.werknemer.id());
                if (sl.supervisor) {
                    facade.maakSupervisor(teamID, sl.werknemer.id());
                }
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

    private void updateSupervisorCbs(List<CheckBox> cbs, List<SelectedLid> leden, boolean heeftAlSupervisor) {
        boolean nieuweSupervisor = leden.stream().anyMatch(l -> l.supervisor);
        boolean blocked = heeftAlSupervisor || nieuweSupervisor;

        for (int i = 0; i < cbs.size(); i++) {
            CheckBox cb = cbs.get(i);
            SelectedLid lid = leden.get(i);
            if (!lid.selected) {
                cb.setDisable(true);
            } else if (blocked && !lid.supervisor) {
                cb.setDisable(true);
            } else {
                cb.setDisable(false);
            }
        }
    }

    private static class SelectedLid {
        WerknemerDTO werknemer;
        boolean selected = false;
        boolean supervisor = false;
        SelectedLid(WerknemerDTO w) { this.werknemer = w; }
    }
}