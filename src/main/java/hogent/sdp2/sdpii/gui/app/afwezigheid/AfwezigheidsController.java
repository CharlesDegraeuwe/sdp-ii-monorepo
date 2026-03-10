package hogent.sdp2.sdpii.gui.app.afwezigheid;

import domain.Beheerder;
import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

public class AfwezigheidsController extends BorderPane {

    @FXML private Button verlofKnop;
    @FXML private Button ziekteKnop;
    @FXML private Button indienKnop;

    @FXML private VBox verlofBox;
    @FXML private DatePicker verlofStartDatum;
    @FXML private DatePicker verlofEindDatum;
    @FXML private ComboBox<String> verlofTypeCombo;

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

    @FXML private TextArea extraUitlegArea;
    @FXML private Label foutLabel;
    @FXML private Label succesLabel;

    private byte[] certificaatBytes;
    private boolean isVerlof = true;

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
    }

    @FXML
    private void toonVerlof() {
        isVerlof = true;
        verlofBox.setVisible(true);
        verlofBox.setManaged(true);
        ziekteBox.setVisible(false);
        ziekteBox.setManaged(false);
        verlofKnop.getStyleClass().setAll("filter-knop-actief");
        ziekteKnop.getStyleClass().setAll("filter-knop");
        indienKnop.setText("Verlof aanvragen");
    }

    @FXML
    private void toonZiekte() {
        isVerlof = false;
        verlofBox.setVisible(false);
        verlofBox.setManaged(false);
        ziekteBox.setVisible(true);
        ziekteBox.setManaged(true);
        ziekteKnop.getStyleClass().setAll("filter-knop-actief");
        verlofKnop.getStyleClass().setAll("filter-knop");
        indienKnop.setText("Ziekte melden");
    }

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
        foutLabel.setVisible(false);
        foutLabel.setManaged(false);
        succesLabel.setVisible(false);
        succesLabel.setManaged(false);

        WerknemerDTO werknemer = Sessie.getInstance().getIngelogdeWerknemer();
        if (werknemer == null) {
            toonFout("Geen gebruiker ingelogd.");
            return;
        }

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
        heleDagToggle.setSelected(false);
        certificaatBytes = null;
        certificaatLabel.setText("");
    }
}