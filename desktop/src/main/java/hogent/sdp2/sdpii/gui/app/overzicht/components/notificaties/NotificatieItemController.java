package hogent.sdp2.sdpii.gui.app.overzicht.components.notificaties;

import domain.dto.NotificatieDTO;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NotificatieItemController extends HBox {

    @FXML HBox container;
    @FXML VBox icon;
    @FXML Label title;

    public NotificatieItemController(NotificatieDTO dto) {
        this(dto.titel(), "#aaaaaa", Scherm.NOTIFICATIES);
    }

    public NotificatieItemController(String titel, String iconColor, Scherm navigeerNaar) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/notificaties/NotificationItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        title.setText(titel);
        icon.setStyle("-fx-background-color: " + iconColor + "; -fx-background-radius: 999px;");
        container.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(navigeerNaar));
    }



    private Map<String, String> colors() {
        Map<String, String> colors = new HashMap<>();
        colors.put("work", "99DA64");
        colors.put("absence", "DA6464");
        colors.put("holiday", "F6B74B");
        colors.put("team", "4B8AF6");
        return colors;
    }
}
