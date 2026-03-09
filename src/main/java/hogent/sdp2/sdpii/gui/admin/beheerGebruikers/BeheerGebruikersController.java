
package hogent.sdp2.sdpii.gui.admin.beheerGebruikers;
import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.facades.WerknemersFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    @FXML private Button btnActiveer;
    @FXML private Button btnDeactiveer;
    @FXML private Button btnBlokkeer;

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
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
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
                } else if ("Geblokkeerd".equalsIgnoreCase(werknemer.status())) {
                    setStyle("-fx-text-background-color: red; -fx-font-weight: bold;");
                }else {
                    setStyle("");
                }
            }
        });

        btnActiveer.setDisable(true);
        btnDeactiveer.setDisable(true);
        btnBlokkeer.setDisable(true);

        gebruikersTable.getSelectionModel().selectedItemProperty().addListener((obs, oudeSelectie, nieuweSelectie) -> {
            boolean isGeselecteerd = (nieuweSelectie != null);
            btnActiveer.setDisable(!isGeselecteerd);
            btnDeactiveer.setDisable(!isGeselecteerd);
            btnBlokkeer.setDisable(!isGeselecteerd);
        });

        laadWerknemers();
    }

    @FXML
    private void handleActiveer() {
        pasStatusAan("activeer");
    }

    @FXML
    private void handleDeactiveer() {
        pasStatusAan("deactiveer");
    }

    @FXML
    private void handleBlokkeer() {
        pasStatusAan("blokkeer");
    }

    private void pasStatusAan(String actie) {
        WerknemerDTO geselecteerdeWerknemer = gebruikersTable.getSelectionModel().getSelectedItem();

        if (geselecteerdeWerknemer != null) {
            foutLabel.setVisible(false);
            boolean succes = service.veranderStatus(geselecteerdeWerknemer.id(), actie);

            if (succes) {
                laadWerknemers();
            } else {
                foutLabel.setText("Er ging iets mis bij het communiceren met de server.");
                foutLabel.setVisible(true);
            }
        }
    }

    private void laadWerknemers() {
        alleWerknemers = service.geefAlleWerknemers();
        ObservableList<WerknemerDTO> masterData = FXCollections.observableArrayList(alleWerknemers);

        FilteredList<WerknemerDTO> filteredData = new FilteredList<>(masterData, p -> true);

        zoekField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(werknemer -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (werknemer.voornaam() != null && werknemer.voornaam().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (werknemer.naam() != null && werknemer.naam().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (werknemer.email() != null && werknemer.email().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<WerknemerDTO> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(gebruikersTable.comparatorProperty());

        gebruikersTable.setItems(sortedData);
    }
}