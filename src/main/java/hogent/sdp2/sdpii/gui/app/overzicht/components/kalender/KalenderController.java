package hogent.sdp2.sdpii.gui.app.overzicht.components.kalender;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.AfwezigheidsOverzichtDTO;
import domain.dto.ShiftDTO;
import domain.dto.WerknemerDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class KalenderController extends VBox {
    @FXML Button btnPrevMonth;
    @FXML Button btnNextMonth;
    @FXML Label lblMonth;
    @FXML GridPane day_grid;

    private static final LocalTime STANDAARD_START = LocalTime.of(9, 0);
    private static final LocalTime STANDAARD_EIND  = LocalTime.of(17, 0);

    private List<ShiftDTO> shifts = new ArrayList<>();
    private List<AfwezigheidsOverzichtDTO> afwezigheden = new ArrayList<>();
    private Set<LocalDate> feestdagen = new HashSet<>();

    public KalenderController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/kalender/Kalender.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);
        this.NavigateCalendar();
    }

    public void NavigateCalendar() {
        LocalDate date = LocalDate.now();
        AtomicReference<Integer> year  = new AtomicReference<>(date.getYear());
        AtomicReference<Month>   month = new AtomicReference<>(date.getMonth());

        lblMonth.setText(monthPicker(month.get()));
        laadPlanningData(month.get(), year.get());

        btnPrevMonth.setOnMouseClicked(e -> {
            LocalDate newDate = LocalDate.of(year.get(), month.get(), 1).minusMonths(1);
            month.set(newDate.getMonth());
            year.set(newDate.getYear());
            lblMonth.setText(monthPicker(month.get()));
            laadPlanningData(month.get(), year.get());
        });

        btnNextMonth.setOnMouseClicked(e -> {
            LocalDate newDate = LocalDate.of(year.get(), month.get(), 1).plusMonths(1);
            month.set(newDate.getMonth());
            year.set(newDate.getYear());
            lblMonth.setText(monthPicker(month.get()));
            laadPlanningData(month.get(), year.get());
        });
    }

    private void laadPlanningData(Month month, int year) {
        WerknemerDTO werknemer = Sessie.getInstance().getIngelogdeWerknemer();
        if (werknemer == null) return;

        LocalDate van = LocalDate.of(year, month, 1);
        LocalDate tot = van.withDayOfMonth(van.lengthOfMonth());
        feestdagen = berekenFeestdagen(year);

        new Thread(() -> {
            try {
                List<ShiftDTO> shiftsData = Beheerder.getInstance()
                        .getShiftFacade()
                        .geefShiftenVanWerknemerBereik(werknemer.id(), van, tot);
                List<AfwezigheidsOverzichtDTO> afwData = Beheerder.getInstance()
                        .getPlanningFacade()
                        .geefAfwezighedenVanTeam(werknemer.id(), van, tot);

                Platform.runLater(() -> {
                    this.shifts = shiftsData;
                    this.afwezigheden = afwData;
                    fillCalendar(month, year);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> fillCalendar(month, year));
            }
        }).start();
    }

    public void fillCalendar(Month month, int year) {
        day_grid.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        LocalDate firstDay    = LocalDate.of(year, month, 1);
        int       startCol    = firstDay.getDayOfWeek().getValue() - 1;
        int       daysInMonth = month.length(firstDay.isLeapYear());
        LocalDate today       = LocalDate.now();

        int col = startCol;
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate celDatum = LocalDate.of(year, month, day);
            KalenderItem lbl   = new KalenderItem(day);
            lbl.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
            lbl.getChildren().clear();

            boolean isVandaag          = celDatum.equals(today);
            boolean isFeestdag         = feestdagen.contains(celDatum);
            boolean heeftAfwijkendShift = heeftAfwijkendeShift(celDatum);

            Label tekstLabel = new Label(String.valueOf(day));

            String containerStyle;
            String tekstStyle;

            if (isVandaag) {
                containerStyle = "-fx-background-color: #DBEAFE; -fx-background-radius: 8px;";
                tekstStyle     = "-fx-font-weight: bold; -fx-text-fill: #1D4ED8;";
            } else if (isFeestdag) {
                containerStyle = "-fx-background-color: #FEF3C7; -fx-background-radius: 8px;";
                tekstStyle     = "-fx-font-weight: normal; -fx-text-fill: #92400E;";
            } else if (heeftAfwijkendShift) {
                containerStyle = "-fx-background-color: #FEE2E2; -fx-background-radius: 8px;";
                tekstStyle     = "-fx-font-weight: normal; -fx-text-fill: #B91C1C;";
            } else {
                containerStyle = "";
                tekstStyle     = "-fx-font-weight: normal; -fx-text-fill: #2c3e50;";
            }

            tekstLabel.setStyle(tekstStyle);

            List<AfwezigheidsOverzichtDTO> actieveAfwezigheden = afwezigheden.stream()
                    .filter(a -> !celDatum.isBefore(a.startDatum()) && !celDatum.isAfter(a.eindDatum()))
                    .toList();

            javafx.scene.layout.HBox bolletjesBox = new javafx.scene.layout.HBox(2);
            bolletjesBox.setAlignment(javafx.geometry.Pos.CENTER);
            bolletjesBox.setMinHeight(6);

            if (actieveAfwezigheden.isEmpty()) {
                bolletjesBox.getChildren().add(new javafx.scene.shape.Circle(3, javafx.scene.paint.Color.TRANSPARENT));
            } else {
                int maxBolletjes = Math.min(actieveAfwezigheden.size(), 3);
                for (int i = 0; i < maxBolletjes; i++) {
                    AfwezigheidsOverzichtDTO a = actieveAfwezigheden.get(i);
                    boolean isWachten = a.status() != null && a.status().equals("In afwachting");
                    String kleurHex = a.type().equals("Ziekte") ? "#E31B35" : (isWachten ? "#F59E0B" : "#10B981");
                    bolletjesBox.getChildren().add(new javafx.scene.shape.Circle(3, javafx.scene.paint.Color.web(kleurHex)));
                }
            }

            VBox container = new VBox(2);
            container.setAlignment(javafx.geometry.Pos.CENTER);
            container.getChildren().addAll(tekstLabel, bolletjesBox);
            container.setPadding(new javafx.geometry.Insets(2, 6, 2, 6));
            container.setStyle(containerStyle);
            container.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
            container.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

            lbl.getChildren().add(container);
            lbl.setOnMouseClicked(e -> {
                hogent.sdp2.sdpii.gui.app.planning.PlanningController.startDatumVanuitDashboard = celDatum;
                hogent.sdp2.sdpii.gui.router.Router.getInstance()
                        .navigeerNaar(hogent.sdp2.sdpii.gui.router.Scherm.PLANNING);
            });

            GridPane.setColumnIndex(lbl, col);
            GridPane.setRowIndex(lbl, row);
            day_grid.getChildren().add(lbl);

            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    private boolean heeftAfwijkendeShift(LocalDate datum) {
        return shifts.stream()
                .filter(s -> s.startDatum() != null && s.eindDatum() != null
                          && !datum.isBefore(s.startDatum()) && !datum.isAfter(s.eindDatum()))
                .anyMatch(s -> (s.startTijd() != null && !s.startTijd().truncatedTo(java.time.temporal.ChronoUnit.MINUTES).equals(STANDAARD_START))
                            || (s.eindTijd()  != null && !s.eindTijd().truncatedTo(java.time.temporal.ChronoUnit.MINUTES).equals(STANDAARD_EIND)));
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
        int a = jaar % 19;
        int b = jaar / 100;
        int c = jaar % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int maand = (h + l - 7 * m + 114) / 31;
        int dag   = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(jaar, maand, dag);
    }

    public String monthPicker(Month month) {
        return switch (month) {
            case JANUARY   -> "januari";
            case FEBRUARY  -> "februari";
            case MARCH     -> "maart";
            case APRIL     -> "april";
            case MAY       -> "mei";
            case JUNE      -> "juni";
            case JULY      -> "juli";
            case AUGUST    -> "augustus";
            case SEPTEMBER -> "september";
            case OCTOBER   -> "oktober";
            case NOVEMBER  -> "november";
            case DECEMBER  -> "december";
        };
    }

    public String dayPicker(DayOfWeek day) {
        return switch (day) {
            case MONDAY    -> "maandag";
            case TUESDAY   -> "dinsdag";
            case WEDNESDAY -> "woensdag";
            case THURSDAY  -> "donderdag";
            case FRIDAY    -> "vrijdag";
            case SATURDAY  -> "zaterdag";
            case SUNDAY    -> "zondag";
        };
    }
}
