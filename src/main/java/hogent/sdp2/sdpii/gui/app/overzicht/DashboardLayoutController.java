package hogent.sdp2.sdpii.gui.app.overzicht;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.app.overzicht.afwezigheden.AfwezighedenController;
import hogent.sdp2.sdpii.gui.app.overzicht.kalender.KalenderController;
import hogent.sdp2.sdpii.gui.app.overzicht.locatie_info.LocatieInfoController;
import hogent.sdp2.sdpii.gui.app.overzicht.notificaties.NotificatieController;
import hogent.sdp2.sdpii.gui.app.overzicht.open_taken.OpenTakenController;
import hogent.sdp2.sdpii.gui.app.overzicht.uren.UrenController;
import hogent.sdp2.sdpii.gui.app.overzicht.jouw_uren.JouwUrenController;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
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

    @FXML Label shift;
    @FXML Label taak;
    @FXML Label afwezig;
    @FXML Label vakantie;


    //containers
    @FXML private GridPane calendarGrid;
    @FXML private VBox kalender_container;
    @FXML private VBox notifications_container;
    @FXML private VBox uren_container;
    @FXML private VBox taken_container;
    @FXML private VBox locatie_info;
    @FXML private VBox locatie_info_container;
    @FXML private VBox planned_hours_container;

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
        taken_container.getChildren().add(new OpenTakenController());
        locatie_info.getChildren().add(new LocatieInfoController());
        planned_hours_container.getChildren().add(new JouwUrenController());
    }

    @FXML
    private void initialize() {
        boolean role = Sessie.getInstance().isWerknemer();
        if(role) {
            shift.setText("Bekijk shiften");
            taak.setText("Bekijk taken");
            locatie_info_container.setVisible(false);
            uren_container.getChildren().add(new UrenController());
        }
        if(Sessie.getInstance().isSuperVisor()) {
            shift.setText("Shift Toevoegen");
            taak.setText("Taak Toekennen");
            uren_container.getChildren().add(new AfwezighedenController());
            locatie_info_container.setVisible(false);
        }
        else {
            uren_container.getChildren().add(new AfwezighedenController());
            shift.setText("Shift Toevoegen");
            taak.setText("Taak Toekennen");
        }
       add_shift.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.PLANNING));
       assign_task.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.TAKEN));
       report_absence.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.ZIEKTE));
       plan_holiday.setOnMouseClicked(e-> Router.getInstance().navigeerNaar(Scherm.VERLOF));
    }
}