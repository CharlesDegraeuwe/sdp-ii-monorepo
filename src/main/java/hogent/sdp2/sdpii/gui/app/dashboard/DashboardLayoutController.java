package hogent.sdp2.sdpii.gui.app.dashboard;

import hogent.sdp2.sdpii.gui.app.dashboard.kalender.KalenderController;
import hogent.sdp2.sdpii.gui.app.dashboard.notificaties.NotificatieController;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DashboardLayoutController extends BorderPane {

    // Quick Actions
    @FXML private HBox add_shift;
    @FXML private HBox assign_task;
    @FXML private HBox report_absence;
    @FXML private HBox plan_holiday;


    //containers
    @FXML private GridPane calendarGrid;
    @FXML private VBox kalender_container;
    @FXML private VBox notifications_container;

    // Scheduled Hours
    @FXML private TableView<?> shiftsTable;
    @FXML private TableColumn<?, ?> colDag;
    @FXML private TableColumn<?, ?> colNaam;
    @FXML private TableColumn<?, ?> colUren;
    @FXML private TableColumn<?, ?> colSite;


    // Calendar
    @FXML private javafx.scene.control.Label lblMonth;


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

        //toevoegen van velden
        kalender_container.getChildren().add(new KalenderController());
        notifications_container.getChildren().add(new NotificatieController());
    }

    @FXML
    private void initialize() {
       add_shift.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.PLANNING));
       assign_task.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.TAKEN));
       report_absence.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.AFWEZIGHEID));
       plan_holiday.setOnMouseClicked(e-> Router.getInstance().navigeerNaar(Scherm.AFWEZIGHEID));
    }
}