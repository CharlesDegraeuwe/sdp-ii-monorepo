package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.*;
import domain.facades.TeamFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTeamsController extends VBox {

    @FXML TextField nameField;
    @FXML TextArea descriptionField;
    @FXML ComboBox<WerknemerDTO> managerCombo;
    @FXML ComboBox<SiteDTO> plantCombo;
    @FXML Button createTeamBtn;
    @FXML VBox employeeListContainer;

    private TeamFacade facade;
    private List<WerknemerDTO> alleWerknemers;
    private  List<WerknemerDTO> managers;
    private  List<WerknemerDTO> werknemers;
    private List<SelectedLid> geselecteerdeLeden = new ArrayList<>();

    public CreateTeamsController(TeamFacade facade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/teamspagina/CreeerTeams.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.facade = facade;
        init();
    }

    private void init() {
        // Sites enz laden
        List<SiteDTO> sites = facade.getAlleSites();
        plantCombo.getItems().addAll(sites);
        plantCombo.setConverter(new StringConverter<>() {
            @Override public String toString(SiteDTO s) { return s == null ? "" : s.naam(); }
            @Override public SiteDTO fromString(String s) { return null; }
        });

        // Werknemers laden voor manager dropdown en ledenselectie
        alleWerknemers = facade.getAlleWerknemersVoorTeams();
        managers = alleWerknemers.stream().filter(werknemerDTO -> werknemerDTO.rol().equals("Manager")).toList();
        werknemers = alleWerknemers.stream().filter(werknemerDTO -> !werknemerDTO.rol().equals("Manager") && !werknemerDTO.rol().equals("Admin")).toList();
        managerCombo.getItems().addAll(managers);
        managerCombo.setConverter(new StringConverter<>() {
            @Override public String toString(WerknemerDTO w) { return w == null ? "" : w.voornaam() + " " + w.naam(); }
            @Override public WerknemerDTO fromString(String s) { return null; }
        });

        // Werknemerslijst tonen in rechterkolom
        buildEmployeeList();

        createTeamBtn.setOnAction(e -> handleCreate());

    }

    private void buildEmployeeList() {
        employeeListContainer.getChildren().clear();
        geselecteerdeLeden.clear();

        List<CheckBox> alleSelectCbs = new ArrayList<>();
        List<CheckBox> alleSupervisorCbs = new ArrayList<>();

        for (WerknemerDTO w : werknemers) {
            SelectedLid lid = new SelectedLid(w);

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
                lid.selected = selectCb.isSelected();
                supervisorCb.setDisable(!selectCb.isSelected());
                if (!selectCb.isSelected()) {
                    supervisorCb.setSelected(false);
                    lid.supervisor = false;
                }

                long aantalGeselecteerd = geselecteerdeLeden.stream().filter(l -> l.selected).count();
                alleSelectCbs.forEach(cb -> {
                    if (!cb.isSelected()) cb.setDisable(aantalGeselecteerd >= 4);
                });
                updateSupervisorCheckboxes(alleSupervisorCbs);
            });

            supervisorCb.setOnAction(e -> {
                lid.supervisor = supervisorCb.isSelected();
                updateSupervisorCheckboxes(alleSupervisorCbs);
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(10, selectCb, naam, spacer, supervisorCb);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMaxWidth(Double.MAX_VALUE);
            row.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 20; -fx-padding: 10 14;");

            employeeListContainer.getChildren().add(row);
            geselecteerdeLeden.add(lid);
        }
    }

    private void updateSupervisorCheckboxes(List<CheckBox> alleSupervisorCbs) {
        boolean heeftSupervisor = false;
        for (int i = 0; i < geselecteerdeLeden.size(); i++) {
            if (geselecteerdeLeden.get(i).supervisor) {
                heeftSupervisor = true;
                break;
            }
        }

        for (int i = 0; i < alleSupervisorCbs.size(); i++) {
            CheckBox cb = alleSupervisorCbs.get(i);
            SelectedLid lid = geselecteerdeLeden.get(i);

            if (!lid.selected) {
                cb.setDisable(true);
            } else if (heeftSupervisor && !lid.supervisor) {
                // Er is al een supervisor, blokkeer de rest
                cb.setDisable(true);
            } else {
                cb.setDisable(false);
            }
        }
    }

    private void handleCreate() {
        String naam = nameField.getText().trim();
        String beschrijving = descriptionField.getText().trim();

        if (naam.isEmpty()) {
            nameField.setStyle("-fx-border-color: #E31B35; -fx-border-radius: 20; -fx-background-radius: 20; -fx-background-color: #f5f5f5; -fx-padding: 10 16;");
            return;
        }

        Integer managerId = managerCombo.getValue() != null ? managerCombo.getValue().id() : null;
        Integer siteId = plantCombo.getValue() != null ? plantCombo.getValue().id() : null;

        List<CreateTeamLidDTO> leden = geselecteerdeLeden.stream()
                .filter(l -> l.selected)
                .limit(4)
                .map(l -> new CreateTeamLidDTO(l.werknemer.id(), l.supervisor))
                .toList();

        CreateTeamDTO dto = new CreateTeamDTO(naam, beschrijving, managerId, siteId, leden);
        facade.maakTeam(dto);

        nameField.clear();
        descriptionField.clear();
        managerCombo.setValue(null);
        plantCombo.setValue(null);
        buildEmployeeList();
    }

    private static class SelectedLid {
        WerknemerDTO werknemer;
        boolean selected = false;
        boolean supervisor = false;

        SelectedLid(WerknemerDTO w) { this.werknemer = w; }
    }
}