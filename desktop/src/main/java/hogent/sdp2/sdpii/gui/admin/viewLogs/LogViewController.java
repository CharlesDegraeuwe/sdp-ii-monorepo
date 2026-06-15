package hogent.sdp2.sdpii.gui.admin.viewLogs;

import domain.auth.Sessie;
import domain.dto.LogDTO;
import domain.dto.WerknemerDTO;
import domain.facades.LogFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class LogViewController extends VBox {


    @FXML
    private TableView<LogDTO> logsTable;
    @FXML private TableColumn<LogDTO, String> id;
    @FXML private TableColumn<LogDTO, String> werknemer;
    @FXML private TableColumn<LogDTO, String> type; // create/update/delete --> enum voor aanmaken
    @FXML private TableColumn<LogDTO, String> tabel; // in welke tabel een wijziging
    @FXML private TableColumn<LogDTO, String> timestamp; // wanneer
    @FXML private TableColumn<LogDTO, String> details; // noemt test
    @FXML private TextField zoekField;
    @FXML private Label foutLabel;

    private final LogFacade service = new LogFacade();
    private List<LogDTO> alleLogs;

    public LogViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/admin/view_logs/ViewLogs.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        // Controleer of de ingelogde gebruiker admin is
        if (!Sessie.getInstance().isAdmin()) {
            foutLabel.setText("Toegang geweigerd: alleen admins kunnen deze pagina bekijken.");
            foutLabel.setVisible(true);
            logsTable.setVisible(false);
            return;
        }

        // Kolommen instellen
        id.setCellValueFactory(data -> new SimpleStringProperty(Integer.toString(data.getValue().id())));
        werknemer.setCellValueFactory(cellData -> {
            WerknemerDTO w = cellData.getValue().werknemer();
            String fullName = w.naam() + " " + w.voornaam();
            return new SimpleStringProperty(fullName);
        });
        type.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().type()));
        tabel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tabel()));
        timestamp.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().timestamp().toString()));
        details.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().test()));

        laadLogs();
        zoekField.textProperty().addListener((obs, oud, nieuw) -> filterWerknemers(nieuw));

    }

    private void laadLogs() {
        alleLogs = service.geefAlleLogs();
        logsTable.setItems(FXCollections.observableArrayList(alleLogs));
    }

    private void filterWerknemers(String zoekterm) {
        if (zoekterm == null || zoekterm.isBlank()) {
            logsTable.setItems(FXCollections.observableArrayList(alleLogs));
            return;
        }
        String lower = zoekterm.toLowerCase();
        List<LogDTO> gefilterd = alleLogs.stream()
                .filter(w -> w.werknemer().toString().toLowerCase().contains(lower))
                .toList();
        logsTable.setItems(FXCollections.observableArrayList(gefilterd));
    }

}
