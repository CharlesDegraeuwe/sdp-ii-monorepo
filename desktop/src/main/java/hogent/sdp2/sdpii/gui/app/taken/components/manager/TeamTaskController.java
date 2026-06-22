package hogent.sdp2.sdpii.gui.app.taken.components.manager;

import domain.dto.*;
import domain.facades.LocatieFacade;
import domain.facades.TakenFacade;
import domain.facades.TeamFacade;
import domain.facades.WerknemersFacade;
import hogent.sdp2.sdpii.gui.app.taken.components.items.TaakItemController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TeamTaskController extends BorderPane {

    @FXML private ComboBox<LocatieDTO> filterLocatie;
    @FXML private ComboBox<TeamDTO> filterTeam;
    @FXML private CheckBox checkToegewezen;
    @FXML private VBox takenLijstContainer;

    @FXML private VBox stateLeeg;
    @FXML private VBox stateDetail;

    @FXML private Label detailTitel;
    @FXML private Label detailTijden;
    @FXML private Label detailLocatie;
    @FXML private Label detailSpecificaties;

    // Toewijzing UI elementen (Zijbalk)
    @FXML private VBox toegewezenLijst;
    @FXML private Button btnBeheerToewijzingen;
    @FXML private Label toewijsFeedback;

    private TakenFacade takenFacade;
    private final TeamFacade teamFacade = new TeamFacade();
    private List<TaakDTO> alleTaken = new ArrayList<>();
    private TaakDTO geselecteerdeTaak;

    // Tijdelijke opslag voor de popup
    private List<WerknemerDTO> huidigeMogelijkeLeden = new ArrayList<>();
    private List<Integer> huidigeToegewezenIds = new ArrayList<>();

    private static final LocatieDTO ALLE_LOCATIES = new LocatieDTO(-1, "Alle locaties", null, null, null);
    private static final TeamDTO ALLE_TEAMS = new TeamDTO(-1, "Alle teams", null, null, null, null, null);

    public TeamTaskController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/TeamTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        this.takenFacade = takenFacade;
        initFilters();
        toonStateLeeg();
        herlaad();
    }

    private void initFilters() {
        zetConverter(filterLocatie, LocatieDTO::naam);
        zetConverter(filterTeam, TeamDTO::naam);

        List<LocatieDTO> locaties = new ArrayList<>();
        locaties.add(ALLE_LOCATIES);
        locaties.addAll(new LocatieFacade().geefAlleLocaties());
        filterLocatie.getItems().setAll(locaties);
        filterLocatie.setValue(ALLE_LOCATIES);

        filterTeam.getItems().setAll(ALLE_TEAMS);
        filterTeam.setValue(ALLE_TEAMS);

        filterLocatie.setOnAction(e -> { updateTeamsDropdown(); renderTakenLijst(); });
        filterTeam.setOnAction(e -> renderTakenLijst());
        checkToegewezen.setOnAction(e -> renderTakenLijst());

        // Koppel de popup knop
        btnBeheerToewijzingen.setOnAction(e -> openToewijzenPopup());
    }

    public void herlaad() {
        new Thread(() -> {
            try {
                List<TaakDTO> geladen = takenFacade.geefAlleTaken();
                Platform.runLater(() -> {
                    alleTaken = geladen;
                    renderTakenLijst();

                    if(geselecteerdeTaak != null) {
                        for(TaakDTO t : alleTaken) {
                            if(t.id() == geselecteerdeTaak.id()) toonTaakDetails(t);
                        }
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void updateTeamsDropdown() {
        LocatieDTO sel = filterLocatie.getValue();
        filterTeam.getItems().clear();
        filterTeam.getItems().add(ALLE_TEAMS);

        if (sel != null && sel.id() != -1) {
            filterTeam.getItems().addAll(teamFacade.geefTeamsVanSite(sel.id()));
        }
        filterTeam.setValue(ALLE_TEAMS);
    }

    private void renderTakenLijst() {
        takenLijstContainer.getChildren().clear();
        LocatieDTO locFilter = filterLocatie.getValue();
        boolean verbergToegewezen = checkToegewezen.isSelected();

        for (TaakDTO taak : alleTaken) {
            if (taak.isAfgewerkt()) continue;
            // Checkt nu de LIJST van werknemerIds in plaats van een enkele ID
            if (verbergToegewezen && taak.werknemerIds() != null && !taak.werknemerIds().isEmpty()) continue;
            if (locFilter != null && locFilter.id() != -1 && !locFilter.naam().equalsIgnoreCase(taak.locatie())) continue;

            TaakItemController item = new TaakItemController(taak.naam(), "Deadline: " + formatDatum(taak.deadline()));
            if(item.lookup(".taak_delete_btn") != null) item.lookup(".taak_delete_btn").setVisible(false);
            if(item.lookup(".taak_checkbox") != null) item.lookup(".taak_checkbox").setVisible(false);

            item.setOnMouseClicked(e -> toonTaakDetails(taak));
            takenLijstContainer.getChildren().add(item);
        }
    }

    private void toonTaakDetails(TaakDTO taak) {
        geselecteerdeTaak = taak;
        stateLeeg.setVisible(false); stateLeeg.setManaged(false);
        stateDetail.setVisible(true); stateDetail.setManaged(true);
        toewijsFeedback.setVisible(false); toewijsFeedback.setManaged(false);

        detailTitel.setText(taak.naam());
        detailTijden.setText("Deadline: " + formatDatum(taak.deadline()));
        detailLocatie.setText(taak.locatie() != null ? taak.locatie() : "Onbekende locatie");
        detailSpecificaties.setText(taak.specificaties() != null && !taak.specificaties().isBlank() ? taak.specificaties() : "Geen verdere specificaties.");

        toegewezenLijst.getChildren().clear();
        toegewezenLijst.getChildren().add(new Label("Gegevens ophalen..."));

        new Thread(() -> {
            try {
                WerknemersFacade werknemerService = new WerknemersFacade();
                List<WerknemerDTO> alleWerknemers = werknemerService.geefAlleWerknemers();

                boolean isManagerOrAdmin = domain.auth.Sessie.getInstance().isMangerOrAdmin();
                int ingelogdeWerknemerId = domain.auth.Sessie.getInstance().getIngelogdeWerknemer().id();

                if (isManagerOrAdmin) {
                    huidigeMogelijkeLeden = alleWerknemers;
                } else {
                    var teams = teamFacade.getTeamsVanWerknemer(ingelogdeWerknemerId);
                    huidigeMogelijkeLeden = teams.stream()
                        .flatMap(t -> teamFacade.getTeamLeden(t.id()).stream())
                        .map(lid -> new WerknemerDTO(lid.werknemerId(), lid.naam(), lid.voornaam(),
                            lid.email(), lid.telefoonnummer(), null, lid.rol(), "Actief"))
                        .distinct().toList();
                }

                huidigeToegewezenIds.clear();
                // Haalt nu de hele lijst van ID's uit de DTO
                if (taak.werknemerIds() != null) {
                    huidigeToegewezenIds.addAll(taak.werknemerIds());
                }

                List<String> toegewezenNamen = new ArrayList<>();
                for (WerknemerDTO w : alleWerknemers) {
                    if (huidigeToegewezenIds.contains(w.id())) {
                        toegewezenNamen.add(w.voornaam() + " " + w.naam());
                    }
                }

                Platform.runLater(() -> {
                    toegewezenLijst.getChildren().clear();
                    if (toegewezenNamen.isEmpty()) {
                        Label leeg = new Label("Nog niemand toegewezen");
                        leeg.setStyle("-fx-text-fill: #ef4444; -fx-font-style: italic;");
                        toegewezenLijst.getChildren().add(leeg);
                    } else {
                        for (String naam : toegewezenNamen) {
                            Label l = new Label("• " + naam);
                            l.setStyle("-fx-font-size: 14px; -fx-text-fill: #10b981; -fx-font-weight: bold;");
                            toegewezenLijst.getChildren().add(l);
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    toegewezenLijst.getChildren().clear();
                    toegewezenLijst.getChildren().add(new Label("Fout bij ophalen."));
                });
            }
        }).start();
    }

    private void openToewijzenPopup() {
        if (geselecteerdeTaak == null || huidigeMogelijkeLeden == null) return;

        VBox modalCard = new VBox(16);
        modalCard.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-padding: 24; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 4);");
        modalCard.setMaxWidth(400);
        modalCard.setMaxHeight(VBox.USE_PREF_SIZE);

        Label title = new Label("Teamleden Toewijzen");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label subTitle = new Label("Duid aan wie aan de taak '" + geselecteerdeTaak.naam() + "' moet werken.");
        subTitle.setStyle("-fx-text-fill: #64748b; -fx-wrap-text: true;");

        VBox checkboxContainer = new VBox(10);
        List<CheckBox> alleVinkjes = new ArrayList<>();

        if (huidigeMogelijkeLeden.isEmpty()) {
            checkboxContainer.getChildren().add(new Label("Geen medewerkers gevonden voor jouw niveau."));
        } else {
            for (WerknemerDTO w : huidigeMogelijkeLeden) {
                CheckBox cb = new CheckBox(w.voornaam() + " " + w.naam());
                cb.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");
                cb.setUserData(w);

                if (huidigeToegewezenIds.contains(w.id())) {
                    cb.setSelected(true);
                }

                checkboxContainer.getChildren().add(cb);
                alleVinkjes.add(cb);
            }
        }

        ScrollPane scrollPane = new ScrollPane(checkboxContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        Button btnOpslaan = new Button("Toewijzingen Opslaan");
        btnOpslaan.setStyle("-fx-background-color: #e11d48; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20; -fx-cursor: hand;");
        btnOpslaan.setMaxWidth(Double.MAX_VALUE);

        Button btnAnnuleer = new Button("Annuleren");
        btnAnnuleer.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAnnuleer.setMaxWidth(Double.MAX_VALUE);

        modalCard.getChildren().addAll(title, subTitle, scrollPane, btnOpslaan, btnAnnuleer);

        StackPane overlay = new StackPane(modalCard);
        overlay.setStyle("-fx-background-color: rgba(15, 23, 42, 0.4);");

        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        Window mainWindow = this.getScene().getWindow();
        popupStage.initOwner(mainWindow);

        Scene scene = new Scene(overlay);
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);

        popupStage.setX(mainWindow.getX());
        popupStage.setY(mainWindow.getY());
        popupStage.setWidth(mainWindow.getWidth());
        popupStage.setHeight(mainWindow.getHeight());

        btnAnnuleer.setOnAction(e -> popupStage.close());

        btnOpslaan.setOnAction(e -> {
            btnOpslaan.setText("Bezig...");
            btnOpslaan.setDisable(true);

            List<Integer> gekozenIds = new ArrayList<>();
            List<String> gekozenNamen = new ArrayList<>();

            for (CheckBox cb : alleVinkjes) {
                if (cb.isSelected()) {
                    WerknemerDTO w = (WerknemerDTO) cb.getUserData();
                    gekozenIds.add(w.id());
                    gekozenNamen.add(w.voornaam() + " " + w.naam());
                }
            }

            new Thread(() -> {
                try {
                    takenFacade.updateTaakToewijzingen(geselecteerdeTaak.id(), gekozenIds);

                    Platform.runLater(() -> {
                        popupStage.close();
                        toonFeedback("Toewijzingen succesvol opgeslagen!", false);

                        toegewezenLijst.getChildren().clear();

                        if (gekozenNamen.isEmpty()) {
                            Label leeg = new Label("Nog niemand toegewezen");
                            leeg.setStyle("-fx-text-fill: #ef4444; -fx-font-style: italic;");
                            toegewezenLijst.getChildren().add(leeg);
                        } else {
                            for (String naam : gekozenNamen) {
                                Label l = new Label("• " + naam);
                                l.setStyle("-fx-font-size: 14px; -fx-text-fill: #10b981; -fx-font-weight: bold;");
                                toegewezenLijst.getChildren().add(l);
                            }
                        }

                        huidigeToegewezenIds.clear();
                        huidigeToegewezenIds.addAll(gekozenIds);

                        herlaad();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        btnOpslaan.setText("Fout! Probeer opnieuw");
                        btnOpslaan.setDisable(false);
                    });
                }
            }).start();
        });

        popupStage.showAndWait();
    }

    private void toonStateLeeg() {
        stateDetail.setVisible(false); stateDetail.setManaged(false);
        stateLeeg.setVisible(true); stateLeeg.setManaged(true);
    }

    private void toonFeedback(String msg, boolean isError) {
        toewijsFeedback.setText(msg);
        toewijsFeedback.setStyle(isError ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-padding: 8px 0 0 0;");
        toewijsFeedback.setVisible(true);
        toewijsFeedback.setManaged(true);
    }

    private String formatDatum(String deadline) {
        if (deadline == null || deadline.length() < 10) return "—";
        try {
            LocalDate d = LocalDate.parse(deadline.substring(0, 10));
            return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) { return "—"; }
    }

    private <T> void zetConverter(ComboBox<T> box, java.util.function.Function<T, String> naamFunctie) {
        box.setConverter(new StringConverter<>() {
            public String toString(T obj) { return obj != null ? naamFunctie.apply(obj) : ""; }
            public T fromString(String s) { return null; }
        });
    }
}
