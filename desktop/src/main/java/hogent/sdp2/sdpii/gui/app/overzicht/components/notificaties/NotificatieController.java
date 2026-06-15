package hogent.sdp2.sdpii.gui.app.overzicht.components.notificaties;

import domain.auth.Sessie;
import domain.dto.NotificatieDTO;
import domain.facades.NotificatieFacade;
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
import java.util.List;
import java.util.stream.Collectors;

public class NotificatieController extends VBox {

    @FXML VBox item_container;
    @FXML ScrollPane scrollPane;
    @FXML HBox footer;
    @FXML Label footerLabel;

    public NotificatieController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/notificaties/Notifications.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);

        footer.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.NOTIFICATIES));
        footer.setCursor(Cursor.HAND);

        laadNotificaties();
    }

    private void laadNotificaties() {
        if (Sessie.getInstance().getIngelogdeWerknemer() == null) return;
        int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();

        new Thread(() -> {
            try {
                List<NotificatieDTO> ongelezen = new NotificatieFacade()
                        .geefNotificaties(werknemerId)
                        .stream()
                        .filter(n -> "Nee".equalsIgnoreCase(n.gelezen()))
                        .collect(Collectors.toList());

                Platform.runLater(() -> toonNotificaties(ongelezen));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void toonNotificaties(List<NotificatieDTO> lijst) {
        item_container.getChildren().clear();

        if (lijst.isEmpty()) {
            Label lbl = new Label("Geen ongelezen notificaties.");
            lbl.setStyle("-fx-text-fill: #9CA3AF; -fx-font-style: italic; -fx-font-size: 11px; -fx-padding: 4 0 0 2;");
            item_container.getChildren().add(lbl);
            updateFooterLabel(0);
            return;
        }

        for (NotificatieDTO n : lijst) {
            item_container.getChildren().add(new NotificatieItemController(n, this::laadNotificaties));
        }
        updateFooterLabel(lijst.size());
    }

    private void updateFooterLabel(int aantalOngelezen) {
        if (aantalOngelezen > 0) {
            footerLabel.setText("Bekijk alle notificaties (" + aantalOngelezen + ")");
        } else {
            footerLabel.setText("Bekijk alle notificaties");
        }
    }
}
