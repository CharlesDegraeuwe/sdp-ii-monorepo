package hogent.sdp2.sdpii.gui.admin.beheerGebruikers;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.facades.WerknemersFacade;
import javafx.application.Platform;
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
    @FXML private ComboBox<String> statusFilterBox;

    private final WerknemersFacade service = new WerknemersFacade();


    private List<WerknemerDTO> alleWerknemers;
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

        gebruikersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        statusFilterBox.getItems().addAll("Alle", "Actief", "Inactief", "Geblokkeerd");
        statusFilterBox.getSelectionModel().selectFirst();
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
        if (geselecteerdeWerknemer == null) return;

        foutLabel.setVisible(false);
        foutLabel.setManaged(false);
        btnActiveer.setDisable(true);
        btnDeactiveer.setDisable(true);
        btnBlokkeer.setDisable(true);

        new Thread(() -> {
            try {
                boolean succes = service.veranderStatus(geselecteerdeWerknemer.id(), actie);
                Platform.runLater(() -> {
                    if (succes) {
                        laadWerknemers();
                    } else {
                        foutLabel.setText("Er ging iets mis bij het communiceren met de server.");
                        foutLabel.setVisible(true);
                        foutLabel.setManaged(true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    foutLabel.setText("Fout: " + e.getMessage());
                    foutLabel.setVisible(true);
                    foutLabel.setManaged(true);
                });
            }
        }).start();
    }

    private void laadWerknemers() {
        gebruikersTable.setPlaceholder(new Label("Laden..."));
        new Thread(() -> {
            try {
                List<WerknemerDTO> geladen = service.geefAlleWerknemers();
                Platform.runLater(() -> {
                    alleWerknemers = geladen;
                    ObservableList<WerknemerDTO> masterData = FXCollections.observableArrayList(alleWerknemers);

                    FilteredList<WerknemerDTO> filteredData = new FilteredList<>(masterData, p -> true);
                    Runnable updateFilter = () -> {
                        String zoekTekst = zoekField.getText() == null ? "" : zoekField.getText().toLowerCase();
                        String gekozenStatus = statusFilterBox.getValue() == null ? "Alle" : statusFilterBox.getValue();
                        filteredData.setPredicate(werknemer -> {
                            boolean statusMatch = "Alle".equals(gekozenStatus) || gekozenStatus.equalsIgnoreCase(werknemer.status());
                            boolean tekstMatch = zoekTekst.isBlank() ||
                                    (werknemer.voornaam() != null && werknemer.voornaam().toLowerCase().contains(zoekTekst)) ||
                                    (werknemer.naam() != null && werknemer.naam().toLowerCase().contains(zoekTekst)) ||
                                    (werknemer.email() != null && werknemer.email().toLowerCase().contains(zoekTekst));
                            return statusMatch && tekstMatch;
                        });
                    };
                    zoekField.textProperty().addListener((obs, oud, nieuw) -> updateFilter.run());
                    statusFilterBox.valueProperty().addListener((obs, oud, nieuw) -> updateFilter.run());
                    updateFilter.run();

                    SortedList<WerknemerDTO> sortedData = new SortedList<>(filteredData);
                    sortedData.comparatorProperty().bind(gebruikersTable.comparatorProperty());
                    gebruikersTable.setItems(sortedData);
                    gebruikersTable.setPlaceholder(new Label("Geen gebruikers gevonden."));
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    foutLabel.setText("Fout bij laden van gebruikers: " + e.getMessage());
                    foutLabel.setVisible(true);
                    foutLabel.setManaged(true);
                    gebruikersTable.setPlaceholder(new Label("Fout bij laden."));
                });
            }
        }).start();
    }
}