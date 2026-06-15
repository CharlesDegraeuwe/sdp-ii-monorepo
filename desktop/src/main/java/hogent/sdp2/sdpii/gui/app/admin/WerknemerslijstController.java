package hogent.sdp2.sdpii.gui.app.admin;

import domain.Sessie;
import domain.Werknemer;
import domain.WerknemerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import domain.LogService;

import java.io.IOException;
import java.util.List;

public class WerknemerslijstController extends VBox {

    @FXML private TableView<Werknemer> werknemersTable;
    @FXML private TableColumn<Werknemer, String> naamCol;
    @FXML private TableColumn<Werknemer, String> voornaamCol;
    @FXML private TableColumn<Werknemer, String> emailCol;
    @FXML private TableColumn<Werknemer, String> statusCol;
    @FXML private TableColumn<Werknemer, String> telefoonCol;
    @FXML private TextField zoekField;
    @FXML private Label foutLabel;

    private final WerknemerService service = new WerknemerService();
    private final LogService logService = new LogService();
    private List<Werknemer> teamWerknemers;

    public WerknemerslijstController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/WerknemerslijstPage.fxml"));
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
        Werknemer ingelogde = Sessie.getIngelogdeWerknemer();

        if (ingelogde == null) {
            toonFout("Geen gebruiker ingelogd.");
            return;
        }

        if (!"Manager".equalsIgnoreCase(ingelogde.getRol()) && !Sessie.isAdmin()) {
            toonFout("Toegang geweigerd: alleen managers kunnen deze pagina bekijken.");
            return;
        }

        // Kolommen instellen
        naamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNaam()));
        voornaamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVoornaam()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        telefoonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTelefoonnummer()));

        // Kleur inactieve rijen
        werknemersTable.setRowFactory(tv -> new TableRow<>() {
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

        laadTeamWerknemers(ingelogde.getId());
        zoekField.textProperty().addListener((obs, oud, nieuw) -> filterWerknemers(nieuw));

        // Log het raadplegen van de werknemerslijst
        logService.logActie(ingelogde, "RAADPLEGEN", "teamwerknemer", null);
    }

    private void laadTeamWerknemers(int managerId) {
        teamWerknemers = service.geefWerknemersVanManager(managerId);
        werknemersTable.setItems(FXCollections.observableArrayList(teamWerknemers));
    }

    private void filterWerknemers(String zoekterm) {
        if (zoekterm == null || zoekterm.isBlank()) {
            werknemersTable.setItems(FXCollections.observableArrayList(teamWerknemers));
            return;
        }
        String lower = zoekterm.toLowerCase();
        List<Werknemer> gefilterd = teamWerknemers.stream()
                .filter(w -> w.getNaam().toLowerCase().contains(lower)
                        || w.getVoornaam().toLowerCase().contains(lower)
                        || w.getEmail().toLowerCase().contains(lower))
                .toList();
        werknemersTable.setItems(FXCollections.observableArrayList(gefilterd));
    }

    private void toonFout(String bericht) {
        foutLabel.setText(bericht);
        foutLabel.setVisible(true);
        foutLabel.setManaged(true);
        werknemersTable.setVisible(false);
        werknemersTable.setManaged(false);
        zoekField.setVisible(false);
        zoekField.setManaged(false);
    }
}