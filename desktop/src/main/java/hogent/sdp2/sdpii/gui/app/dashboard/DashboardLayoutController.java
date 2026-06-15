package hogent.sdp2.sdpii.gui.app.dashboard;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DashboardLayoutController extends BorderPane {

    // Quick Actions
    @FXML private javafx.scene.control.Button btnAddShift;
    @FXML private javafx.scene.control.Button btnAssignTask;
    @FXML private javafx.scene.control.Button btnReportAbsence;
    @FXML private javafx.scene.control.Button btnPlanHoliday;

    // Scheduled Hours
    @FXML private TableView<?> shiftsTable;
    @FXML private TableColumn<?, ?> colDag;
    @FXML private TableColumn<?, ?> colNaam;
    @FXML private TableColumn<?, ?> colUren;
    @FXML private TableColumn<?, ?> colSite;

    // Calendar
    @FXML private javafx.scene.control.Label lblMonth;
    @FXML private GridPane calendarGrid;

    // Notifications
    @FXML private VBox notificatiesList;

    // Statistics
    @FXML private VBox takenList;
    @FXML private VBox afwezigenList;
    @FXML private VBox plantList;

    public DashboardLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/DashboardLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        // TODO: facades injecteren en data laden
    }
}