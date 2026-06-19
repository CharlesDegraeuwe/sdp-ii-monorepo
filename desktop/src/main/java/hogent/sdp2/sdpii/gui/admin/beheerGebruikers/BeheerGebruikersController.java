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

        // Standaard Tekstkolommen instellen
        naamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().naam()));
        voornaamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().voornaam()));
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
        telefoonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().telefoonnummer()));

        // --- ROL KOLOM (Interactieve Dropdown) ---
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
                    // 1. Schakel de actie tijdelijk uit om valse API calls te voorkomen
                    combo.setOnAction(null);

                    // Omzetten van Supervisor naar Werknemer voor de weergave
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
                                        Platform.runLater(() -> {
                                            foutLabel.setText("Fout bij updaten rol op de server.");
                                            foutLabel.setVisible(true);
                                            foutLabel.setManaged(true);
                                        });
                                    }
                                } catch (Exception ex) {
                                    Platform.runLater(() -> {
                                        foutLabel.setText("Connectiefout bij rol wijzigen: " + ex.getMessage());
                                        foutLabel.setVisible(true);
                                        foutLabel.setManaged(true);
                                    });
                                }
                            }).start();
                        }
                    });
                }
            }
        });

        // --- STATUS KOLOM (Interactieve Dropdown) ---
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
                    // 1. Schakel de actie tijdelijk uit
                    combo.setOnAction(null);

                    combo.setValue(status);
                    updateStatusStyle(combo, status);
                    setGraphic(combo);

                    // 2. Koppel de actie voor echte gebruikersinteractie
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
                                        Platform.runLater(() -> {
                                            foutLabel.setText("Fout bij updaten status.");
                                            foutLabel.setVisible(true);
                                            foutLabel.setManaged(true);
                                        });
                                    }
                                } catch (Exception ex) {
                                    Platform.runLater(() -> {
                                        foutLabel.setText("Connectiefout: " + ex.getMessage());
                                        foutLabel.setVisible(true);
                                        foutLabel.setManaged(true);
                                    });
                                }
                            }).start();
                        }
                    });
                }
            }
        });

        gebruikersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        statusFilterBox.getItems().addAll("Alle", "Actief", "Inactief", "Geblokkeerd");
        statusFilterBox.getSelectionModel().selectFirst();
        laadWerknemers();
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
                Platform.runLater(() -> {
                    foutLabel.setText("Fout bij laden van gebruikers: " + e.getMessage());
                    foutLabel.setVisible(true);
                    foutLabel.setManaged(true);
                    gebruikersTable.setPlaceholder(new Label("Fout bij laden."));
                });
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
