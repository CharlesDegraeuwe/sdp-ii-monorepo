package hogent.sdp2.sdpii.gui.app.planning.components.sidebar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class PlanningSidebarController extends VBox {
    @FXML
    Label lblDag;
    @FXML
    Button btnPrevDay, btnNextDay;
    @FXML VBox shiftsTodayContainer, unfinishedContainer, finishedContainer;

    private LocalDate selectedDay = LocalDate.now();

    public PlanningSidebarController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/charles/PlanningSidebar.fxml"));
        loader.setRoot(this); loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        init();
    }

    private void init() {
        render();
        btnPrevDay.setOnMouseClicked(e -> { selectedDay = selectedDay.minusDays(1); render(); });
        btnNextDay.setOnMouseClicked(e -> { selectedDay = selectedDay.plusDays(1); render(); });
    }

    private void render() {
        lblDag.setText(selectedDay.getDayOfMonth() + " "
                + selectedDay.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + selectedDay.getYear());

        shiftsTodayContainer.getChildren().setAll(
                new PlanningShiftTodayItemController("08:30 - 12:30", "Locatie B"),
                new PlanningShiftTodayItemController("13:30 - 16:30", "Locatie B")
        );

        unfinishedContainer.getChildren().setAll(
                new PlanningTaakItemController("Taak naam", "Vandaag 13:30", false),
                new PlanningTaakItemController("Taak naam", "Vandaag 13:30", false)
        );

        finishedContainer.getChildren().setAll(
                new PlanningTaakItemController("Taak naam", "Vandaag 13:30", true),
                new PlanningTaakItemController("Taak naam", "Vandaag 13:30", true)
        );
    }
}
