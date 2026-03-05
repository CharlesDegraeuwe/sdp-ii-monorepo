package hogent.sdp2.sdpii.gui.app.dashboard.kalender;

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
import java.util.concurrent.atomic.AtomicReference;

public class KalenderController extends VBox {
    @FXML Button btnPrevMonth;
    @FXML Button btnNextMonth;
    @FXML Label lblMonth;
    @FXML GridPane day_grid;

    public KalenderController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/kalender/Kalender.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Dit is de fix
        this.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);

        this.NavigateCalendar();
    }

    public void NavigateCalendar() {
        final int plaatsen_rij = 7;
        final int n_rijen = 5;

        LocalDate date = LocalDate.now();

        AtomicReference<Integer> year = new AtomicReference<>(date.getYear());
        AtomicReference<Month> month = new AtomicReference<>(date.getMonth());

        fillCalendar(month.get(), year.get());

        //maand laten switchen
        lblMonth.setText(monthPicker(month.get()));

        btnPrevMonth.setOnMouseClicked(e -> {
            LocalDate newDate = LocalDate.of(year.get(), month.get(), 1).minusMonths(1);
            month.set(newDate.getMonth());
            year.set(newDate.getYear());
            lblMonth.setText(monthPicker(month.get()));
            fillCalendar(month.get(), year.get());
        });
        btnNextMonth.setOnMouseClicked(e -> {
            LocalDate newDate = LocalDate.of(year.get(), month.get(), 1).plusMonths(1);
            month.set(newDate.getMonth());
            year.set(newDate.getYear());
            lblMonth.setText(monthPicker(month.get()));
            fillCalendar(month.get(), year.get());
        });

        fillCalendar(month.get(), year.get());

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
            // lbl.getStyleClass().add("cal_day"); // optioneel

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
            case MAY -> { return "may"; }
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
