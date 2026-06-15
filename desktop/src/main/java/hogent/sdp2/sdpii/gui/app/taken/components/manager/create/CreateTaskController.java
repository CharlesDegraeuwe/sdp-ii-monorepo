package hogent.sdp2.sdpii.gui.app.taken.components.manager.create;

import domain.dto.LocatieDTO;
import domain.facades.LocatieFacade;
import domain.facades.TakenFacade;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CreateTaskController extends BorderPane {
    @FXML TextField nameField;
    @FXML TextArea specField;
    @FXML TextField dueDateField;
    @FXML ComboBox<LocatieDTO> locationPicker;
    @FXML Button createButton;
    @FXML Label feedbackLabel;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private Runnable onAangemaakt;

    public void setOnAangemaakt(Runnable callback) {
        this.onAangemaakt = callback;
    }

    public CreateTaskController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/create/CreateTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        laadSites();
        createButton.setOnAction(e -> maakTaakAan(takenFacade));
    }

    private void laadSites() {
        LocatieFacade siteFacade = new LocatieFacade();
        List<LocatieDTO> sites = siteFacade.geefAlleLocaties();

        locationPicker.setConverter(new StringConverter<>() {
            @Override public String toString(LocatieDTO site) { return site != null ? site.naam() : ""; }
            @Override public LocatieDTO fromString(String s) { return null; }
        });

        locationPicker.getItems().setAll(sites);
        if (!sites.isEmpty()) locationPicker.getSelectionModel().selectFirst();
    }

    private void maakTaakAan(TakenFacade takenFacade) {
        String naam = nameField.getText().trim();
        String specificaties = specField.getText().trim();
        String datumTekst = dueDateField.getText().trim();
        LocatieDTO geselecteerdeSite = locationPicker.getValue();

        // Datum parsing blijft in GUI (is UI-formatting, geen business rule)
        LocalDate deadline;
        try {
            deadline = LocalDate.parse(datumTekst, FORMATTER);
        } catch (DateTimeParseException ex) {
            toonFeedback("Ongeldige datum. Gebruik dd-mm-jjjj.", true);
            return;
        }

        int siteId = geselecteerdeSite != null ? geselecteerdeSite.id() : 0;
        createButton.setDisable(true);
        new Thread(() -> {
            try {
                String resultaat = takenFacade.maakTaakAan(naam, specificaties, deadline, siteId);
                Platform.runLater(() -> {
                    createButton.setDisable(false);
                    toonFeedback(resultaat, false);
                    nameField.clear();
                    specField.clear();
                    dueDateField.clear();
                    if (onAangemaakt != null) onAangemaakt.run();
                });
            } catch (IllegalArgumentException ex) {
                Platform.runLater(() -> {
                    createButton.setDisable(false);
                    toonFeedback(ex.getMessage(), true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    createButton.setDisable(false);
                    toonFeedback("Fout: " + ex.getMessage(), true);
                });
            }
        }).start();
    }

    private void toonFeedback(String bericht, boolean isError) {
        feedbackLabel.setText(bericht);
        feedbackLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        feedbackLabel.setVisible(true);
    }
}