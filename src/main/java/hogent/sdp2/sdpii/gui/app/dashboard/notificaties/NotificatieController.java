package hogent.sdp2.sdpii.gui.app.dashboard.notificaties;

import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class NotificatieController extends VBox {
    @FXML Button see_more;
    @FXML VBox item_container;

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
        this.init();


    }

    private void init() {
        for(int i = 0; i < 4; i++) {
            item_container.getChildren().add(new NotificatieItemController());
        }

        see_more.setOnMouseClicked(e -> {
            Router.getInstance().navigeerNaar(Scherm.NOTIFICATIES);});
    }
}
