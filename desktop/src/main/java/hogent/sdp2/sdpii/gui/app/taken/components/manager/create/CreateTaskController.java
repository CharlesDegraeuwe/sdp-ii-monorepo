package hogent.sdp2.sdpii.gui.app.taken.components.manager.create;

import domain.dto.LocatieDTO;
import domain.facades.LocatieFacade;
import domain.facades.TakenFacade;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateTaskController extends VBox {
    @FXML private TextField nameField;
    @FXML private TextArea specField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<LocatieDTO> locationPicker;

    // Tijd Pickers
    @FXML private ComboBox<String> startHourPicker;
    @FXML private ComboBox<String> startMinutePicker;
    @FXML private ComboBox<String> endHourPicker;
    @FXML private ComboBox<String> endMinutePicker;

    @FXML private Button createButton;
    @FXML private Label feedbackLabel;
    @FXML private Button cancelButton;

    private Runnable onAangemaakt;
    private Runnable onAnnuleer;

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

        vulTijdDropdowns();
        laadSites();

        // Standaard datums invullen (Vandaag)
        startDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now());

        createButton.setOnAction(e -> maakTaakAan(takenFacade));
        cancelButton.setOnAction(e -> {
            if (onAnnuleer != null) onAnnuleer.run();
        });
    }

    public void setOnAnnuleer(Runnable callback) {
        this.onAnnuleer = callback;
    }

    // =======================================================
    // DE NUCLEAIRE FIX VOOR DE ONZICHTBARE TEKST
    // =======================================================
    private void forceerZichtbareTekst(ComboBox<String> comboBox) {
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    // Dwingt JavaFX om dit donkergrijs te kleuren, negeert foute CSS
                    setStyle("-fx-text-fill: #1e293b; -fx-opacity: 1.0; -fx-font-size: 13px;");
                }
            }
        });
    }

    private void vulTijdDropdowns() {
        // Vul uren (00 - 23)
        List<String> uren = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            uren.add(String.format("%02d", i));
        }
        startHourPicker.getItems().setAll(uren);
        endHourPicker.getItems().setAll(uren);

        // Vul minuten (00 - 55 met stappen van 5)
        List<String> minuten = new ArrayList<>();
        for (int i = 0; i < 60; i += 5) {
            minuten.add(String.format("%02d", i));
        }
        startMinutePicker.getItems().setAll(minuten);
        endMinutePicker.getItems().setAll(minuten);

        // PAS DE FIX TOE OP ALLE 4 DE VELDEN
        forceerZichtbareTekst(startHourPicker);
        forceerZichtbareTekst(startMinutePicker);
        forceerZichtbareTekst(endHourPicker);
        forceerZichtbareTekst(endMinutePicker);

        // Selecteer standaard waardes
        startHourPicker.getSelectionModel().select("08");
        startMinutePicker.getSelectionModel().select("00");
        endHourPicker.getSelectionModel().select("17");
        endMinutePicker.getSelectionModel().select("00");
    }

    private void laadSites() {
        LocatieFacade siteFacade = new LocatieFacade();
        List<LocatieDTO> sites = siteFacade.geefAlleLocaties() != null ? siteFacade.geefAlleLocaties() : List.of();

        locationPicker.setConverter(new StringConverter<>() {
            @Override public String toString(LocatieDTO site) { return site != null ? site.naam() : ""; }
            @Override public LocatieDTO fromString(String s) { return null; }
        });

        locationPicker.getItems().setAll(sites);
        if (!sites.isEmpty()) locationPicker.getSelectionModel().selectFirst();

        // Zelfde truc voor de locatie picker, mocht die ooit raar doen!
        locationPicker.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LocatieDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Kies site...");
                    setStyle("-fx-text-fill: #94a3b8;");
                } else {
                    setText(item.naam());
                    setStyle("-fx-text-fill: #1e293b; -fx-opacity: 1.0;");
                }
            }
        });
    }

    private void maakTaakAan(TakenFacade takenFacade) {
        String naam = nameField.getText().trim();
        String specificaties = specField.getText().trim();
        LocalDate startDatum = startDatePicker.getValue();
        LocalDate eindDatum = dueDatePicker.getValue();
        LocatieDTO geselecteerdeSite = locationPicker.getValue();

        String startTijdStr = startHourPicker.getValue() + ":" + startMinutePicker.getValue();
        String eindTijdStr = endHourPicker.getValue() + ":" + endMinutePicker.getValue();

        if (naam.isEmpty()) {
            toonFeedback("Een taaknaam is verplicht.", true);
            return;
        }
        if (startDatum == null || eindDatum == null) {
            toonFeedback("Selecteer zowel een start- als einddatum.", true);
            return;
        }
        if (eindDatum.isBefore(startDatum)) {
            toonFeedback("De deadline kan niet vóór de startdatum liggen.", true);
            return;
        }
        if (geselecteerdeSite == null) {
            toonFeedback("Kies een locatie.", true);
            return;
        }

        int siteId = geselecteerdeSite.id();
        createButton.setDisable(true);

        new Thread(() -> {
            try {
                String resultaat = takenFacade.maakTaakAan(
                    naam,
                    specificaties,
                    eindDatum,
                    siteId,
                    startTijdStr,
                    eindTijdStr
                );

                Platform.runLater(() -> {
                    createButton.setDisable(false);
                    toonFeedback("Taak succesvol opgeslagen!", false);

                    // Reset formulieren
                    nameField.clear();
                    specField.clear();
                    startDatePicker.setValue(LocalDate.now());
                    dueDatePicker.setValue(LocalDate.now());

                    if (onAangemaakt != null) onAangemaakt.run();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    createButton.setDisable(false);
                    toonFeedback("Fout bij opslaan: " + ex.getMessage(), true);
                });
            }
        }).start();
    }

    private void toonFeedback(String bericht, boolean isError) {
        feedbackLabel.setText(bericht);
        feedbackLabel.setStyle(isError
            ? "-fx-text-fill: #b91c1c; -fx-background-color: #fee2e2; -fx-padding: 8 12; -fx-background-radius: 6;"
            : "-fx-text-fill: #047857; -fx-background-color: #d1fae5; -fx-padding: 8 12; -fx-background-radius: 6;");
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
    }
}
