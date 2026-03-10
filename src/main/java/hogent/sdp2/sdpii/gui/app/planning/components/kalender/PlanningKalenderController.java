package hogent.sdp2.sdpii.gui.app.planning.components.kalender;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

public class PlanningKalenderController extends VBox {
    @FXML Button btnPrev, btnNext;
    @FXML Label lblMonth;
    @FXML GridPane kalenderGrid;

    private LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

    public PlanningKalenderController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/charles/PlanningKalender.fxml"));
        loader.setRoot(this); loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        init();
    }

    private void init() {
        renderer();
        btnPrev.setOnMouseClicked(e -> { currentMonth = currentMonth.minusMonths(1); renderer(); });
        btnNext.setOnMouseClicked(e -> { currentMonth = currentMonth.plusMonths(1); renderer(); });
    }

    private void renderer() {
        kalenderGrid.getChildren().removeIf(n -> GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) > 0);
        lblMonth.setText(monthPicker(currentMonth.getMonth()) + " " + currentMonth.getYear());

        int startCol = currentMonth.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();
        int col = startCol, row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            PlanningDagCelController cel = new PlanningDagCelController(day);
            GridPane.setColumnIndex(cel, col);
            GridPane.setRowIndex(cel, row);
            GridPane.setHgrow(cel, Priority.ALWAYS);
            kalenderGrid.getChildren().add(cel);
            if (++col == 7) { col = 0; row++; } //kkr moeilijk deze
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