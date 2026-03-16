package hogent.sdp2.sdpii.gui.app.locaties.components;

import domain.dto.LocatieDTO;
import domain.facades.LocatieFacade;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LocatiesLayoutController extends VBox {

    @FXML private TextField zoekField;
    @FXML private ComboBox<String> statusDropdown;
    @FXML private TableView<LocatieDTO> locatieTable;
    @FXML private TableColumn<LocatieDTO, String> naamCol;
    @FXML private TableColumn<LocatieDTO, String> locatieCol;
    @FXML private TableColumn<LocatieDTO, Integer> capCol;
    @FXML private TableColumn<LocatieDTO, String> statusCol;
    @FXML private Label lblDetailNaam;
    @FXML private Label lblDetailLocatie;
    @FXML private Label lblDetailCapaciteit;
    @FXML private Label lblDetailStatus;
    @FXML private Button btnWijzig;
    @FXML private Button btnVerwijder;
    @FXML private TextField txtDetailNaam;
    @FXML private TextField txtDetailLocatie;
    @FXML private TextField txtDetailCapaciteit;
    @FXML private ComboBox<String> cmbDetailStatus;
    private boolean isBewerkenModus = false;
    private boolean isAanmakenModus = false;

    private final LocatieFacade locatieFacade = new LocatieFacade();

    public LocatiesLayoutController(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/locaties/components/LocatieLayout.fxml"));
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
        naamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().naam()));
        locatieCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().locatie()));
        capCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().capaciteit()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));
        locatieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        btnWijzig.setDisable(true);
        btnVerwijder.setDisable(true);
        locatieTable.getSelectionModel().selectedItemProperty().addListener((observable, oudeSelectie, nieuweSelectie) -> {
            toonLocatieDetails(nieuweSelectie);
        });
        cmbDetailStatus.getItems().addAll("Actief", "Inactief");
        laadLocaties();
    }

    private void laadLocaties() {
        List<LocatieDTO> alleLocaties = locatieFacade.geefAlleLocaties();
        ObservableList<LocatieDTO> masterData = FXCollections.observableArrayList(alleLocaties);

        FilteredList<LocatieDTO> filteredData = new FilteredList<>(masterData, p -> true);

        zoekField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(locatie -> {
                // Als het zoekveld leeg is, laat dan alles zien
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (locatie.naam() != null && locatie.naam().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (locatie.locatie() != null && locatie.locatie().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });
        SortedList<LocatieDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(locatieTable.comparatorProperty());

        locatieTable.setItems(sortedData);
    }

    private void toonLocatieDetails(LocatieDTO locatie) {
        if (isBewerkenModus || isAanmakenModus) {
            isBewerkenModus = false;
            isAanmakenModus = false;
            btnWijzig.setText("Wijzig");
            btnWijzig.getStyleClass().remove("btn-success");
            btnWijzig.getStyleClass().add("btn-secondary");
            zetBewerkVeldenZichtbaar(false);
        }

        if (locatie != null) {
            lblDetailNaam.setText(locatie.naam());

            lblDetailLocatie.setText(locatie.locatie());

            lblDetailCapaciteit.setText(String.valueOf(locatie.capaciteit()));
            lblDetailStatus.setText(locatie.status());

            btnWijzig.setDisable(false);
            btnVerwijder.setDisable(false);
        } else {
            lblDetailNaam.setText("Selecteer een locatie...");
            lblDetailLocatie.setText("-");
            lblDetailCapaciteit.setText("-");
            lblDetailStatus.setText("-");

            btnWijzig.setDisable(true);
            btnVerwijder.setDisable(true);
        }
    }

    @FXML
    private void handleVerwijder() {
        LocatieDTO geselecteerd = locatieTable.getSelectionModel().getSelectedItem();

        if (geselecteerd != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Locatie Verwijderen");
            alert.setHeaderText("Weet je zeker dat je '" + geselecteerd.naam() + "' wilt verwijderen?");
            alert.setContentText("Deze actie kan niet ongedaan worden gemaakt.");

            Optional<ButtonType> resultaat = alert.showAndWait();
            if (resultaat.isPresent() && resultaat.get() == ButtonType.OK) {
                boolean succes = locatieFacade.verwijderLocatie(geselecteerd.id());

                if (succes) {
                    System.out.println("Locatie succesvol verwijderd!");
                    laadLocaties();
                } else {
                    System.err.println("Verwijderen mislukt.");

                }
            }
        }
    }

    @FXML
    private void handleWijzig() {
        LocatieDTO geselecteerd = locatieTable.getSelectionModel().getSelectedItem();

        if (!isBewerkenModus && !isAanmakenModus) {
            if (geselecteerd == null) return;
            isBewerkenModus = true;
            btnWijzig.setText("Opslaan");
            btnWijzig.getStyleClass().remove("btn-secondary");
            btnWijzig.getStyleClass().add("btn-success");

            txtDetailNaam.setText(geselecteerd.naam());
            txtDetailLocatie.setText(geselecteerd.locatie());
            txtDetailCapaciteit.setText(String.valueOf(geselecteerd.capaciteit()));
            cmbDetailStatus.setValue(geselecteerd.status());

            zetBewerkVeldenZichtbaar(true);

        } else {
            try {
                String nieuweNaam = txtDetailNaam.getText();
                String nieuweLocatie = txtDetailLocatie.getText();
                int nieuweCapaciteit = Integer.parseInt(txtDetailCapaciteit.getText());
                String nieuweStatus = cmbDetailStatus.getValue();

                boolean succes;

                if (isAanmakenModus) {
                    LocatieDTO nieuweSite = new LocatieDTO(null, nieuweNaam, nieuweLocatie, nieuweCapaciteit, nieuweStatus);
                    succes = locatieFacade.maakLocatie(nieuweSite);
                } else {
                    LocatieDTO gewijzigdeLocatie = new LocatieDTO(geselecteerd.id(), nieuweNaam, nieuweLocatie, nieuweCapaciteit, nieuweStatus);
                    succes = locatieFacade.wijzigLocatie(geselecteerd.id(), gewijzigdeLocatie);
                }

                if (succes) {
                    System.out.println(isAanmakenModus ? "Nieuwe site aangemaakt!" : "Wijziging opgeslagen!");
                    isBewerkenModus = false;
                    isAanmakenModus = false;
                    btnWijzig.setText("Wijzig");
                    btnWijzig.getStyleClass().remove("btn-success");
                    btnWijzig.getStyleClass().add("btn-secondary");
                    zetBewerkVeldenZichtbaar(false);

                    laadLocaties();
                } else {
                    System.err.println("Fout bij opslaan op de server.");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Capaciteit moet een geldig getal zijn!");
            }
        }
    }
    private void zetBewerkVeldenZichtbaar(boolean toonInvoer) {
        lblDetailNaam.setVisible(!toonInvoer); lblDetailNaam.setManaged(!toonInvoer);
        txtDetailNaam.setVisible(toonInvoer); txtDetailNaam.setManaged(toonInvoer);

        lblDetailLocatie.setVisible(!toonInvoer); lblDetailLocatie.setManaged(!toonInvoer);
        txtDetailLocatie.setVisible(toonInvoer); txtDetailLocatie.setManaged(toonInvoer);

        lblDetailCapaciteit.setVisible(!toonInvoer); lblDetailCapaciteit.setManaged(!toonInvoer);
        txtDetailCapaciteit.setVisible(toonInvoer); txtDetailCapaciteit.setManaged(toonInvoer);

        lblDetailStatus.setVisible(!toonInvoer); lblDetailStatus.setManaged(!toonInvoer);
        cmbDetailStatus.setVisible(toonInvoer); cmbDetailStatus.setManaged(toonInvoer);
    }

    @FXML
    private void handleNieuw() {
        locatieTable.getSelectionModel().clearSelection();

        isAanmakenModus = true;
        isBewerkenModus = false;

        txtDetailNaam.clear();
        txtDetailLocatie.clear();
        txtDetailCapaciteit.clear();
        cmbDetailStatus.getSelectionModel().clearSelection();

        btnWijzig.setDisable(false);
        btnWijzig.setText("Aanmaken");
        btnWijzig.getStyleClass().remove("btn-secondary");
        btnWijzig.getStyleClass().add("btn-success");
        btnVerwijder.setDisable(true);

        zetBewerkVeldenZichtbaar(true);
    }
}
