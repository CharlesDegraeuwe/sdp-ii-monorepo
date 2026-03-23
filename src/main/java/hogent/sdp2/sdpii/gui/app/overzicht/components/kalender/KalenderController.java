package hogent.sdp2.sdpii.gui.app.overzicht.components.kalender;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.AfwezigheidsOverzichtDTO;
import domain.dto.WerknemerDTO;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
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
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class KalenderController extends VBox {
    @FXML Button btnPrevMonth;
    @FXML Button btnNextMonth;
    @FXML Label lblMonth;
    @FXML GridPane day_grid;

    // Nieuwe lijst om de data in op te slaan
    private List<AfwezigheidsOverzichtDTO> afwezigheden = new ArrayList<>();

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

        AtomicReference<Integer> year = new AtomicReference<>(date.getYear());
        AtomicReference<Month> month = new AtomicReference<>(date.getMonth());

        lblMonth.setText(monthPicker(month.get()));

        // Start met het ophalen van data voor de huidige maand (tekent daarna automatisch de kalender)
        laadPlanningData(month.get(), year.get());

        btnPrevMonth.setOnMouseClicked(e -> {
            LocalDate newDate = LocalDate.of(year.get(), month.get(), 1).minusMonths(1);
            month.set(newDate.getMonth());
            year.set(newDate.getYear());
            lblMonth.setText(monthPicker(month.get()));
            laadPlanningData(month.get(), year.get()); // Haal nieuwe data op
        });

        btnNextMonth.setOnMouseClicked(e -> {
            LocalDate newDate = LocalDate.of(year.get(), month.get(), 1).plusMonths(1);
            month.set(newDate.getMonth());
            year.set(newDate.getYear());
            lblMonth.setText(monthPicker(month.get()));
            laadPlanningData(month.get(), year.get()); // Haal nieuwe data op
        });
    }

    // NIEUW: Haal de data asynchroon op zodat je scherm niet vastloopt
    private void laadPlanningData(Month month, int year) {
        WerknemerDTO werknemer = Sessie.getInstance().getIngelogdeWerknemer();
        if (werknemer == null) return;

        LocalDate start = LocalDate.of(year, month, 1).minusDays(7);
        LocalDate eind = LocalDate.of(year, month, 1).plusMonths(1).plusDays(7);

        new Thread(() -> {
            try {
                List<AfwezigheidsOverzichtDTO> data = Beheerder.getInstance()
                        .getPlanningFacade()
                        .geefAfwezighedenVanTeam(werknemer.id(), start, eind);

                Platform.runLater(() -> {
                    this.afwezigheden = data;
                    fillCalendar(month, year); // Teken de kalender pas als we de data hebben!
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void fillCalendar(Month month, int year) {
        // Verwijder oude dag-labels (maar behoud de header-rij)
        day_grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int startCol = firstDay.getDayOfWeek().getValue() - 1; // ma=0, di=1, ... zo=6
        int daysInMonth = month.length(firstDay.isLeapYear());

        int col = startCol;
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            KalenderItem lbl = new KalenderItem(day);
            LocalDate celDatum = LocalDate.of(year, month, day);

            lbl.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
            lbl.getChildren().clear();

            Label tekstLabel = new Label(String.valueOf(day));

            if (celDatum.equals(LocalDate.now())) {
                tekstLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #E31B35;");
            } else {
                tekstLabel.setStyle("-fx-font-weight: normal; -fx-text-fill: #2c3e50;");
            }

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

            if (celDatum.equals(LocalDate.now())) {
                container.setStyle("-fx-background-color: rgba(227, 27, 53, 0.15); -fx-background-radius: 8px;");
            }

            container.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
            container.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

            lbl.getChildren().add(container);

            lbl.setOnMouseClicked(e -> {
                hogent.sdp2.sdpii.gui.app.planning.PlanningController.startDatumVanuitDashboard = celDatum;
                hogent.sdp2.sdpii.gui.router.Router.getInstance().navigeerNaar(hogent.sdp2.sdpii.gui.router.Scherm.PLANNING);
            });

            GridPane.setColumnIndex(lbl, col);
            GridPane.setRowIndex(lbl, row);
            day_grid.getChildren().add(lbl);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    public String monthPicker(Month month) {
        switch(month) {
            case JANUARY -> {return "januari"; }
            case FEBRUARY -> {return "februari"; }
            case MARCH -> { return "maart"; }
            case APRIL -> { return "april"; }
            case MAY -> { return "mei"; }
            case JUNE -> { return "juni"; }
            case JULY ->  { return "juli"; }
            case AUGUST -> { return "augustus"; }
            case SEPTEMBER -> { return "september"; }
            case OCTOBER -> { return "oktober"; }
            case NOVEMBER -> { return "november"; }
            case DECEMBER -> { return "december"; }
        }
        return null;
    }

    public String dayPicker(DayOfWeek day) {
        switch(day) {
            case MONDAY -> {return "maandag"; }
            case TUESDAY -> {return "dinsdag"; }
            case WEDNESDAY -> {return "woensdag"; }
            case THURSDAY -> { return "donderdag"; }
            case FRIDAY -> { return "vrijdag"; }
            case SATURDAY -> { return "zaterdag"; }
            case SUNDAY -> { return "zondag"; }
        }
        return null;
    }
}