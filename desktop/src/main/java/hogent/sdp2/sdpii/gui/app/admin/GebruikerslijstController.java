package hogent.sdp2.sdpii.gui.app.admin;

import domain.LogService;
import domain.Sessie;
import domain.Werknemer;
import domain.WerknemerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class GebruikerslijstController extends VBox {

    @FXML private TableView<Werknemer> gebruikersTable;
    @FXML private TableColumn<Werknemer, String> naamCol;
    @FXML private TableColumn<Werknemer, String> voornaamCol;
    @FXML private TableColumn<Werknemer, String> emailCol;
    @FXML private TableColumn<Werknemer, String> rolCol;
    @FXML private TableColumn<Werknemer, String> statusCol;
    @FXML private TableColumn<Werknemer, String> telefoonCol;
    @FXML private TextField zoekField;
    @FXML private Label foutLabel;

    private final WerknemerService service = new WerknemerService();
    private final LogService logService = new LogService();
    private List<Werknemer> alleWerknemers;

    public GebruikerslijstController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/GebruikerslijstPage.fxml"));
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
        if (!Sessie.isAdmin()) {
            foutLabel.setText("Toegang geweigerd: alleen admins kunnen deze pagina bekijken.");
            foutLabel.setVisible(true);
            gebruikersTable.setVisible(false);
            return;
        }

        // Kolommen instellen
        naamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNaam()));
        voornaamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVoornaam()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        rolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRol()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        telefoonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelefoonnummer()));

        // Kleur actieve/inactieve rijen
        gebruikersTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Werknemer werknemer, boolean empty) {
                super.updateItem(werknemer, empty);
                if (werknemer == null || empty) {
                    setStyle("");
                } else if ("Inactief".equalsIgnoreCase(werknemer.getStatus())) {
                    setStyle("-fx-opacity: 0.5;");
                } else {
                    setStyle("");
                }
            }
        });

        laadWerknemers();
        zoekField.textProperty().addListener((obs, oud, nieuw) -> filterWerknemers(nieuw));

        // Log het raadplegen van de gebruikerslijst
        logService.logActie(Sessie.getIngelogdeWerknemer(), "RAADPLEGEN", "werknemers", null);
    }

    private void laadWerknemers() {
        alleWerknemers = service.geefAlleWerknemers();
        gebruikersTable.setItems(FXCollections.observableArrayList(alleWerknemers));
    }

    private void filterWerknemers(String zoekterm) {
        if (zoekterm == null || zoekterm.isBlank()) {
            gebruikersTable.setItems(FXCollections.observableArrayList(alleWerknemers));
            return;
        }
        String lower = zoekterm.toLowerCase();
        List<Werknemer> gefilterd = alleWerknemers.stream()
                .filter(w -> w.getNaam().toLowerCase().contains(lower)
                        || w.getVoornaam().toLowerCase().contains(lower)
                        || w.getEmail().toLowerCase().contains(lower)
                        || w.getRol().toLowerCase().contains(lower))
                .toList();
        gebruikersTable.setItems(FXCollections.observableArrayList(gefilterd));
    }
}