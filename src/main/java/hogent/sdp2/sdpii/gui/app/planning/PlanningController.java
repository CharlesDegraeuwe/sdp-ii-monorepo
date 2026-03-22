package hogent.sdp2.sdpii.gui.app.planning;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.AfwezigheidsOverzichtDTO;
import domain.dto.WerknemerDTO;
import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PlanningController extends BorderPane {

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
    private VBox geselecteerdeCel;

    private enum View { MAAND, WEEK, DAG }
    private View huidigeView = View.MAAND;
    private LocalDate huidigeDatum = LocalDate.now();
    private List<AfwezigheidsOverzichtDTO> afwezigheden = List.of();

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
        toonDagDetail(LocalDate.now());
        laadAfwezigheden();
    }

    private void laadAfwezigheden() {
        WerknemerDTO werknemer = Sessie.getInstance().getIngelogdeWerknemer();
        if (werknemer == null) return;

        LocalDate van = LocalDate.now().minusMonths(3);
        LocalDate tot = LocalDate.now().plusMonths(9);

        new Thread(() -> {
            try {
                List<AfwezigheidsOverzichtDTO> data = Beheerder.getInstance()
                        .getPlanningFacade()
                        .geefAfwezighedenVanTeam(werknemer.id(), van, tot);
                Platform.runLater(() -> {
                    this.afwezigheden = data;
                    teken();
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

        if (datum.equals(LocalDate.now())) {
            cel.getStyleClass().add("maand-cel-vandaag");
            geselecteerdeCel = cel;
        }

        Label dagNummer = new Label(String.valueOf(datum.getDayOfMonth()));
        dagNummer.getStyleClass().add("dag-nummer");
        cel.getChildren().add(dagNummer);

        List<AfwezigheidsOverzichtDTO> opDag = afwezighedenOpDag(datum);
        for (AfwezigheidsOverzichtDTO a : opDag) {
            cel.getChildren().add(maakBadge(a));
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

        List<AfwezigheidsOverzichtDTO> opDag = afwezighedenOpDag(datum);
        if (opDag.isEmpty()) {
            Label leeg = new Label("–");
            leeg.getStyleClass().add("week-leeg");
            kolom.getChildren().add(leeg);
        } else {
            for (AfwezigheidsOverzichtDTO a : opDag) {
                kolom.getChildren().add(maakWeekKaart(a));
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

    // ─── DAG VIEW ────────────────────────────────────────────────────────────────

    private void tekenDag() {
        kalenderContainer.getChildren().clear();

        periodeLabel.setText(huidigeDatum.format(DAG_FORMAT));

        List<AfwezigheidsOverzichtDTO> opDag = afwezighedenOpDag(huidigeDatum);

        VBox dagView = new VBox(10);
        dagView.setPadding(new Insets(10));
        dagView.getStyleClass().add("dag-view");

        if (opDag.isEmpty()) {
            Label leeg = new Label("Geen afwezigheden op deze dag.");
            leeg.getStyleClass().add("leeg-label");
            dagView.getChildren().add(leeg);
        } else {
            for (AfwezigheidsOverzichtDTO a : opDag) {
                dagView.getChildren().add(maakDetailRij(a));
            }
        }

        kalenderContainer.getChildren().add(dagView);
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