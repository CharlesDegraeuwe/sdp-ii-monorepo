package hogent.sdp2.sdpii.gui.app.taken.components.manager.assign;

import domain.auth.Sessie;
import domain.dto.*;
import domain.facades.LocatieFacade;
import domain.facades.TakenFacade;
import domain.facades.TeamFacade;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignMemberItemController;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignTaakItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssignTaskController extends BorderPane {
    @FXML ComboBox<LocatieDTO> Locatie;
    @FXML ComboBox<TeamDTO> Team;
    @FXML ComboBox<TeamLidDTO> Werknemer;
    @FXML VBox taskListContainer;
    @FXML VBox teamListContainer;
    @FXML VBox memberListContainer;
    @FXML Button assignButton;
    @FXML CheckBox toonToegewezenCheckbox;

    private TaakDTO geselecteerdeTaak;
    private TeamLidDTO geselecteerdeLid;
    private List<TaakDTO> alleTaken;

    private final TeamFacade teamFacade = new TeamFacade();

    private static final String SELECTED_STYLE = "-fx-border-color: #E31B35; -fx-border-radius: 999px; -fx-border-width: 2;";

    public AssignTaskController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/assign/AssignTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Init(takenFacade);
    }

    public void Init(TakenFacade takenFacade) {
        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        if (isSupervisor) {
            Locatie.setVisible(false); Locatie.setManaged(false);
            Team.setVisible(false);    Team.setManaged(false);
            teamListContainer.setVisible(false); teamListContainer.setManaged(false);
        }

        alleTaken = takenFacade.geefAlleTaken();
        vulTaakList();

        toonToegewezenCheckbox.setOnAction(e -> vulTaakList());

        setConverter(Locatie, l -> l.naam());
        setConverter(Team, t -> t.naam());
        setConverter(Werknemer, w -> w.voornaam() + " " + w.naam());

        LocatieFacade locatieFacade = new LocatieFacade();
        Locatie.getItems().setAll(locatieFacade.geefAlleLocaties());

        Locatie.setOnAction(e -> {
            LocatieDTO geselecteerdeLocatie = Locatie.getValue();
            Team.getItems().clear();
            Team.setValue(null);
            Werknemer.getItems().clear();
            Werknemer.setValue(null);
            if (geselecteerdeLocatie == null) {
                vulMemberList(List.of());
                return;
            }
            List<TeamDTO> teams = teamFacade.geefTeamsVanSite(geselecteerdeLocatie.id());
            Team.getItems().setAll(teams);

            List<TeamLidDTO> leden = teams.stream()
                    .flatMap(t -> teamFacade.geefTeamLedenMetSupervisor(t.id()).stream())
                    .distinct().toList();
            vulMemberList(leden);
            Werknemer.getItems().setAll(leden);
        });

        Team.setOnAction(e -> {
            TeamDTO geselecteerdTeam = Team.getValue();
            Werknemer.getItems().clear();
            Werknemer.setValue(null);
            if (geselecteerdTeam == null) return;
            List<TeamLidDTO> leden = teamFacade.geefTeamLedenMetSupervisor(geselecteerdTeam.id());
            vulMemberList(leden);
            Werknemer.getItems().setAll(leden);
        });

        Werknemer.setOnAction(e -> {
            TeamLidDTO geselecteerdeWerknemer = Werknemer.getValue();
            if (geselecteerdeWerknemer == null) return;
            vulMemberList(List.of(geselecteerdeWerknemer));
        });

        assignButton.setOnAction(e -> {
            if (geselecteerdeTaak == null || geselecteerdeLid == null) {
                assignButton.setText("Selecteer eerst een taak en medewerker");
                return;
            }
            try {
                String resultaat = takenFacade.wijsTaakToe(geselecteerdeTaak.id(), geselecteerdeLid.werknemerId());
                assignButton.setText(resultaat);
                geselecteerdeTaak = null;
                geselecteerdeLid = null;
                taskListContainer.getChildren().forEach(n -> n.setStyle(""));
                memberListContainer.getChildren().forEach(n -> n.setStyle(""));
                alleTaken = takenFacade.geefAlleTaken();
                vulTaakList();
            } catch (Exception ex) {
                assignButton.setText("Fout: " + ex.getMessage());
            }
        });
    }

    private void vulTaakList() {
        taskListContainer.getChildren().clear();
        geselecteerdeTaak = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean toonAlles = toonToegewezenCheckbox.isSelected();

        for (TaakDTO taak : alleTaken) {
            if (taak.afgewerkt().equals("ja")) continue;
            if (!toonAlles && taak.werknemer() != null) continue;

            String deadlineTekst = "Deadline: " + taak.deadline().format(formatter);
            AssignTaakItemController item = new AssignTaakItemController(taak.titel(), deadlineTekst);
            item.setOnMouseClicked(e -> {
                taskListContainer.getChildren().forEach(n -> n.setStyle(""));
                item.setStyle(SELECTED_STYLE);
                geselecteerdeTaak = taak;
            });
            taskListContainer.getChildren().add(item);
        }
    }

    private void vulMemberList(List<TeamLidDTO> werknemers) {
        memberListContainer.getChildren().clear();
        for (TeamLidDTO w : werknemers) {
            String label = w.voornaam() + " " + w.naam();
            if (w.isSupervisor()) label += " (supervisor)";
            AssignMemberItemController item = new AssignMemberItemController(
                    label, "rgba(240, 240, 240, 0.48)");
            item.setOnMouseClicked(e -> {
                memberListContainer.getChildren().forEach(n -> n.setStyle(""));
                item.setStyle(SELECTED_STYLE);
                geselecteerdeLid = w;
            });
            memberListContainer.getChildren().add(item);
        }
    }

    private <T> void setConverter(ComboBox<T> comboBox, java.util.function.Function<T, String> naamFunctie) {
        comboBox.setConverter(new StringConverter<>() {
            @Override public String toString(T obj) { return obj != null ? naamFunctie.apply(obj) : ""; }
            @Override public T fromString(String s) { return null; }
        });
    }
}