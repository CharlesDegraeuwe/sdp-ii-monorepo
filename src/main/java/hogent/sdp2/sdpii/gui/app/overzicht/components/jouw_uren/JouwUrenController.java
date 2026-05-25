package hogent.sdp2.sdpii.gui.app.overzicht.components.jouw_uren;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.ShiftDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JouwUrenController extends VBox {

    @FXML VBox dagContainer;

    private static final LocalTime STANDAARD_START = LocalTime.of(9, 0);
    private static final LocalTime STANDAARD_EIND  = LocalTime.of(17, 0);

    public JouwUrenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/jouw_uren/JouwUren.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        laadData();
    }

    private void laadData() {
        if (Sessie.getInstance().getIngelogdeWerknemer() == null) return;
        int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
        LocalDate vandaag = LocalDate.now();
        LocalDate tot = vandaag.plusDays(6);
        Set<LocalDate> feestdagen = berekenFeestdagen(vandaag.getYear());
        if (vandaag.getYear() != tot.getYear()) {
            feestdagen.addAll(berekenFeestdagen(tot.getYear()));
        }

        new Thread(() -> {
            try {
                List<ShiftDTO> shifts = Beheerder.getInstance()
                        .getShiftFacade()
                        .geefShiftenVanWerknemerBereik(werknemerId, vandaag, tot);

                Map<LocalDate, ShiftDTO> shiftPerDag = new HashMap<>();
                for (ShiftDTO s : shifts) {
                    if (s.startDatum() == null) continue;
                    LocalDate cur = s.startDatum();
                    while (!cur.isAfter(s.eindDatum() != null ? s.eindDatum() : s.startDatum())) {
                        if (!cur.isBefore(vandaag) && !cur.isAfter(tot)) {
                            shiftPerDag.putIfAbsent(cur, s);
                        }
                        cur = cur.plusDays(1);
                    }
                }

                final Set<LocalDate> fd = feestdagen;
                Platform.runLater(() -> toonDagen(vandaag, shiftPerDag, fd));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void toonDagen(LocalDate vandaag, Map<LocalDate, ShiftDTO> shiftPerDag, Set<LocalDate> feestdagen) {
        dagContainer.getChildren().clear();
        for (int i = 0; i < 7; i++) {
            LocalDate datum = vandaag.plusDays(i);
            ShiftDTO shift = shiftPerDag.get(datum);
            dagContainer.getChildren().add(maakDagRij(datum, shift, feestdagen));
        }
    }

    private VBox maakDagRij(LocalDate datum, ShiftDTO shift, Set<LocalDate> feestdagen) {
        boolean isVandaag   = datum.equals(LocalDate.now());
        boolean isFeestdag  = feestdagen.contains(datum);
        boolean isWeekend   = datum.getDayOfWeek() == DayOfWeek.SATURDAY || datum.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isVrij      = (isFeestdag || isWeekend) && shift == null;
        boolean isAfwijkend = shift != null && isAfwijkend(shift);

        // Top row: day name + date
        Label lblDag = new Label(dagNaam(datum.getDayOfWeek()));
        lblDag.setStyle(isVandaag
                ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;"
                : "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6B7280;");

        Label lblDatum = new Label(datum.getDayOfMonth() + " " + maandNaam(datum.getMonth()));
        lblDatum.setStyle("-fx-font-size: 10px; -fx-text-fill: #9CA3AF;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topRow = new HBox(lblDag, spacer, lblDatum);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Bottom row: vrij or shift
        HBox bottomRow = new HBox(4);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        if (isVrij) {
            Label lbl = new Label("Vrij — " + (isFeestdag ? "Feestdag" : "Weekend"));
            lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #9CA3AF; -fx-font-style: italic;");
            bottomRow.getChildren().add(lbl);
        } else {
            Label lblTag = new Label("SHIFT");
            lblTag.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");

            String start = formatTijd(shift != null ? shift.startTijd() : null, "09:00");
            String eind  = formatTijd(shift != null ? shift.eindTijd()  : null, "17:00");
            Label lblTijd = new Label(start + " – " + eind);
            lblTijd.setStyle(isAfwijkend
                    ? "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #B91C1C;"
                    : "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #374151;");

            bottomRow.getChildren().addAll(lblTag, lblTijd);
        }

        VBox rij = new VBox(4, topRow, bottomRow);
        rij.setPadding(new Insets(8, 10, 8, 10));
        rij.setMaxWidth(Double.MAX_VALUE);
        rij.setCursor(Cursor.HAND);

        String achtergrond;
        String rand;
        if (isVandaag) {
            achtergrond = "#EFF6FF";
            rand        = "#BFDBFE";
        } else if (isFeestdag) {
            achtergrond = "#FEFCE8";
            rand        = "#FDE68A";
        } else {
            achtergrond = "#F9FAFB";
            rand        = "#E5E7EB";
        }
        rij.setStyle(String.format(
                "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;",
                achtergrond, rand));

        rij.setOnMouseClicked(e -> {
            hogent.sdp2.sdpii.gui.app.planning.PlanningController.startDatumVanuitDashboard = datum;
            hogent.sdp2.sdpii.gui.router.Router.getInstance()
                    .navigeerNaar(hogent.sdp2.sdpii.gui.router.Scherm.PLANNING);
        });
        rij.setOnMouseEntered(e -> rij.setStyle(String.format(
                "-fx-background-color: derive(%s, -5%%); -fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;",
                achtergrond, rand)));
        rij.setOnMouseExited(e -> rij.setStyle(String.format(
                "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;",
                achtergrond, rand)));

        return rij;
    }

    private boolean isAfwijkend(ShiftDTO shift) {
        if (shift.startTijd() == null || shift.eindTijd() == null) return false;
        LocalTime start = shift.startTijd().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        LocalTime eind  = shift.eindTijd().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        return !start.equals(STANDAARD_START) || !eind.equals(STANDAARD_EIND);
    }

    private String formatTijd(LocalTime tijd, String fallback) {
        if (tijd == null) return fallback;
        return tijd.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String dagNaam(DayOfWeek dag) {
        return switch (dag) {
            case MONDAY    -> "maandag";
            case TUESDAY   -> "dinsdag";
            case WEDNESDAY -> "woensdag";
            case THURSDAY  -> "donderdag";
            case FRIDAY    -> "vrijdag";
            case SATURDAY  -> "zaterdag";
            case SUNDAY    -> "zondag";
        };
    }

    private String maandNaam(Month maand) {
        return switch (maand) {
            case JANUARY   -> "jan";
            case FEBRUARY  -> "feb";
            case MARCH     -> "mrt";
            case APRIL     -> "apr";
            case MAY       -> "mei";
            case JUNE      -> "jun";
            case JULY      -> "jul";
            case AUGUST    -> "aug";
            case SEPTEMBER -> "sep";
            case OCTOBER   -> "okt";
            case NOVEMBER  -> "nov";
            case DECEMBER  -> "dec";
        };
    }

    private Set<LocalDate> berekenFeestdagen(int jaar) {
        Set<LocalDate> dagen = new HashSet<>();
        dagen.add(LocalDate.of(jaar, 1, 1));
        dagen.add(LocalDate.of(jaar, 5, 1));
        dagen.add(LocalDate.of(jaar, 7, 21));
        dagen.add(LocalDate.of(jaar, 8, 15));
        dagen.add(LocalDate.of(jaar, 11, 1));
        dagen.add(LocalDate.of(jaar, 11, 11));
        dagen.add(LocalDate.of(jaar, 12, 25));
        LocalDate pasen = berekenPasen(jaar);
        dagen.add(pasen);
        dagen.add(pasen.plusDays(1));
        dagen.add(pasen.plusDays(39));
        dagen.add(pasen.plusDays(50));
        return dagen;
    }

    private LocalDate berekenPasen(int jaar) {
        int a = jaar % 19, b = jaar / 100, c = jaar % 100;
        int d = b / 4, e = b % 4, f = (b + 8) / 25, g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4, k = c % 4, l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int maand = (h + l - 7 * m + 114) / 31;
        int dag   = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(jaar, maand, dag);
    }
}
