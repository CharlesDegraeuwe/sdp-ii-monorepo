package hogent.sdp2.sdpii.gui.app.afwezigheid;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.GeschiedenisItemDTO;
import domain.dto.WerknemerDTO;
import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class AfwezigheidsController extends BorderPane {

    // Toggle knoppen
    @FXML private Button verlofKnop;
    @FXML private Button ziekteKnop;
    @FXML private Button geschiedenisKnop;

    // Cards
    @FXML private VBox verlofCard;
    @FXML private VBox ziekteCard;
    @FXML private HBox geschiedenisCard;

    // Verlof velden
    @FXML private VBox verlofBox;
    @FXML private DatePicker verlofStartDatum;
    @FXML private DatePicker verlofEindDatum;
    @FXML private ComboBox<String> verlofTypeCombo;
    @FXML private TextArea extraUitlegArea;
    @FXML private Button indienKnop;

    // Ziekte velden
    @FXML private VBox ziekteBox;
    @FXML private TextField redenField;
    @FXML private DatePicker startDatumPicker;
    @FXML private DatePicker eindDatumPicker;
    @FXML private ToggleButton heleDagToggle;
    @FXML private HBox tijdBox;
    @FXML private Spinner<Integer> startUurSpinner;
    @FXML private Spinner<Integer> startMinuutSpinner;
    @FXML private Spinner<Integer> eindUurSpinner;
    @FXML private Spinner<Integer> eindMinuutSpinner;
    @FXML private Label certificaatLabel;
    @FXML private TextArea ziekteExtraArea;

    // Geschiedenis
    @FXML private VBox teamledenPanel;
    @FXML private VBox teamledenLijst;
    @FXML private VBox geschiedenisLijst;
    @FXML private Label geschiedenisTitel;

    // Feedback
    @FXML private Label foutLabel;
    @FXML private Label succesLabel;

    private byte[] certificaatBytes;
    private boolean isVerlof = true;

    private static final DateTimeFormatter DAG_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("nl", "BE"));

    public AfwezigheidsController(boolean verlof) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/afwezigheden/AbsensePage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTop(new PageTitleController("Afwezigheden"));
        if (verlof) {
            toonVerlof();
        } else {
            toonZiekte();
        }
    }

    @FXML
    public void initialize() {
        verlofTypeCombo.getItems().addAll("Jaarlijks verlof", "Onbetaald verlof", "Bijzonder verlof");
        verlofTypeCombo.setValue("Jaarlijks verlof");

        tijdBox.setVisible(false);
        tijdBox.setManaged(false);
        heleDagToggle.selectedProperty().addListener((obs, oud, nieuw) -> {
            tijdBox.setVisible(!nieuw);
            tijdBox.setManaged(!nieuw);
        });

        startUurSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8));
        startMinuutSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        eindUurSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 17));
        eindMinuutSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        foutLabel.setVisible(false);
        foutLabel.setManaged(false);
        succesLabel.setVisible(false);
        succesLabel.setManaged(false);

        // Iedereen kan eigen geschiedenis zien
        geschiedenisKnop.setVisible(true);
        geschiedenisKnop.setManaged(true);
    }

    // ─── TABS ────────────────────────────────────────────────────────────────────

    @FXML
    private void toonVerlof() {
        isVerlof = true;
        verlofCard.setVisible(true);   verlofCard.setManaged(true);
        ziekteCard.setVisible(false);  ziekteCard.setManaged(false);
        geschiedenisCard.setVisible(false); geschiedenisCard.setManaged(false);
        verlofKnop.getStyleClass().setAll("filter-knop-actief");
        ziekteKnop.getStyleClass().setAll("filter-knop");
        geschiedenisKnop.getStyleClass().setAll("filter-knop");
    }

    @FXML
    private void toonZiekte() {
        isVerlof = false;
        verlofCard.setVisible(false);  verlofCard.setManaged(false);
        ziekteCard.setVisible(true);   ziekteCard.setManaged(true);
        geschiedenisCard.setVisible(false); geschiedenisCard.setManaged(false);
        ziekteKnop.getStyleClass().setAll("filter-knop-actief");
        verlofKnop.getStyleClass().setAll("filter-knop");
        geschiedenisKnop.getStyleClass().setAll("filter-knop");
    }

    @FXML
    private void toonGeschiedenis() {
        verlofCard.setVisible(false);  verlofCard.setManaged(false);
        ziekteCard.setVisible(false);  ziekteCard.setManaged(false);
        geschiedenisCard.setVisible(true); geschiedenisCard.setManaged(true);
        geschiedenisKnop.getStyleClass().setAll("filter-knop-actief");
        verlofKnop.getStyleClass().setAll("filter-knop");
        ziekteKnop.getStyleClass().setAll("filter-knop");

        WerknemerDTO ingelogde = Sessie.getInstance().getIngelogdeWerknemer();
        boolean heeftTeamToegang = Sessie.getInstance().isMangerOrAdmin() || Sessie.getInstance().isSuperVisor();

        // Teamledenlijst links alleen zichtbaar voor manager/supervisor
        teamledenPanel.setVisible(heeftTeamToegang);
        teamledenPanel.setManaged(heeftTeamToegang);

        // Eigen geschiedenis altijd meteen laden
        geschiedenisTitel.setText("Mijn afwezigheidsgeschiedenis");
        laadGeschiedenisVanWerknemer(ingelogde);

        if (heeftTeamToegang) {
            laadTeamleden();
        }
    }

    // ─── GESCHIEDENIS ─────────────────────────────────────────────────────────────

    private void laadTeamleden() {
        WerknemerDTO ingelogde = Sessie.getInstance().getIngelogdeWerknemer();
        if (ingelogde == null) return;

        teamledenLijst.getChildren().clear();

        new Thread(() -> {
            try {
                List<WerknemerDTO> teamleden = Beheerder.getInstance()
                        .getGeschiedenisFacade()
                        .geefTeamledenVanManager(ingelogde.id());

                Platform.runLater(() -> {
                    // "Mijn geschiedenis" bovenaan
                    Button eigenKnop = new Button("Mijn geschiedenis");
                    eigenKnop.setMaxWidth(Double.MAX_VALUE);
                    eigenKnop.getStyleClass().addAll("teamlid-knop", "teamlid-knop-eigen");
                    eigenKnop.setOnAction(e -> {
                        geschiedenisTitel.setText("Mijn afwezigheidsgeschiedenis");
                        laadGeschiedenisVanWerknemer(ingelogde);
                    });
                    teamledenLijst.getChildren().add(eigenKnop);

                    if (teamleden.isEmpty()) {
                        Label leeg = new Label("Geen teamleden gevonden.");
                        leeg.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
                        teamledenLijst.getChildren().add(leeg);
                    } else {
                        for (WerknemerDTO w : teamleden) {
                            Button knop = new Button(w.voornaam() + " " + w.naam());
                            knop.setMaxWidth(Double.MAX_VALUE);
                            knop.getStyleClass().add("teamlid-knop");
                            knop.setOnAction(e -> laadGeschiedenisVanWerknemer(w));
                            teamledenLijst.getChildren().add(knop);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void laadGeschiedenisVanWerknemer(WerknemerDTO werknemer) {
        geschiedenisTitel.setText(werknemer.voornaam() + " " + werknemer.naam());
        geschiedenisLijst.getChildren().clear();

        Label laadLabel = new Label("Laden...");
        laadLabel.setStyle("-fx-text-fill: #aaaaaa;");
        geschiedenisLijst.getChildren().add(laadLabel);

        new Thread(() -> {
            try {
                List<GeschiedenisItemDTO> items = Beheerder.getInstance()
                        .getGeschiedenisFacade()
                        .geefGeschiedenisVanWerknemer(werknemer.id());

                Platform.runLater(() -> {
                    geschiedenisLijst.getChildren().clear();
                    if (items.isEmpty()) {
                        Label leeg = new Label("Geen geschiedenis gevonden.");
                        leeg.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");
                        geschiedenisLijst.getChildren().add(leeg);
                    } else {
                        for (GeschiedenisItemDTO item : items) {
                            geschiedenisLijst.getChildren().add(maakGeschiedenisRij(item));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private HBox maakGeschiedenisRij(GeschiedenisItemDTO item) {
        HBox rij = new HBox(12);
        rij.setAlignment(Pos.CENTER_LEFT);
        rij.setPadding(new Insets(10, 14, 10, 14));

        boolean isZiekte = item.type().equals("Ziekte");
        boolean isAfgewezen = "Afgewezen".equals(item.status());
        boolean isGeannuleerd = "Geannuleerd".equals(item.status());
        boolean isWachten = "In afwachting".equals(item.status());

        String achtergrond = isZiekte ? "rgba(227, 27, 53, 0.06)"
                : isAfgewezen || isGeannuleerd ? "rgba(150,150,150,0.08)"
                : isWachten ? "rgba(212, 172, 13, 0.08)"
                : "rgba(39, 174, 96, 0.06)";

        rij.setStyle("-fx-background-color: " + achtergrond + "; -fx-background-radius: 10;");

        // Type indicator
        Label typeLabel = new Label(isZiekte ? "Ziekte" : "Verlof");
        typeLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: "
                + (isZiekte ? "#E31B35" : isAfgewezen || isGeannuleerd ? "#888888" : isWachten ? "#a07d00" : "#1e8449")
                + "; -fx-background-color: " + achtergrond
                + "; -fx-background-radius: 6; -fx-padding: 3 8 3 8;");

        // Info
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        String datumTekst = item.startDatum().format(DAG_FORMAT)
                + (item.startDatum().equals(item.eindDatum()) ? "" : " – " + item.eindDatum().format(DAG_FORMAT));
        Label datumLabel = new Label(datumTekst);
        datumLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        if (item.omschrijving() != null && !item.omschrijving().isEmpty()) {
            Label omschrijvingLabel = new Label(item.omschrijving());
            omschrijvingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
            info.getChildren().addAll(datumLabel, omschrijvingLabel);
        } else {
            info.getChildren().add(datumLabel);
        }

        rij.getChildren().addAll(typeLabel, info);

        // Status badge
        if (item.status() != null) {
            Label statusLabel = new Label(item.status());
            String statusKleur = switch (item.status()) {
                case "Goedgekeurd" -> "#1e8449";
                case "Afgewezen" -> "#E31B35";
                case "Geannuleerd" -> "#888888";
                default -> "#a07d00";
            };
            statusLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + statusKleur
                    + "; -fx-background-radius: 999px; -fx-padding: 3 10 3 10;"
                    + "-fx-background-color: " + achtergrond + ";");
            rij.getChildren().add(statusLabel);
        }

        return rij;
    }

    // ─── VERLOF / ZIEKTE INDIENEN ────────────────────────────────────────────────

    @FXML
    private void uploadCertificaat() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Kies ziektebriefje");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Afbeeldingen", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File bestand = fileChooser.showOpenDialog(getScene().getWindow());
        if (bestand != null) {
            try {
                certificaatBytes = Files.readAllBytes(bestand.toPath());
                certificaatLabel.setText(bestand.getName());
            } catch (IOException e) {
                toonFout("Fout bij inlezen bestand: " + e.getMessage());
            }
        }
    }

    @FXML
    private void indienen() {
        foutLabel.setVisible(false); foutLabel.setManaged(false);
        succesLabel.setVisible(false); succesLabel.setManaged(false);

        WerknemerDTO werknemer = Sessie.getInstance().getIngelogdeWerknemer();
        if (werknemer == null) { toonFout("Geen gebruiker ingelogd."); return; }

        try {
            if (isVerlof) {
                Beheerder.getInstance().getVerlofFacade().vraagVerlofAan(
                        werknemer.id(),
                        verlofStartDatum.getValue(),
                        verlofEindDatum.getValue(),
                        verlofTypeCombo.getValue()
                );
                toonSucces("Verlofaanvraag succesvol ingediend!");
            } else {
                Beheerder.getInstance().getAfwezigheidFacade().meldAfwezigheid(
                        werknemer.id(),
                        startDatumPicker.getValue(),
                        eindDatumPicker.getValue(),
                        redenField.getText(),
                        certificaatBytes
                );
                toonSucces("Ziekte succesvol gemeld!");
            }
            resetForm();
        } catch (IllegalArgumentException e) {
            toonFout(e.getMessage());
        } catch (Exception e) {
            toonFout("Er is een fout opgetreden: " + e.getMessage());
        }
    }

    private void toonFout(String bericht) {
        foutLabel.setText(bericht);
        foutLabel.setVisible(true);
        foutLabel.setManaged(true);
    }

    private void toonSucces(String bericht) {
        succesLabel.setText(bericht);
        succesLabel.setVisible(true);
        succesLabel.setManaged(true);
    }

    private void resetForm() {
        verlofStartDatum.setValue(null);
        verlofEindDatum.setValue(null);
        verlofTypeCombo.setValue("Jaarlijks verlof");
        redenField.clear();
        startDatumPicker.setValue(null);
        eindDatumPicker.setValue(null);
        extraUitlegArea.clear();
        ziekteExtraArea.clear();
        heleDagToggle.setSelected(false);
        certificaatBytes = null;
        certificaatLabel.setText("");
    }
}