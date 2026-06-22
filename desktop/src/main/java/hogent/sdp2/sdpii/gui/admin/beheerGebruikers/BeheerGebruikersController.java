package hogent.sdp2.sdpii.gui.admin.beheerGebruikers;

import domain.auth.Sessie;
import domain.dto.UpdateWerknemerDTO;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BeheerGebruikersController extends VBox {

    @FXML private TableView<WerknemerDTO> gebruikersTable;
    @FXML private TableColumn<WerknemerDTO, String> naamCol;
    @FXML private TableColumn<WerknemerDTO, String> voornaamCol;
    @FXML private TableColumn<WerknemerDTO, String> emailCol;
    @FXML private TableColumn<WerknemerDTO, String> rolCol;
    @FXML private TableColumn<WerknemerDTO, String> statusCol;
    @FXML private TableColumn<WerknemerDTO, String> telefoonCol;
    @FXML private TableColumn<WerknemerDTO, Void> actieCol; // NIEUW

    @FXML private TextField zoekField;
    @FXML private Label foutLabel;
    @FXML private ComboBox<String> statusFilterBox;

    private final WerknemersFacade service = new WerknemersFacade();
    private List<WerknemerDTO> alleWerknemers;

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
            foutLabel.setManaged(true);
            gebruikersTable.setVisible(false);
            return;
        }

        // Zorg dat de tabel bewerkbaar is
        gebruikersTable.setEditable(true);

        // --- TEXT KOLOMMEN (Inline bewerkbaar) ---
        naamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().naam()));
        naamCol.setCellFactory(TextFieldTableCell.forTableColumn());
        naamCol.setOnEditCommit(event -> verwerkTekstUpdate(event.getRowValue(), "naam", event.getNewValue()));

        voornaamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().voornaam()));
        voornaamCol.setCellFactory(TextFieldTableCell.forTableColumn());
        voornaamCol.setOnEditCommit(event -> verwerkTekstUpdate(event.getRowValue(), "voornaam", event.getNewValue()));

        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setOnEditCommit(event -> verwerkTekstUpdate(event.getRowValue(), "email", event.getNewValue()));

        telefoonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().telefoonnummer()));
        telefoonCol.setCellFactory(TextFieldTableCell.forTableColumn());
        telefoonCol.setOnEditCommit(event -> verwerkTekstUpdate(event.getRowValue(), "telefoonnummer", event.getNewValue()));

        // --- ROL KOLOM ---
        rolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().rol()));
        rolCol.setCellFactory(tc -> new TableCell<>() {
            private final ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList("Admin", "Manager", "Werknemer"));
            {
                combo.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setGraphic(null);
                } else {
                    combo.setOnAction(null);

                    String weergaveRol = rol.equalsIgnoreCase("Supervisor") ? "Werknemer" : rol;

                    combo.setValue(weergaveRol);
                    updateRoleStyle(combo, weergaveRol);
                    setGraphic(combo);

                    combo.setOnAction(e -> {
                        WerknemerDTO w = getTableView().getItems().get(getIndex());
                        String nieuweRol = combo.getValue();

                        if (w != null && nieuweRol != null && !nieuweRol.equals(weergaveRol)) {
                            updateRoleStyle(combo, nieuweRol);

                            new Thread(() -> {
                                try {
                                    boolean succes = service.veranderRol(w.id(), nieuweRol);
                                    if (succes) {
                                        Platform.runLater(() -> laadWerknemers());
                                    } else {
                                        toonFout("Fout bij updaten rol op de server.");
                                    }
                                } catch (Exception ex) {
                                    toonFout("Connectiefout bij rol wijzigen: " + ex.getMessage());
                                }
                            }).start();
                        }
                    });
                }
            }
        });

        // --- STATUS KOLOM ---
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));
        statusCol.setCellFactory(tc -> new TableCell<>() {
            private final ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList("Actief", "Inactief", "Geblokkeerd"));
            {
                combo.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    combo.setOnAction(null);
                    combo.setValue(status);
                    updateStatusStyle(combo, status);
                    setGraphic(combo);

                    combo.setOnAction(e -> {
                        WerknemerDTO w = getTableView().getItems().get(getIndex());
                        String nieuweStatus = combo.getValue();

                        if (w != null && nieuweStatus != null && !nieuweStatus.equals(status)) {
                            updateStatusStyle(combo, nieuweStatus);

                            String actie = switch (nieuweStatus.toLowerCase()) {
                                case "actief" -> "activeer";
                                case "inactief" -> "deactiveer";
                                case "geblokkeerd" -> "blokkeer";
                                default -> "";
                            };

                            new Thread(() -> {
                                try {
                                    boolean succes = service.veranderStatus(w.id(), actie);
                                    if (succes) {
                                        Platform.runLater(() -> laadWerknemers());
                                    } else {
                                        toonFout("Fout bij updaten status.");
                                    }
                                } catch (Exception ex) {
                                    toonFout("Connectiefout: " + ex.getMessage());
                                }
                            }).start();
                        }
                    });
                }
            }
        });

        // --- ACTIE KOLOM (Vuilbakje) ---
        actieCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button();
            {
                FontIcon trashIcon = new FontIcon("fas-trash"); // Zorg dat ikonli-fontawesome5-pack in je pom.xml zit
                trashIcon.setIconSize(16);
                trashIcon.setIconColor(javafx.scene.paint.Color.valueOf("#ef4444")); // Rode kleur

                deleteBtn.setGraphic(trashIcon);
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                deleteBtn.setOnAction(event -> {
                    WerknemerDTO w = getTableView().getItems().get(getIndex());

                    if (w.email().equals(Sessie.getInstance().getIngelogdeWerknemer().email())) {
                        toonFout("Je kunt je eigen account niet verwijderen.");
                        return;
                    }

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Gebruiker verwijderen");
                    alert.setHeaderText("Weet je zeker dat je " + w.voornaam() + " wilt verwijderen?");
                    alert.setContentText("Deze actie kan niet ongedaan worden gemaakt.");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        new Thread(() -> {
                            try {
                                boolean succes = service.verwijderWerknemer(w.id());
                                if (succes) {
                                    Platform.runLater(() -> laadWerknemers());
                                } else {
                                    toonFout("Fout bij verwijderen van gebruiker.");
                                }
                            } catch (Exception ex) {
                                toonFout("Connectiefout: " + ex.getMessage());
                            }
                        }).start();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Check of het de huidige ingelogde gebruiker is, zo ja: geen vuilbak tonen
                    WerknemerDTO w = getTableView().getItems().get(getIndex());
                    if (w != null && w.email().equals(Sessie.getInstance().getIngelogdeWerknemer().email())) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteBtn);
                    }
                }
            }
        });

        gebruikersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        statusFilterBox.getItems().addAll("Alle", "Actief", "Inactief", "Geblokkeerd");
        statusFilterBox.getSelectionModel().selectFirst();
        laadWerknemers();
    }

    // --- HELPER METHODE VOOR INLINE UPDATES ---
// --- HELPER METHODE VOOR INLINE UPDATES ---
    private void verwerkTekstUpdate(WerknemerDTO werknemer, String veld, String nieuweWaarde) {
        new Thread(() -> {
            try {
                String naam = werknemer.naam();
                String voornaam = werknemer.voornaam();
                String email = werknemer.email();
                String telefoon = werknemer.telefoonnummer();

                switch (veld) {
                    case "naam" -> naam = nieuweWaarde;
                    case "voornaam" -> voornaam = nieuweWaarde;
                    case "email" -> email = nieuweWaarde;
                    case "telefoonnummer" -> telefoon = nieuweWaarde;
                }

                UpdateWerknemerDTO updateDTO = new UpdateWerknemerDTO(
                    werknemer.id(),
                    naam,
                    voornaam,
                    email,
                    telefoon,
                    werknemer.geboortedatum(),
                    werknemer.rol(),
                    werknemer.status()
                );

                service.update(updateDTO);

                Platform.runLater(this::laadWerknemers);

            } catch (Exception ex) {
                toonFout("Fout bij opslaan wijziging: " + ex.getMessage());
                Platform.runLater(this::laadWerknemers);
            }
        }).start();
    }

    private void toonFout(String bericht) {
        Platform.runLater(() -> {
            foutLabel.setText(bericht);
            foutLabel.setVisible(true);
            foutLabel.setManaged(true);
        });
    }

    @FXML
    private void handleBack() {
        hogent.sdp2.sdpii.gui.router.Router.getInstance().navigeerNaar(hogent.sdp2.sdpii.gui.router.Scherm.ADMIN_HOME);
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
                toonFout("Fout bij laden van gebruikers: " + e.getMessage());
                Platform.runLater(() -> gebruikersTable.setPlaceholder(new Label("Fout bij laden.")));
            }
        }).start();
    }

    // --- STYLING HELPERS ---

    private void updateRoleStyle(ComboBox<String> combo, String rol) {
        String bg, border, text;
        switch (rol != null ? rol.toLowerCase() : "") {
            case "admin":
                bg = "#f3e8ff"; border = "#e9d5ff"; text = "#7e22ce"; break;
            case "manager":
                bg = "#dbeafe"; border = "#bfdbfe"; text = "#1d4ed8"; break;
            case "werknemer":
            default:
                bg = "#f1f5f9"; border = "#e2e8f0"; text = "#334155"; break;
        }

        combo.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-weight: bold; -fx-text-base-color: %s;",
            bg, border, text
        ));
    }

    private void updateStatusStyle(ComboBox<String> combo, String status) {
        String bg, border, text;
        switch (status != null ? status.toLowerCase() : "") {
            case "actief":
                bg = "#dcfce7"; border = "#bbf7d0"; text = "#166534"; break;
            case "geblokkeerd":
                bg = "#fee2e2"; border = "#fecaca"; text = "#b91c1c"; break;
            case "inactief":
            default:
                bg = "#fef3c7"; border = "#fde68a"; text = "#b45309"; break;
        }

        combo.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-weight: bold; -fx-text-base-color: %s;",
            bg, border, text
        ));
    }
}
