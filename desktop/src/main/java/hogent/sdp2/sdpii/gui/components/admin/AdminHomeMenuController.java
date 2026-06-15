package hogent.sdp2.sdpii.gui.components.admin;

import domain.auth.Sessie;
import domain.dto.ActiviteitLogDTO;
import domain.services.DashboardApiService;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminHomeMenuController extends VBox {

    // --- KPI Labels ---
    @FXML private Label lblTotaalWerknemers;
    @FXML private Label lblActieveLocaties;
    @FXML private Label lblAfwezigeWerknemers;

    // --- Actieknoppen ---
    @FXML private VBox rgstr_usr;
    @FXML private VBox mng_usr;

    // --- Logs Container ---
    @FXML private VBox logsContainer;

    private final DashboardApiService dashboardService = new DashboardApiService();

    public AdminHomeMenuController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/admin/AdminHomeMenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!Sessie.getInstance().isAdmin()) {
            rgstr_usr.setVisible(false);
            rgstr_usr.setManaged(false);
        }

        this.Router();
        this.laadDashboardData();
    }

    private void Router() {
        rgstr_usr.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.CREEER_MEDEWERKER));
        mng_usr.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.BEHEER_GEBRUIKERS));
    }

    private void laadDashboardData() {
        new Thread(() -> {
            int totaal = dashboardService.getTotaalWerknemers();
            int actieveSites = dashboardService.getActieveSitesPercentage();
            int afwezigen = dashboardService.getAfwezigeWerknemers();
            List<ActiviteitLogDTO> logs = dashboardService.getRecenteLogs();

            Platform.runLater(() -> {
                lblTotaalWerknemers.setText(String.valueOf(totaal));
                lblActieveLocaties.setText(actieveSites + "%");
                lblAfwezigeWerknemers.setText(String.valueOf(afwezigen));

                verwerkLogs(logs);
            });
        }).start();
    }

    private void verwerkLogs(List<ActiviteitLogDTO> logs) {
        logsContainer.getChildren().clear();

        if (logs == null || logs.isEmpty()) {
            Label leeg = new Label("Geen recente activiteit gevonden.");
            leeg.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #94a3b8;");
            logsContainer.getChildren().add(leeg);
            logsContainer.setAlignment(Pos.CENTER);
            return;
        }

        logsContainer.setAlignment(Pos.TOP_LEFT);

        for (ActiviteitLogDTO log : logs) {
            HBox logRegel = maakLogRegel(log);
            logsContainer.getChildren().add(logRegel);
        }
    }

    private HBox maakLogRegel(ActiviteitLogDTO log) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(12, 5, 12, 5));
        row.setStyle("-fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 0 0 1 0;"); // Border-bottom

        Circle dot = new Circle(4);
        dot.setFill(Color.web(getLogColor(log.type())));
        dot.setTranslateY(6);

        VBox contentBox = new VBox(6);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        TextFlow beschrijvingFlow = new TextFlow();

        Label tabelLabel = new Label(log.tabel() != null ? log.tabel().toUpperCase() : "SYSTEEM");
        tabelLabel.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6 2 6; -fx-background-radius: 4;");
        tabelLabel.setTranslateY(-2);

        String voornaam = log.werknemer() != null && log.werknemer().voornaam() != null ? log.werknemer().voornaam() : "";
        String naam = log.werknemer() != null && log.werknemer().naam() != null ? log.werknemer().naam() : "Onbekend";

        Text naamText = new Text("  " + voornaam + " " + naam + " ");
        naamText.setStyle("-fx-font-weight: bold; -fx-fill: #334155; -fx-font-size: 13px;");

        Text actieText = new Text(getActionText(log.type()) + " ");
        actieText.setStyle("-fx-fill: #64748b; -fx-font-size: 13px;");

        Text detailText = new Text(log.beschrijving() != null ? log.beschrijving() : "");
        detailText.setStyle("-fx-fill: #1e293b; -fx-font-size: 13px;");

        beschrijvingFlow.getChildren().addAll(tabelLabel, naamText, actieText, detailText);

        HBox metaRow = new HBox();
        metaRow.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label("Actie: " + (log.type() != null ? log.type() : "ONBEKEND"));
        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String tijdGeformatteerd = log.timestamp();
        try {
            LocalDateTime dt = LocalDateTime.parse(log.timestamp(), DateTimeFormatter.ISO_DATE_TIME);
            tijdGeformatteerd = dt.format(DateTimeFormatter.ofPattern("HH:mm")) + " (" + dt.format(DateTimeFormatter.ofPattern("dd MMM")) + ")";
        } catch (Exception ignored) { }

        Label timeLabel = new Label(tijdGeformatteerd);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #2563eb; -fx-background-color: #eff6ff; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-weight: bold;");

        metaRow.getChildren().addAll(typeLabel, spacer, timeLabel);

        contentBox.getChildren().addAll(beschrijvingFlow, metaRow);
        row.getChildren().addAll(dot, contentBox);

        return row;
    }

    private String getLogColor(String type) {
        if (type == null) return "#9ca3af";
        return switch (type.toUpperCase()) {
            case "CREATE" -> "#22c55e";
            case "DELETE" -> "#ef4444";
            case "UPDATE" -> "#3b82f6";
            default -> "#9ca3af";
        };
    }

    private String getActionText(String type) {
        if (type == null) return "actie: ";
        return switch (type.toUpperCase()) {
            case "CREATE" -> "voegde toe:";
            case "DELETE" -> "verwijderde:";
            case "UPDATE" -> "deed een aanpassing:";
            default -> "actie:";
        };
    }
}