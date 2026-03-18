package hogent.sdp2.sdpii.gui.app.taken.components.manager.create;

import domain.Beheerder;
import domain.dto.LocatieDTO;
import domain.facades.SiteFacade;
import domain.facades.TakenFacade;
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
        SiteFacade siteFacade = Beheerder.getInstance().getSiteFacade();
        List<LocatieDTO> sites = siteFacade.geefSitesVanManager();

        locationPicker.setConverter(new StringConverter<>() {
            @Override public String toString(LocatieDTO site) { return site != null ? site.naam() : ""; }
            @Override public LocatieDTO fromString(String s) { return null; }
        });

        locationPicker.getItems().setAll(sites);
        if (!sites.isEmpty()) locationPicker.getSelectionModel().selectFirst();
    }

    private void maakTaakAan(TakenFacade takenFacade) {
        String titel = nameField.getText().trim();
        String beschrijving = specField.getText().trim();
        String datumTekst = dueDateField.getText().trim();
        LocatieDTO geselecteerdeSite = locationPicker.getValue();

        if (titel.isEmpty() || beschrijving.isEmpty() || datumTekst.isEmpty() || geselecteerdeSite == null) {
            toonFeedback("Vul alle velden in.", true);
            return;
        }

        LocalDate deadline;
        try {
            deadline = LocalDate.parse(datumTekst, FORMATTER);
        } catch (DateTimeParseException ex) {
            toonFeedback("Ongeldige datum. Gebruik dd-mm-jjjj.", true);
            return;
        }

        try {
            String resultaat = takenFacade.maakTaakAan(titel, beschrijving, deadline, geselecteerdeSite.id());
            toonFeedback(resultaat, false);
            nameField.clear();
            specField.clear();
            dueDateField.clear();
        } catch (Exception ex) {
            toonFeedback("Fout: " + ex.getMessage(), true);
        }
    }

    private void toonFeedback(String bericht, boolean isError) {
        feedbackLabel.setText(bericht);
        feedbackLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        feedbackLabel.setVisible(true);
    }
}