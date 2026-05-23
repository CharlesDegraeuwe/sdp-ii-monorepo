package hogent.sdp2.sdpii.gui.admin.viewLogs;

import domain.auth.Sessie;
import domain.dto.LogDTO;
import domain.dto.WerknemerDTO;
import domain.facades.LogFacade;
import domain.facades.WerknemersFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final WerknemersFacade werknemersFacade = new WerknemersFacade();
    private List<LogDTO> alleLogs;
    private List<WerknemerDTO> alleWerknemers;

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
        type.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().type()));
        tabel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tabel()));
        timestamp.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().timestamp().toString()));
        details.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().details()));

        laadLogs();
        zoekField.textProperty().addListener((obs, oud, nieuw) -> filterWerknemers(nieuw));
    }

    @FXML
    private void herlaad() {
        zoekField.clear();
        laadLogs();

    }

    private void laadLogs() {
        try {
            alleWerknemers = werknemersFacade.geefAlleWerknemers();
            Map<Integer, WerknemerDTO> werknemersMap = alleWerknemers.stream()
                    .collect(Collectors.toMap(WerknemerDTO::id, Function.identity()));

            werknemer.setCellValueFactory(data -> {
                WerknemerDTO w = werknemersMap.get(data.getValue().werknemerId());
                String naam = w != null ? w.voornaam() + " " + w.naam() : "#" + data.getValue().werknemerId();
                return new SimpleStringProperty(naam);
            });

            alleLogs = service.geefAlleLogs();
            logsTable.setItems(FXCollections.observableArrayList(alleLogs));
        } catch (Exception e) {
            alleLogs = List.of();
            foutLabel.setText("Logs konden niet geladen worden.");
            foutLabel.setVisible(true);
        }
    }

    private void filterWerknemers(String zoekterm) {
        if (zoekterm == null || zoekterm.isBlank()) {
            logsTable.setItems(FXCollections.observableArrayList(alleLogs));
            return;
        }
        String lower = zoekterm.toLowerCase();

        alleWerknemers = werknemersFacade.geefAlleWerknemers();

        Map<Integer, WerknemerDTO> werknemersMap = alleWerknemers.stream()
                .collect(Collectors.toMap(WerknemerDTO::id, Function.identity()));

        List<LogDTO> gefilterd = alleLogs.stream()
                .filter(log -> {
                    WerknemerDTO w = werknemersMap.get(log.werknemerId());
                    return w != null &&
                            (w.voornaam() + " " + w.naam()).toLowerCase().contains(lower);
                })
                .toList();
        logsTable.setItems(FXCollections.observableArrayList(gefilterd));
    }

}