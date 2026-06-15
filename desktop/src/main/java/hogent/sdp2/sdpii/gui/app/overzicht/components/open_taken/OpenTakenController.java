package hogent.sdp2.sdpii.gui.app.overzicht.components.open_taken;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.facades.TakenFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OpenTakenController extends VBox {

    @FXML VBox itemContainer;
    @FXML ScrollPane scrollPane;
    @FXML HBox footer;

    private static final DateTimeFormatter FORMAAT = DateTimeFormatter.ofPattern("d MMM", new java.util.Locale("nl"));

    public OpenTakenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/open_taken/OpenTaken.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        footer.setCursor(Cursor.HAND);
        footer.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.TAKEN));

        sceneProperty().addListener((obs, old, nw) -> {
            if (nw != null) laadTaken();
        });
    }

    private void laadTaken() {
        new Thread(() -> {
            try {
                TakenFacade facade = new TakenFacade();
                List<TaakDTO> taken;
                if (Sessie.getInstance().isWerknemer() || Sessie.getInstance().isSuperVisor()) {
                    taken = facade.geefEigenTaken();
                } else {
                    taken = facade.geefAlleTaken();
                }
                List<TaakDTO> open = taken.stream()
                        .filter(t -> !t.isAfgewerkt())
                        .toList();
                Platform.runLater(() -> toonTaken(open));
            } catch (Exception e) {
                Platform.runLater(() -> toonLeeg());
            }
        }).start();
    }

    private void toonTaken(List<TaakDTO> taken) {
        itemContainer.getChildren().clear();
        if (taken.isEmpty()) {
            toonLeeg();
            return;
        }
        for (TaakDTO t : taken) {
            itemContainer.getChildren().add(maakTaakRij(t));
        }
    }

    private VBox maakTaakRij(TaakDTO t) {
        java.time.LocalDate dl = t.deadline() != null && t.deadline().length() >= 10
                ? tryParse(t.deadline()) : null;
        boolean isUrgent = dl != null && !dl.isAfter(LocalDate.now());

        String achtergrond = isUrgent ? "#FEF2F2" : "rgba(255,255,255,0.7)";
        String rand        = isUrgent ? "rgba(252,165,165,0.7)" : "rgba(209,213,219,0.6)";
        String dotKleur    = isUrgent ? "#F87171" : "#D1D5DB";
        String titelKleur  = isUrgent ? "#B91C1C" : "#1F2937";
        String deadlineKleur = isUrgent ? "#F87171" : "#9CA3AF";

        javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(3.5,
                javafx.scene.paint.Color.web(dotKleur));

        Label lblTitel = new Label(t.naam());
        lblTitel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + titelKleur + ";");
        lblTitel.setMaxWidth(160);

        Label lblDeadline = new Label(dl != null ? dl.format(FORMAAT) : "");
        lblDeadline.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + deadlineKleur + ";");

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox rij = new HBox(6, dot, lblTitel, spacer, lblDeadline);
        rij.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        rij.setPadding(new javafx.geometry.Insets(7, 10, 7, 10));
        rij.setStyle(String.format(
                "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                achtergrond, rand));
        rij.setCursor(javafx.scene.Cursor.HAND);
        rij.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.TAKEN));

        VBox wrapper = new VBox(rij);
        return wrapper;
    }

    private static java.time.LocalDate tryParse(String d) {
        try { return java.time.LocalDate.parse(d.substring(0, 10)); } catch (Exception e) { return null; }
    }

    private void toonLeeg() {
        itemContainer.getChildren().clear();
        Label lbl = new Label("Geen openstaande taken.");
        lbl.setStyle("-fx-text-fill: #9CA3AF; -fx-font-style: italic; -fx-font-size: 11px; -fx-padding: 4 0 0 2;");
        itemContainer.getChildren().add(lbl);
    }
}
