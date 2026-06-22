package hogent.sdp2.sdpii.gui.app.planning;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.*;
import domain.facades.*;
import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.beans.binding.NumberBinding;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PlanningController extends BorderPane {
    public static LocalDate startDatumVanuitDashboard = null;

    @FXML private Button maandKnop;
    @FXML private Button weekKnop;
    @FXML private Button dagKnop;
    @FXML private Button vorigeKnop;
    @FXML private Button volgendeKnop;
    @FXML private Label periodeLabel;
    @FXML private VBox kalenderContainer;
    @FXML private VBox detailPanel;
    @FXML private Label detailTitel;
    @FXML private VBox detailLijst;
    @FXML private HBox planningToggleBar;
    @FXML private Button eigenPlanningKnop;
    @FXML private Button teamPlanningKnop;
    @FXML private HBox teamFilterBar;
    @FXML private ComboBox<LocatieDTO> locatieDropdown;
    @FXML private ComboBox<TeamDTO> teamDropdown;
    @FXML private ComboBox<TeamLidDTO> werknemerDropdown;

    @FXML private Label shiftTitel;
    @FXML private VBox shiftOverview;
    @FXML private Label ShiftDetailTitle;
    @FXML private VBox shiftdetails;

    private VBox geselecteerdeCel;

    private enum View { MAAND, WEEK, DAG }
    private View huidigeView = View.MAAND;
    private LocalDate huidigeDatum = LocalDate.now();
    private List<AfwezigheidsOverzichtDTO> afwezigheden = List.of();
    private List<TaakDTO> taken = List.of();
    private List<ShiftDTO> shiften = List.of();
    private int geselecteerdeWerknemerId;

    private static final DateTimeFormatter MAAND_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("nl", "BE"));
    private static final DateTimeFormatter DAG_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("nl", "BE"));

    public PlanningController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/PlanningPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        setTop(new PageTitleController("Planning"));
    }

    @FXML
    public void initialize() {
        if (startDatumVanuitDashboard != null) {
            huidigeDatum = startDatumVanuitDashboard;
            huidigeView = View.MAAND;
            Platform.runLater(() -> setToggle(maandKnop));
            startDatumVanuitDashboard = null;
        } else {
            huidigeDatum = LocalDate.now();
        }

        geselecteerdeWerknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();

        if (Sessie.getInstance().isMangerOrAdmin()) {
            planningToggleBar.setVisible(true);
            planningToggleBar.setManaged(true);
            configureerDropdowns();
            laadLocaties();
        }

        toonDagDetail(huidigeDatum);
        laadData(geselecteerdeWerknemerId);
    }

    private void configureerDropdowns() {
        locatieDropdown.setConverter(new StringConverter<>() {
            public String toString(LocatieDTO l) { return l == null ? "" : l.naam(); }
            public LocatieDTO fromString(String s) { return null; }
        });
        teamDropdown.setConverter(new StringConverter<>() {
            public String toString(TeamDTO t) { return t == null ? "" : t.naam(); }
            public TeamDTO fromString(String s) { return null; }
        });
        werknemerDropdown.setConverter(new StringConverter<>() {
            public String toString(TeamLidDTO w) { return w == null ? "" : w.voornaam() + " " + w.naam(); }
            public TeamLidDTO fromString(String s) { return null; }
        });

        locatieDropdown.valueProperty().addListener((obs, oud, nieuw) -> {
            if (nieuw == null) return;
            teamDropdown.setDisable(false);
            teamDropdown.getItems().clear();
            werknemerDropdown.getItems().clear();
            werknemerDropdown.setDisable(true);
            new Thread(() -> {
                List<TeamDTO> teams = new TeamFacade().geefTeamsVanSite(nieuw.id());
                Platform.runLater(() -> teamDropdown.setItems(FXCollections.observableArrayList(teams)));
            }).start();
        });

        teamDropdown.valueProperty().addListener((obs, oud, nieuw) -> {
            if (nieuw == null) return;
            werknemerDropdown.setDisable(false);
            werknemerDropdown.getItems().clear();
            new Thread(() -> {
                List<TeamLidDTO> leden = new TeamFacade().geefWerknemersVanTeam(nieuw.id());
                Platform.runLater(() -> werknemerDropdown.setItems(FXCollections.observableArrayList(leden)));
            }).start();
        });

        werknemerDropdown.valueProperty().addListener((obs, oud, nieuw) -> {
            if (nieuw == null) return;
            geselecteerdeWerknemerId = nieuw.werknemerId();
            laadData(geselecteerdeWerknemerId);
        });
    }

    private void laadLocaties() {
        new Thread(() -> {
            List<LocatieDTO> locaties = new LocatieFacade().geefAlleLocaties();
            Platform.runLater(() -> locatieDropdown.setItems(FXCollections.observableArrayList(locaties)));
        }).start();
    }

    @FXML
    private void toonEigenPlanning() {
        setTogglePlanning(eigenPlanningKnop);
        teamFilterBar.setVisible(false);
        teamFilterBar.setManaged(false);
        locatieDropdown.setValue(null);
        teamDropdown.getItems().clear();
        teamDropdown.setDisable(true);
        werknemerDropdown.getItems().clear();
        werknemerDropdown.setDisable(true);
        geselecteerdeWerknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
        laadData(geselecteerdeWerknemerId);
    }

    @FXML
    private void schakelTeamPlanning() {
        setTogglePlanning(teamPlanningKnop);
        teamFilterBar.setVisible(true);
        teamFilterBar.setManaged(true);
    }

    private void setTogglePlanning(Button actief) {
        for (Button b : List.of(eigenPlanningKnop, teamPlanningKnop)) {
            b.getStyleClass().removeAll("filter-knop", "filter-knop-actief");
            b.getStyleClass().add(b == actief ? "filter-knop-actief" : "filter-knop");
        }
    }

    private void laadData(int werknemerId) {
        LocalDate van = LocalDate.now().minusMonths(3);
        LocalDate tot = LocalDate.now().plusMonths(9);

        new Thread(() -> {
            try {
                List<AfwezigheidsOverzichtDTO> afwData = Beheerder.getInstance().getPlanningFacade().geefAfwezighedenVanTeam(werknemerId, van, tot);
                List<ShiftDTO> shiftData = Beheerder.getInstance().getShiftFacade().geefShiftenVanWerknemerBereik(werknemerId, van, tot);

                List<TaakDTO> alleTaken = Beheerder.getInstance().getTakenFacade().geefAlleTaken();

                List<TaakDTO> taakData = alleTaken.stream()
                    .filter(t -> t.werknemerIds() != null && t.werknemerIds().contains(werknemerId))
                    .filter(t -> !t.isAfgewerkt())
                    .toList();

                Platform.runLater(() -> {
                    this.afwezigheden = afwData;
                    this.taken = taakData;
                    this.shiften = shiftData;
                    teken();
                    toonDagDetail(huidigeDatum);
                });
            } catch (Exception e) {
                Platform.runLater(this::teken);
            }
        }).start();
    }
    private void teken() {
        switch (huidigeView) {
            case MAAND -> tekenMaand();
            case WEEK -> tekenWeek();
            case DAG -> tekenDag();
        }
    }

    // =========================================================================
    // RECHTER MUISKNOP: CONTEXT MENU TOEVOEGEN
    // =========================================================================
    private void voegRechterMuisknopToe(Region node, LocalDate datum) {
        // Alleen managers of admins mogen shifts aanmaken via de backend
        if (!Sessie.getInstance().isMangerOrAdmin()) return;

        ContextMenu contextMenu = new ContextMenu();
        MenuItem maakShiftItem = new MenuItem("Shift aanmaken op " + datum.format(DateTimeFormatter.ofPattern("dd/MM")));
        maakShiftItem.setOnAction(e -> openShiftDialog(datum));
        contextMenu.getItems().add(maakShiftItem);

        node.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(node, e.getScreenX(), e.getScreenY());
            } else if (e.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
                // Bestaande klik logica (selecteer dag)
                toonDagDetail(datum);
                if (node instanceof VBox && node.getStyleClass().contains("maand-cel")) {
                    if (geselecteerdeCel != null) geselecteerdeCel.getStyleClass().remove("maand-cel-vandaag");
                    node.getStyleClass().add("maand-cel-vandaag");
                    geselecteerdeCel = (VBox) node;
                }
            }
        });
    }

    // =========================================================================
    // DIALOG: 1. SHIFT AANMAKEN
    // =========================================================================
    private void openShiftDialog(LocalDate datum) {
        if (planningToggleBar.isVisible() && teamPlanningKnop.getStyleClass().contains("filter-knop-actief")) {
            if (geselecteerdeWerknemerId <= 0 || werknemerDropdown.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecteer eerst een werknemer in het team filter!");
                // Delaware styling ook op de alerts toepassen!
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/delaware-dialogs.css").toExternalForm());
                alert.show();
                return;
            }
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nieuwe Shift Inplannen");
        dialog.setHeaderText("Maak een shift voor: " + datum.format(DAG_FORMAT));

        // Koppel het nieuwe strakke Delaware thema aan de pop-up
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/planning.css").toExternalForm());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> startUur = maakTijdBox(0, 23, "09");
        ComboBox<String> startMin = maakTijdBox(0, 55, "00");
        ComboBox<String> eindUur = maakTijdBox(0, 23, "17");
        ComboBox<String> eindMin = maakTijdBox(0, 55, "00");

        ComboBox<String> pauzeStartUur = maakTijdBox(0, 23, "12");
        ComboBox<String> pauzeStartMin = maakTijdBox(0, 55, "00");
        ComboBox<String> pauzeEindUur = maakTijdBox(0, 23, "12");
        ComboBox<String> pauzeEindMin = maakTijdBox(0, 55, "30");

        grid.add(new Label("Starttijd:"), 0, 0);
        grid.add(new HBox(5, startUur, new Label(":"), startMin), 1, 0);

        grid.add(new Label("Eindtijd:"), 0, 1);
        grid.add(new HBox(5, eindUur, new Label(":"), eindMin), 1, 1);

        grid.add(new Label("Pauze van:"), 0, 2);
        grid.add(new HBox(5, pauzeStartUur, new Label(":"), pauzeStartMin), 1, 2);

        grid.add(new Label("Pauze tot:"), 0, 3);
        grid.add(new HBox(5, pauzeEindUur, new Label(":"), pauzeEindMin), 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                LocalTime start = LocalTime.parse(startUur.getValue() + ":" + startMin.getValue());
                LocalTime eind = LocalTime.parse(eindUur.getValue() + ":" + eindMin.getValue());
                LocalTime pStart = LocalTime.parse(pauzeStartUur.getValue() + ":" + pauzeStartMin.getValue());
                LocalTime pEind = LocalTime.parse(pauzeEindUur.getValue() + ":" + pauzeEindMin.getValue());

                new Thread(() -> {
                    try {
                        Beheerder.getInstance().getShiftFacade().maakShiftAan(
                            geselecteerdeWerknemerId, datum, start, eind, pStart, pEind
                        );

                        Platform.runLater(() -> {
                            laadData(geselecteerdeWerknemerId);
                            openTakenToewijzenDialog(datum);
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Fout bij opslaan: " + e.getMessage());
                            errorAlert.getDialogPane().getStylesheets().add(getClass().getResource("/css/planning.css").toExternalForm());
                            errorAlert.show();
                        });
                    }
                }).start();
            }
        });
    }

    private ComboBox<String> maakTijdBox(int min, int max, String def) {
        ComboBox<String> box = new ComboBox<>();
        for (int i = min; i <= max; i += (max == 55 ? 5 : 1)) box.getItems().add(String.format("%02d", i));
        box.setValue(def);
        return box;
    }

    private void openTakenToewijzenDialog(LocalDate datum) {
        new Thread(() -> {
            List<TaakDTO> alleTaken = Beheerder.getInstance().getTakenFacade().geefAlleTaken();

            List<TaakDTO> vrijeTaken = alleTaken.stream()
                .filter(t -> (t.werknemerIds() == null || t.werknemerIds().isEmpty()) && !t.isAfgewerkt())
                .collect(Collectors.toList());

            Platform.runLater(() -> {
                if (vrijeTaken.isEmpty()) {
                    new Alert(Alert.AlertType.INFORMATION, "Shift aangemaakt! Er zijn geen openstaande, onafgewerkte taken meer.").show();
                    return;
                }

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Zwevende Taken Inplannen");
                dialog.setHeaderText("Selecteer de taken en vul de verwachte werkuren in:");

                dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/planning.css").toExternalForm());

                VBox container = new VBox(12);

                // We slaan per checkbox op welke tijden erbij horen
                class TaakRijInfo {
                    CheckBox cb; ComboBox<String> startU; ComboBox<String> startM; ComboBox<String> eindU; ComboBox<String> eindM;
                }
                List<TaakRijInfo> rijen = new ArrayList<>();

                for (TaakDTO taak : vrijeTaken) {
                    TaakRijInfo info = new TaakRijInfo();

                    info.cb = new CheckBox(taak.naam());
                    info.cb.setUserData(taak.id());
                    info.cb.setPrefWidth(150);

                    info.startU = maakTijdBox(0, 23, "09"); info.startM = maakTijdBox(0, 55, "00");
                    info.eindU = maakTijdBox(0, 23, "10"); info.eindM = maakTijdBox(0, 55, "00");

                    HBox tijdBox = new HBox(4,
                        new Label(" Van "), info.startU, new Label(":"), info.startM,
                        new Label(" Tot "), info.eindU, new Label(":"), info.eindM
                    );
                    tijdBox.setAlignment(Pos.CENTER_LEFT);
                    tijdBox.setDisable(true); // Eerst uitschakelen

                    // Alleen tijd kunnen instellen als de checkbox is aangevinkt
                    info.cb.selectedProperty().addListener((obs, oud, nieuw) -> tijdBox.setDisable(!nieuw));

                    HBox rij = new HBox(10, info.cb, tijdBox);
                    rij.setAlignment(Pos.CENTER_LEFT);

                    rijen.add(info);
                    container.getChildren().add(rij);
                }

                ScrollPane scroll = new ScrollPane(container);
                scroll.setPrefHeight(250);
                dialog.getDialogPane().setContent(scroll);
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dialog.showAndWait().ifPresent(res -> {
                    if (res == ButtonType.OK) {
                        new Thread(() -> {
                            for (TaakRijInfo info : rijen) {
                                if (info.cb.isSelected()) {
                                    int taakId = (int) info.cb.getUserData();

                                    Beheerder.getInstance().getTakenFacade().wijsTaakToe(taakId, geselecteerdeWerknemerId);

                                    String startTijd = info.startU.getValue() + ":" + info.startM.getValue() + ":00";
                                    String eindTijd = info.eindU.getValue() + ":" + info.eindM.getValue() + ":00";
                                    Beheerder.getInstance().getTakenFacade().planTaakIn(taakId, datum.toString(), startTijd, eindTijd);
                                }
                            }

                            // DE FIX VOOR JE DATUM: Wacht een halve seconde tot MySQL klaar is met updaten!
                            try { Thread.sleep(500); } catch (InterruptedException ignored) {}

                            // Nu pas de data opnieuw ophalen, zodat hij de nieuwe 22/06 datum binnenhaalt
                            Platform.runLater(() -> laadData(geselecteerdeWerknemerId));
                        }).start();
                    }
                });
            });
        }).start();
    }


    // ─── MAAND & WEEK VIEW ──────────────────────────────
    private void tekenMaand() {
        kalenderContainer.getChildren().clear();
        periodeLabel.setText(huidigeDatum.format(MAAND_FORMAT));
        GridPane headerGrid = maakGrid(7);
        String[] dagen = {"ma", "di", "wo", "do", "vr", "za", "zo"};
        for (int i = 0; i < dagen.length; i++) {
            Label l = new Label(dagen[i]);
            l.getStyleClass().add("dag-header");
            l.setMaxWidth(Double.MAX_VALUE);
            l.setAlignment(Pos.CENTER);
            headerGrid.add(l, i, 0);
        }
        kalenderContainer.getChildren().add(headerGrid);

        GridPane celGrid = maakGrid(7);
        kalenderContainer.getChildren().add(celGrid);

        LocalDate eersteVanMaand = huidigeDatum.withDayOfMonth(1);
        int startKolom = eersteVanMaand.getDayOfWeek().getValue() - 1;
        int aantalDagen = huidigeDatum.lengthOfMonth();
        int rij = 0;
        int kolom = startKolom;

        for (int dag = 1; dag <= aantalDagen; dag++) {
            LocalDate datum = huidigeDatum.withDayOfMonth(dag);
            VBox cel = maakMaandCel(datum);
            voegRechterMuisknopToe(cel, datum); // <--- Context menu koppelen
            celGrid.add(cel, kolom, rij);
            kolom++;
            if (kolom == 7) { kolom = 0; rij++; }
        }
    }

    private GridPane maakGrid(int kolommen) {
        GridPane grid = new GridPane();
        grid.setHgap(4); grid.setVgap(4);
        for (int i = 0; i < kolommen; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / kolommen);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private VBox maakMaandCel(LocalDate datum) {
        VBox cel = new VBox(3);
        cel.getStyleClass().add("maand-cel");
        cel.setPadding(new Insets(8));
        cel.setMinHeight(80);

        if (datum.getDayOfWeek() == DayOfWeek.SATURDAY || datum.getDayOfWeek() == DayOfWeek.SUNDAY) {
            cel.getStyleClass().add("maand-cel-weekend");
        }
        if (datum.equals(huidigeDatum)) {
            cel.getStyleClass().add("maand-cel-vandaag");
            geselecteerdeCel = cel;
        }

        Label dagNummer = new Label(String.valueOf(datum.getDayOfMonth()));
        dagNummer.getStyleClass().add("dag-nummer");
        cel.getChildren().add(dagNummer);

        for (AfwezigheidsOverzichtDTO a : afwezighedenOpDag(datum)) {
            cel.getChildren().add(maakBadge(a));
        }
        for (TaakDTO taak : takenOpDag(datum)) {
            Label badge = new Label(taak.naam());
            badge.getStyleClass().add("badge-taak");
            badge.setMaxWidth(Double.MAX_VALUE);
            cel.getChildren().add(badge);
        }
        return cel;
    }

    private void tekenWeek() {
        kalenderContainer.getChildren().clear();
        LocalDate maandag = huidigeDatum.with(DayOfWeek.MONDAY);
        LocalDate zondag = maandag.plusDays(6);
        periodeLabel.setText(maandag.format(DAG_FORMAT) + " – " + zondag.format(DAG_FORMAT));

        HBox weekRij = new HBox(6);
        weekRij.setPrefHeight(300);

        for (int i = 0; i < 7; i++) {
            LocalDate datum = maandag.plusDays(i);
            VBox dagKolom = maakWeekDagKolom(datum);
            voegRechterMuisknopToe(dagKolom, datum); // <--- Context menu koppelen
            HBox.setHgrow(dagKolom, Priority.ALWAYS);
            weekRij.getChildren().add(dagKolom);
        }
        kalenderContainer.getChildren().add(weekRij);
    }

    private VBox maakWeekDagKolom(LocalDate datum) {
        VBox kolom = new VBox(6);
        kolom.getStyleClass().add("week-dag-kolom");
        kolom.setPadding(new Insets(8));

        String dagNaam = datum.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("nl", "BE"));
        Label header = new Label(dagNaam + " " + datum.getDayOfMonth());
        header.getStyleClass().add(datum.equals(LocalDate.now()) ? "week-dag-header-vandaag" : "week-dag-header");
        kolom.getChildren().add(header);

        List<AfwezigheidsOverzichtDTO> afwOpDag = afwezighedenOpDag(datum);
        List<TaakDTO> takenOpDag = takenOpDag(datum);

        if (afwOpDag.isEmpty() && takenOpDag.isEmpty()) {
            Label leeg = new Label("–");
            leeg.getStyleClass().add("week-leeg");
            kolom.getChildren().add(leeg);
        } else {
            for (AfwezigheidsOverzichtDTO a : afwOpDag) kolom.getChildren().add(maakWeekKaart(a));
            for (TaakDTO taak : takenOpDag) kolom.getChildren().add(maakWeekTaakKaart(taak));
        }
        return kolom;
    }

    private VBox maakWeekKaart(AfwezigheidsOverzichtDTO a) {
        VBox kaart = new VBox(2);
        boolean isWachten = a.status() != null && a.status().equals("In afwachting");
        kaart.getStyleClass().add(a.type().equals("Ziekte") ? "week-kaart-ziekte" : (isWachten ? "week-kaart-verlof-wachten" : "week-kaart-verlof"));
        kaart.setPadding(new Insets(6, 8, 6, 8));
        Label naam = new Label(a.voornaam() + " " + a.naam());
        naam.getStyleClass().add("week-kaart-naam");
        Label type = new Label(a.type().equals("Ziekte") ? "Ziekte" : (isWachten ? "Verlof (wachten)" : "Verlof"));
        type.getStyleClass().add("week-kaart-type");
        kaart.getChildren().addAll(naam, type);
        return kaart;
    }

    private VBox maakWeekTaakKaart(TaakDTO taak) {
        VBox kaart = new VBox(2);
        kaart.getStyleClass().add("week-kaart-taak");
        kaart.setPadding(new Insets(6, 8, 6, 8));
        Label naam = new Label(taak.naam());
        naam.getStyleClass().add("week-kaart-naam");
        Label type = new Label("Taak");
        type.getStyleClass().add("week-kaart-type");
        kaart.getChildren().addAll(naam, type);
        return kaart;
    }

    // ─── HULP KLASSE VOOR OVERLAPPENDE TAKEN ────────────────────────────────
    private static class TaakOverlapInfo {
        TaakDTO taak;
        VBox node;
        LocalTime start;
        LocalTime eind;
        int colIndex = 0;
        int maxCols = 1;

        public TaakOverlapInfo(TaakDTO taak, VBox node, LocalTime start, LocalTime eind) {
            this.taak = taak;
            this.node = node;
            this.start = start;
            this.eind = eind;
        }
    }

    // ─── DAG VIEW ──────────────────────────────────
    private void tekenDag() {
        kalenderContainer.getChildren().clear();
        periodeLabel.setText(huidigeDatum.format(DAG_FORMAT));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("dag-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        GridPane timeGrid = new GridPane();
        timeGrid.getStyleClass().add("time-grid");

        // Voeg ook hier de rechtermuisknop toe!
        voegRechterMuisknopToe(timeGrid, huidigeDatum);

        ColumnConstraints timeCol = new ColumnConstraints(60);
        ColumnConstraints contentCol = new ColumnConstraints();
        contentCol.setHgrow(Priority.ALWAYS);
        timeGrid.getColumnConstraints().addAll(timeCol, contentCol);

        int startUur = 7;
        int eindUur = 20;
        int rijenPerUur = 4; // 15 minuten per rij
        int totaleRijen = (eindUur - startUur) * rijenPerUur;

        for (int i = 0; i <= totaleRijen; i++) {
            timeGrid.getRowConstraints().add(new RowConstraints(15));
            if (i % rijenPerUur == 0 && i < totaleRijen) {
                Label timeLabel = new Label(String.format("%02d:00", startUur + (i / rijenPerUur)));
                timeLabel.getStyleClass().add("time-label");
                timeLabel.setAlignment(Pos.TOP_RIGHT);
                timeLabel.setPadding(new Insets(0, 8, 0, 0));
                timeGrid.add(timeLabel, 0, i, 1, rijenPerUur);

                Region hourLine = new Region();
                hourLine.getStyleClass().add("hour-line");
                timeGrid.add(hourLine, 1, i);
            } else if (i % 2 == 0) {
                Region halfHourLine = new Region();
                halfHourLine.getStyleClass().add("half-hour-line");
                timeGrid.add(halfHourLine, 1, i);
            }
        }

        Pane itemLayer = new Pane();
        timeGrid.add(itemLayer, 1, 0, 1, totaleRijen);

        List<AfwezigheidsOverzichtDTO> afwezighedenOpDag = afwezighedenOpDag(huidigeDatum);

        if (!afwezighedenOpDag.isEmpty()) {
            for (AfwezigheidsOverzichtDTO a : afwezighedenOpDag) {
                VBox afwezigBlock = maakAfwezigheidsBlok(a);
                afwezigBlock.setLayoutY(0);
                afwezigBlock.setPrefHeight(totaleRijen * 15);
                afwezigBlock.prefWidthProperty().bind(itemLayer.widthProperty().subtract(10));
                itemLayer.getChildren().add(afwezigBlock);
            }
        } else {
            for (ShiftDTO shift : shiftenOpDag(huidigeDatum)) {
                VBox shiftBlock = maakTijdsBlok(shift, startUur, rijenPerUur);
                if (shiftBlock != null) {
                    shiftBlock.prefWidthProperty().bind(itemLayer.widthProperty().subtract(10));
                    itemLayer.getChildren().add(shiftBlock);
                }
            }

            List<TaakOverlapInfo> taakItems = new ArrayList<>();
            for (TaakDTO taak : takenOpDag(huidigeDatum)) {
                if (taak.startuur() == null || taak.einduur() == null) continue;
                try {
                    LocalTime start = LocalTime.parse(taak.startuur());
                    LocalTime eind = LocalTime.parse(taak.einduur());
                    VBox node = maakTaakBlok(taak, startUur, rijenPerUur);
                    if (node != null) {
                        taakItems.add(new TaakOverlapInfo(taak, node, start, eind));
                    }
                } catch (Exception e) {}
            }

            taakItems.sort(Comparator.comparing(t -> t.start));

            List<List<TaakOverlapInfo>> clusters = new ArrayList<>();
            List<TaakOverlapInfo> huidigCluster = new ArrayList<>();
            LocalTime clusterEinde = LocalTime.MIN;

            for (TaakOverlapInfo item : taakItems) {
                if (huidigCluster.isEmpty()) {
                    huidigCluster.add(item);
                    clusterEinde = item.eind;
                } else {
                    if (item.start.compareTo(clusterEinde) < 0) {
                        huidigCluster.add(item);
                        if (item.eind.compareTo(clusterEinde) > 0) clusterEinde = item.eind;
                    } else {
                        clusters.add(huidigCluster);
                        huidigCluster = new ArrayList<>();
                        huidigCluster.add(item);
                        clusterEinde = item.eind;
                    }
                }
            }
            if (!huidigCluster.isEmpty()) clusters.add(huidigCluster);

            for (List<TaakOverlapInfo> cluster : clusters) {
                List<LocalTime> kolommen = new ArrayList<>();
                for (TaakOverlapInfo item : cluster) {
                    boolean geplaatst = false;
                    for (int i = 0; i < kolommen.size(); i++) {
                        if (kolommen.get(i).compareTo(item.start) <= 0) {
                            item.colIndex = i;
                            kolommen.set(i, item.eind);
                            geplaatst = true;
                            break;
                        }
                    }
                    if (!geplaatst) {
                        item.colIndex = kolommen.size();
                        kolommen.add(item.eind);
                    }
                }

                for (TaakOverlapInfo item : cluster) {
                    item.maxCols = kolommen.size();
                    NumberBinding baseWidth = itemLayer.widthProperty().subtract(70);
                    NumberBinding taskWidth = baseWidth.divide(item.maxCols);
                    item.node.layoutXProperty().bind(taskWidth.multiply(item.colIndex).add(60));
                    item.node.prefWidthProperty().bind(taskWidth.subtract(4));
                    itemLayer.getChildren().add(item.node);
                }
            }
        }

        scrollPane.setContent(timeGrid);
        kalenderContainer.getChildren().add(scrollPane);
    }

    private VBox maakTijdsBlok(ShiftDTO shift, int kalenderStartUur, int rijenPerUur) {
        if (shift.startTijd() == null || shift.eindTijd() == null) return null;
        try {
            LocalTime startTijd = shift.startTijd();
            LocalTime eindTijd = shift.eindTijd();
            int startMin = (startTijd.getHour() - kalenderStartUur) * 60 + startTijd.getMinute();
            double yPos = (startMin / 15.0) * 15;
            int duur = (int) java.time.Duration.between(startTijd, eindTijd).toMinutes();
            double hoogte = (duur / 15.0) * 15;

            VBox blok = new VBox(2);
            blok.getStyleClass().add("time-block-shift");
            blok.setLayoutY(yPos);
            blok.setPrefHeight(hoogte);

            Label titel = new Label("Shift: " + startTijd + " - " + eindTijd);
            titel.getStyleClass().add("time-block-title");
            Label details = new Label("Klik voor details...");
            details.getStyleClass().add("time-block-details");
            blok.getChildren().addAll(titel, details);

            blok.setOnMouseClicked(e -> {
                blok.getParent().getChildrenUnmodifiable().forEach(node -> node.getStyleClass().remove("time-block-selected"));
                blok.getStyleClass().add("time-block-selected");

                if (ShiftDetailTitle != null) ShiftDetailTitle.setText("Shift: " + startTijd + " - " + eindTijd);
                if (shiftdetails != null) {
                    shiftdetails.getChildren().clear();
                    shiftdetails.getChildren().add(new Label("Werknemer: " + (shift.werknemerNaam() != null ? shift.werknemerNaam() : "Onbekend")));
                    if(shift.pauzeStart() != null && shift.pauzeEind() != null){
                        shiftdetails.getChildren().add(new Label("Pauze: " + shift.pauzeStart() + " - " + shift.pauzeEind()));
                    }
                }
            });
            return blok;
        } catch (Exception e) { return null; }
    }

    private VBox maakTaakBlok(TaakDTO taak, int kalenderStartUur, int rijenPerUur) {
        if (taak.startuur() == null || taak.einduur() == null) return null;
        try {
            LocalTime startTijd = LocalTime.parse(taak.startuur());
            LocalTime eindTijd = LocalTime.parse(taak.einduur());

            int startMin = (startTijd.getHour() - kalenderStartUur) * 60 + startTijd.getMinute();
            double yPos = (startMin / 15.0) * 15;
            int duur = (int) java.time.Duration.between(startTijd, eindTijd).toMinutes();
            double hoogte = (duur / 15.0) * 15;

            VBox blok = new VBox(2);
            blok.getStyleClass().add("time-block-taak");
            blok.setLayoutY(yPos);
            blok.setPrefHeight(hoogte);

            Label titel = new Label("Taak: " + taak.naam());
            titel.getStyleClass().add("time-block-title");

            Label tijdLabel = new Label(startTijd + " - " + eindTijd);
            tijdLabel.getStyleClass().add("time-block-details");

            blok.getChildren().addAll(titel, tijdLabel);

            blok.setOnMouseClicked(e -> {
                blok.getParent().getChildrenUnmodifiable().forEach(node -> node.getStyleClass().remove("time-block-selected"));
                blok.getStyleClass().add("time-block-selected");

                if (ShiftDetailTitle != null) ShiftDetailTitle.setText("Taak: " + taak.naam());
                if (shiftdetails != null) {
                    shiftdetails.getChildren().clear();
                    shiftdetails.getChildren().add(new Label("Uren: " + startTijd + " - " + eindTijd));
                    if (taak.locatie() != null) shiftdetails.getChildren().add(new Label("Locatie: " + taak.locatie()));
                    if (taak.specificaties() != null) {
                        Label spec = new Label("Info: " + taak.specificaties());
                        spec.setWrapText(true);
                        shiftdetails.getChildren().add(spec);
                    }
                }
            });
            return blok;
        } catch (Exception e) { return null; }
    }

    private VBox maakAfwezigheidsBlok(AfwezigheidsOverzichtDTO a) {
        VBox blok = new VBox(5);
        boolean isZiekte = "Ziekte".equalsIgnoreCase(a.type());
        blok.getStyleClass().add(isZiekte ? "time-block-ziek" : "time-block-verlof");
        Label titel = new Label(isZiekte ? "Afwezig: Ziekte" : "Afwezig: Verlof");
        titel.getStyleClass().add("time-block-title-large");
        Label detail = new Label(a.status() != null ? "Status: " + a.status() : "");
        blok.getChildren().addAll(titel, detail);
        return blok;
    }

    private void toonDagDetail(LocalDate datum) {
        List<AfwezigheidsOverzichtDTO> opDag = afwezighedenOpDag(datum);

        if (detailTitel != null) detailTitel.setText(datum.format(DAG_FORMAT));
        if (detailLijst != null) detailLijst.getChildren().clear();

        if (opDag.isEmpty()) {
            Label leeg = new Label("Geen afwezigheden.");
            leeg.getStyleClass().add("leeg-label");
            if (detailLijst != null) detailLijst.getChildren().add(leeg);
        } else {
            for (AfwezigheidsOverzichtDTO a : opDag) if (detailLijst != null) detailLijst.getChildren().add(maakDetailRij(a));
        }

        if (shiftOverview != null) {
            shiftOverview.getChildren().clear();
            List<ShiftDTO> shiftenVandaag = shiftenOpDag(datum);
            List<TaakDTO> takenVandaag = takenOpDag(datum);

            if(shiftenVandaag.isEmpty() && takenVandaag.isEmpty()){
                Label leeg = new Label("Geen geplande items vandaag.");
                leeg.getStyleClass().add("leeg-label");
                shiftOverview.getChildren().add(leeg);
            } else {
                for(ShiftDTO s : shiftenVandaag) shiftOverview.getChildren().add(new Label("Shift: " + s.startTijd() + " - " + s.eindTijd()));
                for(TaakDTO t : takenVandaag) shiftOverview.getChildren().add(new Label("Taak: " + t.naam()));
            }
        }

        if (ShiftDetailTitle != null) ShiftDetailTitle.setText("Details");
        if (shiftdetails != null) {
            shiftdetails.getChildren().clear();
            Label leeg = new Label("Selecteer een blok in de agenda");
            leeg.getStyleClass().add("leeg-label");
            shiftdetails.getChildren().add(leeg);
        }
    }

    private HBox maakDetailRij(AfwezigheidsOverzichtDTO a) {
        HBox rij = new HBox(12);
        boolean isWachten = a.status() != null && a.status().equals("In afwachting");
        rij.getStyleClass().add(a.type().equals("Ziekte") ? "detail-rij-ziekte" : (isWachten ? "detail-rij-verlof-wachten" : "detail-rij-verlof"));
        rij.setAlignment(Pos.CENTER_LEFT);
        rij.setPadding(new Insets(10, 14, 10, 14));
        Label icon = new Label(a.type().equals("Ziekte") ? "Z" : "V");
        icon.setStyle("-fx-font-size: 18px;");
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label naam = new Label(a.voornaam() + " " + a.naam());
        naam.getStyleClass().add("detail-naam");
        String datumTekst = a.startDatum().equals(a.eindDatum()) ? a.startDatum().format(DAG_FORMAT) : a.startDatum().format(DAG_FORMAT) + " – " + a.eindDatum().format(DAG_FORMAT);
        Label datumLabel = new Label(datumTekst);
        datumLabel.getStyleClass().add("detail-datum");
        info.getChildren().addAll(naam, datumLabel);
        if (a.status() != null) {
            Label statusLabel = new Label(a.status());
            statusLabel.getStyleClass().add(a.status().equals("Goedgekeurd") ? "badge-goedgekeurd" : "badge-wachten");
            rij.getChildren().addAll(icon, info, statusLabel);
        } else {
            rij.getChildren().addAll(icon, info);
        }
        return rij;
    }

    private List<AfwezigheidsOverzichtDTO> afwezighedenOpDag(LocalDate datum) {
        return afwezigheden.stream().filter(a -> !datum.isBefore(a.startDatum()) && !datum.isAfter(a.eindDatum())).toList();
    }

    private List<TaakDTO> takenOpDag(LocalDate datum) {
        return taken.stream().filter(t -> {
            if (t.deadline() == null || t.deadline().isBlank()) return false;
            try { return LocalDate.parse(t.deadline().substring(0, 10)).equals(datum); }
            catch (Exception e) { return false; }
        }).toList();
    }

    private List<ShiftDTO> shiftenOpDag(LocalDate datum) {
        return shiften.stream().filter(s -> s.startDatum() != null && s.eindDatum() != null && !datum.isBefore(s.startDatum()) && !datum.isAfter(s.eindDatum())).toList();
    }

    private Label maakBadge(AfwezigheidsOverzichtDTO a) {
        boolean isWachten = a.status() != null && a.status().equals("In afwachting");
        Label badge = new Label(a.voornaam() + (a.type().equals("Ziekte") ? " (Z)" : " (V)"));
        badge.getStyleClass().add(a.type().equals("Ziekte") ? "badge-ziekte" : (isWachten ? "badge-verlof-wachten" : "badge-verlof"));
        badge.setMaxWidth(Double.MAX_VALUE);
        return badge;
    }

    @FXML private void vorigePeriode() {
        switch (huidigeView) { case MAAND -> huidigeDatum = huidigeDatum.minusMonths(1); case WEEK -> huidigeDatum = huidigeDatum.minusWeeks(1); case DAG -> huidigeDatum = huidigeDatum.minusDays(1); }
        teken(); toonDagDetail(huidigeDatum);
    }
    @FXML private void volgendePeriode() {
        switch (huidigeView) { case MAAND -> huidigeDatum = huidigeDatum.plusMonths(1); case WEEK -> huidigeDatum = huidigeDatum.plusWeeks(1); case DAG -> huidigeDatum = huidigeDatum.plusDays(1); }
        teken(); toonDagDetail(huidigeDatum);
    }
    @FXML private void naarVandaag() { huidigeDatum = LocalDate.now(); teken(); toonDagDetail(huidigeDatum); }
    @FXML private void toonMaand() { huidigeView = View.MAAND; setToggle(maandKnop); teken(); }
    @FXML private void toonWeek() { huidigeView = View.WEEK; setToggle(weekKnop); teken(); }
    @FXML private void toonDag() { huidigeView = View.DAG; setToggle(dagKnop); teken(); }

    private void setToggle(Button actief) {
        for (Button b : List.of(maandKnop, weekKnop, dagKnop)) {
            b.getStyleClass().removeAll("filter-knop", "filter-knop-actief");
            b.getStyleClass().add(b == actief ? "filter-knop-actief" : "filter-knop");
        }
    }
}
