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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/Notifications.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.init();


    }

    private void init() {
        see_more.setOnMouseClicked(e -> {
            Router.getInstance().navigeerNaar(Scherm.NOTIFICATIES);});
    }
}
