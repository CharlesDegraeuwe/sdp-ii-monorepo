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

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                List<AfwezigheidsOverzichtDTO> afwData = Beheerder.getInstance()
                        .getPlanningFacade()
                        .geefAfwezighedenVanTeam(werknemerId, van, tot);
                List<TaakDTO> taakData = Beheerder.getInstance()
                        .getTakenFacade()
                        .geefTakenVanWerknemer(werknemerId);
                List<ShiftDTO> shiftData = Beheerder.getInstance()
                        .getShiftFacade()
                        .geefShiftenVanWerknemerBereik(werknemerId, van, tot);

                Platform.runLater(() -> {
                    this.afwezigheden = afwData;
                    this.taken = taakData;
                    this.shiften = shiftData;
                    teken();
                    toonDagDetail(huidigeDatum);
                });
            } catch (Exception e) {
                e.printStackTrace();
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

    // ─── MAAND VIEW ──────────────────────────────────────────────────────────────

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
            celGrid.add(cel, kolom, rij);

            kolom++;
            if (kolom == 7) {
                kolom = 0;
                rij++;
            }
        }
    }

    private GridPane maakGrid(int kolommen) {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
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
            badge.setWrapText(false);
            cel.getChildren().add(badge);
        }

        cel.setOnMouseClicked(e -> {
            if (geselecteerdeCel != null) {
                geselecteerdeCel.getStyleClass().remove("maand-cel-vandaag");
            }
            toonDagDetail(datum);
            cel.getStyleClass().add("maand-cel-vandaag");
            geselecteerdeCel = cel;
        });
        return cel;
    }

    // ─── WEEK VIEW ───────────────────────────────────────────────────────────────

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
            for (AfwezigheidsOverzichtDTO a : afwOpDag) {
                kolom.getChildren().add(maakWeekKaart(a));
            }
            for (TaakDTO taak : takenOpDag) {
                kolom.getChildren().add(maakWeekTaakKaart(taak));
            }
        }

        kolom.setOnMouseClicked(e -> toonDagDetail(datum));
        return kolom;
    }

    private VBox maakWeekKaart(AfwezigheidsOverzichtDTO a) {
        VBox kaart = new VBox(2);
        boolean isWachten = a.status() != null && a.status().equals("In afwachting");
        String stijl = a.type().equals("Ziekte") ? "week-kaart-ziekte"
                : (isWachten ? "week-kaart-verlof-wachten" : "week-kaart-verlof");
        kaart.getStyleClass().add(stijl);
        kaart.setPadding(new Insets(6, 8, 6, 8));

        Label naam = new Label(a.voornaam() + " " + a.naam());
        naam.getStyleClass().add("week-kaart-naam");
        Label type = new Label(a.type().equals("Ziekte") ? "Ziekte"
                : (isWachten ? "Verlof (wachten)" : "Verlof"));
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

    // ─── DAG VIEW ────────────────────────────────────────────────────────────────

    private void tekenDag() {
        kalenderContainer.getChildren().clear();

        periodeLabel.setText(huidigeDatum.format(DAG_FORMAT));

        VBox dagView = new VBox(10);
        dagView.setPadding(new Insets(10));
        dagView.getStyleClass().add("dag-view");

        List<ShiftDTO> shiftenOpDag = shiftenOpDag(huidigeDatum);
        if (!shiftenOpDag.isEmpty()) {
            Label titel = new Label("Shift");
            titel.getStyleClass().add("dag-sectie-titel");
            dagView.getChildren().add(titel);
            for (ShiftDTO shift : shiftenOpDag) {
                dagView.getChildren().add(maakShiftKaart(shift));
            }
        }

        List<AfwezigheidsOverzichtDTO> opDag = afwezighedenOpDag(huidigeDatum);
        if (!opDag.isEmpty()) {
            Label titel = new Label("Afwezigheden");
            titel.getStyleClass().add("dag-sectie-titel");
            dagView.getChildren().add(titel);
            for (AfwezigheidsOverzichtDTO a : opDag) {
                dagView.getChildren().add(maakDetailRij(a));
            }
        }

        if (shiftenOpDag.isEmpty() && opDag.isEmpty()) {
            Label leeg = new Label("Geen planning op deze dag.");
            leeg.getStyleClass().add("leeg-label");
            dagView.getChildren().add(leeg);
        }

        kalenderContainer.getChildren().add(dagView);
    }

    private HBox maakShiftKaart(ShiftDTO shift) {
        HBox kaart = new HBox(12);
        kaart.getStyleClass().add("shift-item");
        kaart.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label tijd = new Label(shift.startTijd() + " – " + shift.eindTijd());
        tijd.getStyleClass().add("shift-tijd");
        info.getChildren().add(tijd);

        if (shift.pauzeStart() != null && shift.pauzeEind() != null) {
            Label pauze = new Label("Pauze: " + shift.pauzeStart() + " – " + shift.pauzeEind());
            pauze.getStyleClass().add("shift-locatie");
            info.getChildren().add(pauze);
        }

        kaart.getChildren().add(info);
        return kaart;
    }

    // ─── DAG DETAIL ──────────────────────────────────────────────────────────────

    private void toonDagDetail(LocalDate datum) {
        List<AfwezigheidsOverzichtDTO> opDag = afwezighedenOpDag(datum);

        detailTitel.setText(datum.format(DAG_FORMAT));
        detailLijst.getChildren().clear();

        if (opDag.isEmpty()) {
            Label leeg = new Label("Geen afwezigheden.");
            leeg.getStyleClass().add("leeg-label");
            detailLijst.getChildren().add(leeg);
        } else {
            for (AfwezigheidsOverzichtDTO a : opDag) {
                detailLijst.getChildren().add(maakDetailRij(a));
            }
        }
    }

    private HBox maakDetailRij(AfwezigheidsOverzichtDTO a) {
        HBox rij = new HBox(12);
        boolean isWachten = a.status() != null && a.status().equals("In afwachting");
        String stijl = a.type().equals("Ziekte") ? "detail-rij-ziekte"
                : (isWachten ? "detail-rij-verlof-wachten" : "detail-rij-verlof");
        rij.getStyleClass().add(stijl);
        rij.setAlignment(Pos.CENTER_LEFT);
        rij.setPadding(new Insets(10, 14, 10, 14));

        Label icon = new Label(a.type().equals("Ziekte") ? "Z" : "V");
        icon.setStyle("-fx-font-size: 18px;");

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label naam = new Label(a.voornaam() + " " + a.naam());
        naam.getStyleClass().add("detail-naam");
        String datumTekst = a.startDatum().equals(a.eindDatum())
                ? a.startDatum().format(DAG_FORMAT)
                : a.startDatum().format(DAG_FORMAT) + " – " + a.eindDatum().format(DAG_FORMAT);
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

    // ─── HELPERS ─────────────────────────────────────────────────────────────────

    private List<AfwezigheidsOverzichtDTO> afwezighedenOpDag(LocalDate datum) {
        return afwezigheden.stream()
                .filter(a -> !datum.isBefore(a.startDatum()) && !datum.isAfter(a.eindDatum()))
                .toList();
    }

    private List<TaakDTO> takenOpDag(LocalDate datum) {
        return taken.stream()
                .filter(t -> {
                    if (t.deadline() == null || t.deadline().isBlank()) return false;
                    try {
                        LocalDate dl = LocalDate.parse(t.deadline().substring(0, 10));
                        return dl.equals(datum);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();
    }

    private List<ShiftDTO> shiftenOpDag(LocalDate datum) {
        return shiften.stream()
                .filter(s -> s.startDatum() != null && s.eindDatum() != null
                        && !datum.isBefore(s.startDatum()) && !datum.isAfter(s.eindDatum()))
                .toList();
    }

    private Label maakBadge(AfwezigheidsOverzichtDTO a) {
        boolean isWachten = a.status() != null && a.status().equals("In afwachting");
        String tekst = a.voornaam() + (a.type().equals("Ziekte") ? " (Z)" : " (V)");
        Label badge = new Label(tekst);
        String stijl = a.type().equals("Ziekte") ? "badge-ziekte"
                : (isWachten ? "badge-verlof-wachten" : "badge-verlof");
        badge.getStyleClass().add(stijl);
        badge.setMaxWidth(Double.MAX_VALUE);
        badge.setWrapText(false);
        return badge;
    }

    // ─── NAVIGATIE ───────────────────────────────────────────────────────────────

    @FXML private void vorigePeriode() {
        switch (huidigeView) {
            case MAAND -> huidigeDatum = huidigeDatum.minusMonths(1);
            case WEEK -> huidigeDatum = huidigeDatum.minusWeeks(1);
            case DAG -> huidigeDatum = huidigeDatum.minusDays(1);
        }
        teken();
    }

    @FXML private void volgendePeriode() {
        switch (huidigeView) {
            case MAAND -> huidigeDatum = huidigeDatum.plusMonths(1);
            case WEEK -> huidigeDatum = huidigeDatum.plusWeeks(1);
            case DAG -> huidigeDatum = huidigeDatum.plusDays(1);
        }
        teken();
    }

    @FXML private void naarVandaag() {
        huidigeDatum = LocalDate.now();
        teken();
    }

    @FXML private void toonMaand() {
        huidigeView = View.MAAND;
        setToggle(maandKnop);
        teken();
    }

    @FXML private void toonWeek() {
        huidigeView = View.WEEK;
        setToggle(weekKnop);
        teken();
    }

    @FXML private void toonDag() {
        huidigeView = View.DAG;
        setToggle(dagKnop);
        teken();
    }

    private void setToggle(Button actief) {
        for (Button b : List.of(maandKnop, weekKnop, dagKnop)) {
            b.getStyleClass().removeAll("filter-knop", "filter-knop-actief");
            b.getStyleClass().add(b == actief ? "filter-knop-actief" : "filter-knop");
        }
    }
}
