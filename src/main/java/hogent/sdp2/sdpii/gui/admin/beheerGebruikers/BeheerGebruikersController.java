
package hogent.sdp2.sdpii.gui.admin.beheerGebruikers;
import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.facades.WerknemersFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
        import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class BeheerGebruikersController extends VBox {

    @FXML private TableView<WerknemerDTO> gebruikersTable;
    @FXML private TableColumn<WerknemerDTO, String> naamCol;
    @FXML private TableColumn<WerknemerDTO, String> voornaamCol;
    @FXML private TableColumn<WerknemerDTO, String> emailCol;
    @FXML private TableColumn<WerknemerDTO, String> rolCol;
    @FXML private TableColumn<WerknemerDTO, String> statusCol;
    @FXML private TableColumn<WerknemerDTO, String> telefoonCol;
    @FXML private TextField zoekField;
    @FXML private Label foutLabel;

    private final WerknemersFacade service = new WerknemersFacade();


    private List<WerknemerDTO> alleWerknemers;
    // TODO filtered list, observable sorted list toevoegen
    // lijstje via decorators omringen wrappers

    public BeheerGebruikersController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/admin/manage_users/ManageUsers.fxml"));
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
            gebruikersTable.setVisible(false);
            return;
        }

        // Kolommen instellen
        naamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().naam()));
        voornaamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().voornaam()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().voornaam()));
        rolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().rol()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));
        telefoonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().telefoonnummer()));

        // Kleur actieve/inactieve rijen
        gebruikersTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(WerknemerDTO werknemer, boolean empty) {
                super.updateItem(werknemer, empty);
                if (werknemer == null || empty) {
                    setStyle("");
                } else if ("Inactief".equalsIgnoreCase(werknemer.status())) {
                    setStyle("-fx-opacity: 0.5;");
                } else {
                    setStyle("");
                }
            }
        });

        laadWerknemers();
        zoekField.textProperty().addListener((obs, oud, nieuw) -> filterWerknemers(nieuw));

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
        List<WerknemerDTO> gefilterd = alleWerknemers.stream()
                .filter(w -> w.naam().toLowerCase().contains(lower)
                        || w.voornaam().toLowerCase().contains(lower)
                        || w.email().toLowerCase().contains(lower)
                        || w.rol().toLowerCase().contains(lower))
                .toList();
        gebruikersTable.setItems(FXCollections.observableArrayList(gefilterd));
    }
}