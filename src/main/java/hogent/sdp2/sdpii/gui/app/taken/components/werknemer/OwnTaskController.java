package hogent.sdp2.sdpii.gui.app.taken.components.werknemer;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.facades.TakenFacade;
import hogent.sdp2.sdpii.gui.app.taken.components.items.FinishedTaakItem;
import hogent.sdp2.sdpii.gui.app.taken.components.items.TaakItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OwnTaskController extends BorderPane {
    @FXML Label titel_taken;
    @FXML Label afgewerkte_taken_titel;
    @FXML Label edit_button;
    @FXML VBox finished_task_container;
    @FXML VBox belangrijk_container;
    @FXML VBox vandaag_container;
    @FXML VBox morgen_container;
    @FXML VBox detail_card;
    @FXML Label detail_deadline;
    @FXML Label detail_locatie;
    @FXML TextField detail_titel;
    @FXML TextArea detail_beschrijving;
    @FXML Button opslaan_button;

    private TaakDTO geselecteerdeTaak;
    private TakenFacade takenFacade;

    public OwnTaskController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/werknemer/OwnTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        init(takenFacade);
    }

    private void toonDetails(TaakDTO taak) {
        geselecteerdeTaak = taak;
        detail_deadline.setText("Deadline: " + formatDeadline(taak.deadline()));
        detail_titel.setText(taak.naam());
        detail_beschrijving.setText(taak.specificaties() != null ? taak.specificaties() : "");
        if (taak.locatie() != null && !taak.locatie().isBlank()) {
            detail_locatie.setText(taak.locatie());
            detail_locatie.setVisible(true);
            detail_locatie.setManaged(true);
        } else {
            detail_locatie.setVisible(false);
            detail_locatie.setManaged(false);
        }
        zetEditModusUit();
        detail_card.setVisible(true);
        detail_card.setManaged(true);
    }

    public void verbergDetails() {
        detail_card.setVisible(false);
        detail_card.setManaged(false);
        geselecteerdeTaak = null;
    }

    private void zetEditModusAan() {
        detail_titel.setEditable(true);
        detail_beschrijving.setEditable(true);
        opslaan_button.setVisible(true);
        opslaan_button.setManaged(true);
        edit_button.setText("annuleer");
    }

    private void zetEditModusUit() {
        detail_titel.setEditable(false);
        detail_beschrijving.setEditable(false);
        opslaan_button.setVisible(false);
        opslaan_button.setManaged(false);
        edit_button.setText("edit");
    }

    public void init(TakenFacade takenFacade) {
        this.takenFacade = takenFacade;
        titel_taken.setText(Sessie.getInstance().getIngelogdeWerknemer().voornaam() + "'s taken:");
        afgewerkte_taken_titel.setText(Sessie.getInstance().getIngelogdeWerknemer().voornaam() + "'s afgewerkte taken:");
        boolean rol = Sessie.getInstance().isWerknemer();
        edit_button.setVisible(!rol);

        edit_button.setOnMouseClicked(e -> {
            if (opslaan_button.isVisible()) {
                zetEditModusUit();
                if (geselecteerdeTaak != null) toonDetails(geselecteerdeTaak);
            } else {
                zetEditModusAan();
            }
        });

        opslaan_button.setOnMouseClicked(e -> {
            if (geselecteerdeTaak == null) return;
            takenFacade.wijzigTaak(geselecteerdeTaak.id(), detail_titel.getText(), detail_beschrijving.getText());
            zetEditModusUit();
        });

        herlaad();
    }

    public void herlaad() {
        new Thread(() -> {
            List<TaakDTO> taken = takenFacade.geefEigenTaken();
            javafx.application.Platform.runLater(() -> vulIn(taken));
        }).start();
    }

    private void vulIn(List<TaakDTO> taken) {
        belangrijk_container.getChildren().clear();
        vandaag_container.getChildren().clear();
        morgen_container.getChildren().clear();
        finished_task_container.getChildren().clear();
        verbergDetails();

        LocalDate vandaag = LocalDate.now();

        for (TaakDTO taak : taken) {
            LocalDate deadline = parseDeadline(taak.deadline());
            String deadlineTekst = "Deadline: " + (deadline != null
                    ? deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "—");
            if (!taak.isAfgewerkt()) {
                TaakItemController item = new TaakItemController(taak.naam(), deadlineTekst);
                item.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> toonDetails(taak));
                item.setOnVerwijder(() -> {
                    takenFacade.verwijderTaak(taak.id());
                    VBox parent = (VBox) item.getParent();
                    if (parent != null) parent.getChildren().remove(item);
                });
                item.setOnAfgewerkt(() -> {
                    takenFacade.markeerAfgewerkt(taak.id());
                    VBox parent = (VBox) item.getParent();
                    if (parent != null) parent.getChildren().remove(item);
                    finished_task_container.getChildren().add(new FinishedTaakItem(taak.naam(), "Afgewerkt op " + deadlineTekst));
                });
                if (deadline == null || deadline.isBefore(vandaag)) {
                    belangrijk_container.getChildren().add(item);
                } else if (deadline.isEqual(vandaag)) {
                    vandaag_container.getChildren().add(item);
                } else {
                    morgen_container.getChildren().add(item);
                }
            } else {
                finished_task_container.getChildren().add(new FinishedTaakItem(taak.naam(), "Afgewerkt op " + deadlineTekst));
            }
        }
    }

    private static LocalDate parseDeadline(String deadline) {
        if (deadline == null || deadline.length() < 10) return null;
        try { return LocalDate.parse(deadline.substring(0, 10)); } catch (Exception e) { return null; }
    }

    private static String formatDeadline(String deadline) {
        LocalDate d = parseDeadline(deadline);
        return d != null ? d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "—";
    }
}
