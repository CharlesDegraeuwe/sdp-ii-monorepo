package hogent.sdp2.sdpii.gui.app.locaties.components;


import domain.dto.LocatieDTO;
import domain.dto.MachineAanmaakDTO;
import domain.facades.LocatieFacade;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LocatiesLayoutController extends VBox {

    @FXML private TextField zoekField;
    @FXML private ComboBox<String> statusDropdown;
    @FXML private ListView<MachineAanmaakDTO> listMachines;
    @FXML private Button btnWijzigMachine;
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
    @FXML private Button btnVerwijderMachine;
    @FXML private Button btnVerwijder;
    @FXML private TextField txtDetailNaam;
    @FXML private TextField txtDetailLocatie;
    @FXML private TextField txtDetailCapaciteit;
    @FXML private ComboBox<String> cmbDetailStatus;
    @FXML private Button btnMaakMachine;
    private boolean isBewerkenModus = false;
    private boolean isAanmakenModus = false;

    private final LocatieFacade locatieFacade = new LocatieFacade();
    @FXML VBox leftCard;
    @FXML VBox rightCard;


    public LocatiesLayoutController(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/locaties/components/LocatieLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        locatieTable.setPlaceholder(new Label(""));
        locatieTable.setFixedCellSize(40);

        locatieTable.prefHeightProperty().bind(
                locatieTable.fixedCellSizeProperty()
                        .multiply(Bindings.size(locatieTable.getItems()))
                        .add(35) // hoogte van de header
        );
        locatieTable.setMinHeight(75); // header + minstens 1 rij

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
        listMachines.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(MachineAanmaakDTO machine, boolean empty) {
                super.updateItem(machine, empty);
                if (empty || machine == null) {
                    setText(null);
                } else {
                    setText(machine.naam() + " (" + machine.status() + ")"); // Ziet eruit als: Poes (Onderhoud)
                }
            }
        });

        listMachines.getSelectionModel().selectedItemProperty().addListener((obs, oudeSelectie, nieuweSelectie) -> {
            boolean heeftSelectie = (nieuweSelectie != null);
            btnWijzigMachine.setDisable(!heeftSelectie);
            btnVerwijderMachine.setDisable(!heeftSelectie);
        });
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
            btnMaakMachine.setDisable(false);
            List<MachineAanmaakDTO> echteMachines = locatieFacade.haalMachinesOpVoorSite(locatie.id());
            listMachines.setItems(FXCollections.observableArrayList(echteMachines));
        } else {
            lblDetailNaam.setText("Selecteer een locatie...");
            lblDetailLocatie.setText("-");
            lblDetailCapaciteit.setText("-");
            lblDetailStatus.setText("-");

            btnWijzig.setDisable(true);
            btnVerwijder.setDisable(true);
            btnMaakMachine.setDisable(true);
            btnVerwijderMachine.setDisable(true);
            listMachines.getItems().clear();
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

    @FXML
    private void handleMaakMachine() {
        LocatieDTO geselecteerd = locatieTable.getSelectionModel().getSelectedItem();

        if (geselecteerd == null) return;

        Dialog<MachineAanmaakDTO> dialog = new Dialog<>();
        dialog.setTitle("Nieuwe Machine Creëren");
        dialog.setHeaderText("Voeg een machine toe aan site: " + geselecteerd.naam());

        ButtonType btnTypeAanmaken = new ButtonType("Aanmaken", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeAanmaken, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField naamVeld = new TextField();
        naamVeld.setPromptText("Naam (bijv. Graafmachine)");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Actief", "Inactief", "Onderhoud");
        statusBox.getSelectionModel().selectFirst();

        grid.add(new Label("Machine naam:"), 0, 0);
        grid.add(naamVeld, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(naamVeld::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeAanmaken && !naamVeld.getText().isBlank()) {
                return new MachineAanmaakDTO(
                        naamVeld.getText(),
                        statusBox.getValue(),
                        geselecteerd.id()
                );
            }
            return null;
        });

        Optional<MachineAanmaakDTO> resultaat = dialog.showAndWait();

        resultaat.ifPresent(nieuweMachine -> {
            boolean succes = locatieFacade.maakMachine(nieuweMachine);

            if (succes) {
                System.out.println("Machine '" + nieuweMachine.naam() + "' succesvol op status '" + nieuweMachine.status() + "' gezet!");
            } else {
                System.err.println("Fout bij het aanmaken van de machine. Check je backend.");
            }
        });
    }

    @FXML
    private void handleWijzigMachine() {
        MachineAanmaakDTO geselecteerdeMachine = listMachines.getSelectionModel().getSelectedItem();
        LocatieDTO huidigeSite = locatieTable.getSelectionModel().getSelectedItem();

        if (geselecteerdeMachine == null || huidigeSite == null) return;

        Dialog<MachineAanmaakDTO> dialog = new Dialog<>();
        dialog.setTitle("Machine Wijzigen");
        dialog.setHeaderText("Wijzig gegevens van: " + geselecteerdeMachine.naam());

        ButtonType btnOpslaan = new ButtonType("Opslaan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOpslaan, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 10));

        TextField txtNaam = new TextField(geselecteerdeMachine.naam());

        ComboBox<String> cmbStatus = new ComboBox<>();
        cmbStatus.getItems().addAll("Actief", "Inactief", "Onderhoud");
        cmbStatus.setValue(geselecteerdeMachine.status());

        ComboBox<LocatieDTO> cmbSites = new ComboBox<>();
        List<LocatieDTO> alleSites = locatieFacade.geefAlleLocaties();
        cmbSites.setItems(FXCollections.observableArrayList(alleSites));

        cmbSites.setConverter(new StringConverter<>() {
            @Override public String toString(LocatieDTO l) { return l == null ? "" : l.naam(); }
            @Override public LocatieDTO fromString(String s) { return null; }
        });

        alleSites.stream()
                .filter(s -> s.id().equals(huidigeSite.id()))
                .findFirst()
                .ifPresent(cmbSites::setValue);

        grid.add(new Label("Naam:"), 0, 0); grid.add(txtNaam, 1, 0);
        grid.add(new Label("Status:"), 0, 1); grid.add(cmbStatus, 1, 1);
        grid.add(new Label("Gekoppeld aan Site:"), 0, 2); grid.add(cmbSites, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == btnOpslaan && !txtNaam.getText().isBlank() && cmbSites.getValue() != null) {
                return new MachineAanmaakDTO(
                        txtNaam.getText(),
                        cmbStatus.getValue(),
                        Math.toIntExact(cmbSites.getValue().id())
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(gewijzigdeData -> {
            boolean succes = locatieFacade.wijzigMachine(geselecteerdeMachine.siteId(), gewijzigdeData);
            if (succes) {
                System.out.println("Machine succesvol gewijzigd!");
                toonLocatieDetails(huidigeSite);
            } else {
                System.err.println("Fout bij opslaan machine op de server.");
            }
        });
    }

    @FXML
    private void handleVerwijderMachine() {
        MachineAanmaakDTO geselecteerdeMachine = listMachines.getSelectionModel().getSelectedItem();
        LocatieDTO huidigeSite = locatieTable.getSelectionModel().getSelectedItem();

        if (geselecteerdeMachine != null && huidigeSite != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Machine Verwijderen");
            alert.setHeaderText("Weet je zeker dat je '" + geselecteerdeMachine.naam() + "' wilt verwijderen?");
            alert.setContentText("Deze actie kan niet ongedaan worden gemaakt.");

            Optional<ButtonType> resultaat = alert.showAndWait();

            if (resultaat.isPresent() && resultaat.get() == ButtonType.OK) {

                boolean succes = locatieFacade.verwijderMachine(geselecteerdeMachine.siteId());

                if (succes) {
                    System.out.println("Machine succesvol verwijderd!");

                    toonLocatieDetails(huidigeSite);
                } else {
                    System.err.println("Fout bij verwijderen machine op de server.");
                }
            }
        }
    }
}
