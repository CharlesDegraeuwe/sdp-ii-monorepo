package hogent.sdp2.sdpii.gui.app.taken.components.manager.check;

import domain.auth.Sessie;
import domain.dto.*;
import hogent.sdp2.sdpii.gui.router.Router;
import domain.facades.LocatieFacade;
import domain.facades.TakenFacade;
import domain.facades.TeamFacade;
import hogent.sdp2.sdpii.gui.app.taken.components.items.FinishedTaakItem;
import hogent.sdp2.sdpii.gui.app.taken.components.items.TaakItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.Getter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CheckTaskController extends BorderPane {
    @FXML Label titel_taken;
    @FXML Label afgewerkte_taken_titel;
    @FXML Label edit_button;
    @FXML Label geen_filter_label;
    @FXML VBox finished_task_container;
    @FXML VBox taken_container;
    @FXML @Getter BorderPane page_container;
    @FXML ComboBox<String> Toegewezen;
    @FXML ComboBox<LocatieDTO> Locatie;
    @FXML ComboBox<TeamDTO> Team;
    @FXML ComboBox<TeamLidDTO> Werknemer;
    @FXML VBox detail_card;
    @FXML Label detail_deadline;
    @FXML Label detail_locatie;
    @FXML Label detail_werknemer;
    @FXML TextField detail_titel;
    @FXML TextArea detail_beschrijving;
    @FXML Button opslaan_button;

    private static final String ALLE_TOEGEWEZEN = "Alle taken";
    private static final LocatieDTO ALLE_LOCATIES = new LocatieDTO(-1, "Alle locaties", null, null, null);
    private static final TeamDTO ALLE_TEAMS = new TeamDTO(-1, "Alle teams", null, null, null, null, null);
    private static final TeamLidDTO ALLE_WERKNEMERS = new TeamLidDTO(-1, "Alle werknemers", "", null, null, null, false, 0, null, null, null, null, null, null);

    private TaakDTO geselecteerdeTaak;
    private List<TaakDTO> alleTaken;
    private TakenFacade takenFacade;
    private final TeamFacade teamFacade = new TeamFacade();


    public CheckTaskController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/check/CheckTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        init(takenFacade);
    }

    public void init(TakenFacade takenFacade) {
        this.takenFacade = takenFacade;
        boolean rol = Sessie.getInstance().isWerknemer();
        edit_button.setVisible(!rol);
        afgewerkte_taken_titel.setText("afgewerkte taken:");

        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        if (isSupervisor) {
            Locatie.setVisible(false); Locatie.setManaged(false);
            Team.setVisible(false);    Team.setManaged(false);
            Werknemer.setVisible(false); Werknemer.setManaged(false);
        }

        // Edit / opslaan
        edit_button.setOnMouseClicked(e -> {
            if (opslaan_button.isVisible()) {
                zetEditModusUit();
                if (geselecteerdeTaak != null) toonDetails(geselecteerdeTaak);
            } else {
                zetEditModusAan();
            }
        });
        opslaan_button.setOnMouseClicked(e -> {
            if (geselecteerdeTaak == null) return;
            takenFacade.wijzigTaak(geselecteerdeTaak.id(), detail_titel.getText(), detail_beschrijving.getText());
            zetEditModusUit();
        });


        // Converters
        setConverter(Locatie, l -> l.naam());
        setConverter(Team, t -> t.naam());
        setConverter(Werknemer, w -> w.werknemerId() == -1 ? w.naam() : w.voornaam() + " " + w.naam());
        // Toegewezen items
        Toegewezen.getItems().setAll(ALLE_TOEGEWEZEN, "Toegewezen", "Niet toegewezen");
        Toegewezen.setValue(ALLE_TOEGEWEZEN);

        // Locaties laden met reset-optie
        List<LocatieDTO> locaties = new ArrayList<>();
        locaties.add(ALLE_LOCATIES);
        locaties.addAll(new LocatieFacade().geefAlleLocaties());
        Locatie.getItems().setAll(locaties);
        Locatie.setValue(ALLE_LOCATIES);

        // Team start met enkel reset-optie
        Team.getItems().setAll(ALLE_TEAMS);
        Team.setValue(ALLE_TEAMS);

        // Werknemer start met enkel reset-optie
        Werknemer.getItems().setAll(ALLE_WERKNEMERS);
        Werknemer.setValue(ALLE_WERKNEMERS);

        // Cascade: Locatie → Teams
        Locatie.setOnAction(e -> {
            LocatieDTO sel = Locatie.getValue();
            Team.getItems().setAll(ALLE_TEAMS);
            Team.setValue(ALLE_TEAMS);
            Werknemer.getItems().setAll(ALLE_WERKNEMERS);
            Werknemer.setValue(ALLE_WERKNEMERS);
            if (sel != null && sel.id() != -1) {
                List<TeamDTO> teams = teamFacade.geefTeamsVanSite(sel.id());
                List<TeamDTO> teamItems = new ArrayList<>();
                teamItems.add(ALLE_TEAMS);
                teamItems.addAll(teams);
                Team.getItems().setAll(teamItems);

                List<TeamLidDTO> leden = teams.stream()
                        .flatMap(t -> teamFacade.geefTeamLedenMetSupervisor(t.id()).stream())
                        .distinct().toList();
                List<TeamLidDTO> werkItems = new ArrayList<>();
                werkItems.add(ALLE_WERKNEMERS);
                werkItems.addAll(leden);
                Werknemer.getItems().setAll(werkItems);
            }
            toonTaken();
        });
        // Cascade: Team → Werknemers
        Team.setOnAction(e -> {
            TeamDTO sel = Team.getValue();
            Werknemer.getItems().setAll(ALLE_WERKNEMERS);
            Werknemer.setValue(ALLE_WERKNEMERS);
            if (sel != null && sel.id() != -1) {
                List<TeamLidDTO> leden = teamFacade.geefWerknemersVanTeam(sel.id());
                List<TeamLidDTO> werkItems = new ArrayList<>();
                werkItems.add(ALLE_WERKNEMERS);
                werkItems.addAll(leden);
                Werknemer.getItems().setAll(werkItems);
            }
            toonTaken();
        });

        Werknemer.setOnAction(e -> toonTaken());
        Toegewezen.setOnAction(e -> toonTaken());

        alleTaken = takenFacade.geefAlleTaken();
        toonTaken();
    }

    public void herlaad() {
        alleTaken = takenFacade.geefAlleTaken();
        toonTaken();
    }

    private void toonTaken() {
        taken_container.getChildren().clear();
        finished_task_container.getChildren().clear();
        verbergDetails();
        geen_filter_label.setVisible(false);
        geen_filter_label.setManaged(false);

        String toegewezenFilter = Toegewezen.getValue() != null && !Toegewezen.getValue().equals(ALLE_TOEGEWEZEN)
                ? Toegewezen.getValue() : null;
        Integer siteFilter = Locatie.getValue() != null && Locatie.getValue().id() != -1
                ? Locatie.getValue().id() : null;
        Integer teamFilter = Team.getValue() != null && Team.getValue().id() != -1
                ? Team.getValue().id() : null;
        Integer werknemerFilter = Werknemer.getValue() != null && Werknemer.getValue().werknemerId() != -1
                ? Werknemer.getValue().werknemerId() : null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int aantalTaken = 0;

        for (TaakDTO taak : alleTaken) {
            // Toegewezen filter
            if (toegewezenFilter != null) {
                if (toegewezenFilter.equals("Toegewezen") && taak.werknemer() == null) continue;
                if (toegewezenFilter.equals("Niet toegewezen") && taak.werknemer() != null) continue;
            }
            if (siteFilter != null && !siteFilter.equals(taak.siteId())) continue;
            if (teamFilter != null && !teamFilter.equals(taak.teamId())) continue;
            if (werknemerFilter != null) {
                if (taak.werknemer() == null || taak.werknemer().id() != werknemerFilter) continue;
            }

            aantalTaken++;
            String deadlineTekst = "Deadline: " + taak.deadline().format(formatter);
            if (taak.afgewerkt().equals("nee")) {
                TaakItemController item = new TaakItemController(taak.titel(), deadlineTekst);
                item.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> toonDetails(taak));
                taken_container.getChildren().add(item);
            } else {
                finished_task_container.getChildren().add(new FinishedTaakItem(taak.titel(), "Afgewerkt"));
            }
        }

        if (aantalTaken == 0) {
            geen_filter_label.setText("Geen taken gevonden voor de geselecteerde filters.");
            geen_filter_label.setVisible(true);
            geen_filter_label.setManaged(true);
        }
    }

    private void toonDetails(TaakDTO taak) {
        geselecteerdeTaak = taak;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        detail_deadline.setText("Deadline: " + taak.deadline().format(formatter));
        detail_werknemer.setText(taak.werknemer() != null
                ? "Toegewezen aan: " + taak.werknemer().voornaam() + " " + taak.werknemer().naam()
                : "Niet toegewezen");
        detail_titel.setText(taak.titel());
        detail_beschrijving.setText(taak.beschrijving() != null ? taak.beschrijving() : "");
        LocatieDTO gevondenLocatie = taak.siteId() != null
                ? new LocatieFacade().vindLocatie(taak.siteId())
                : null;
        if (gevondenLocatie != null) {
            detail_locatie.setText(gevondenLocatie.naam());
            final int siteId = gevondenLocatie.id();
            detail_locatie.setOnMouseClicked(e -> Router.getInstance().navigeerNaarLocatie(siteId));
            detail_locatie.setVisible(true);
            detail_locatie.setManaged(true);
        } else {
            detail_locatie.setVisible(false);
            detail_locatie.setManaged(false);
        }
        zetEditModusUit();
        detail_card.setVisible(true);
        detail_card.setManaged(true);
    }

    public void verbergDetails() {
        detail_card.setVisible(false);
        detail_card.setManaged(false);
        geselecteerdeTaak = null;
    }

    private void zetEditModusAan() {
        detail_titel.setEditable(true);
        detail_beschrijving.setEditable(true);
        opslaan_button.setVisible(true);
        opslaan_button.setManaged(true);
        edit_button.setText("annuleer");
    }

    private void zetEditModusUit() {
        detail_titel.setEditable(false);
        detail_beschrijving.setEditable(false);
        opslaan_button.setVisible(false);
        opslaan_button.setManaged(false);
        edit_button.setText("edit");
    }

    private <T> void setConverter(ComboBox<T> comboBox, java.util.function.Function<T, String> naamFunctie) {
        comboBox.setConverter(new StringConverter<>() {
            @Override public String toString(T obj) { return obj != null ? naamFunctie.apply(obj) : ""; }
            @Override public T fromString(String s) { return null; }
        });
    }
}
