package hogent.sdp2.sdpii.gui.components.app.header;

import domain.Sessie;
import domain.Werknemer;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;
import hogent.sdp2.sdpii.gui.app.notifications.NotificationsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class HeaderController extends HBox {
    //vars
    @FXML private Button search;
    @FXML private Button notifications;
    @FXML private Button account;
    @FXML private HBox dropdown;
    @FXML private Label lblUsername;


    public HeaderController(AppController app) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/header/Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        Werknemer ingelogd = Sessie.getIngelogdeWerknemer();
        if (ingelogd != null) {
            lblUsername.setText(ingelogd.getNaam());
        } else {
            lblUsername.setText("Gast");
        }
        Router(app);
    }

    private void Router(AppController app) {
        notifications.setOnMouseClicked(e -> {
            NotificationsController nc = new NotificationsController();
            app.getSidebar().setActive(nc);
            app.navigateTo(nc, app.getBody());
        });

        account.setOnMouseClicked(e -> {
            AccountController ac = new AccountController(app.getStage(), app);
            app.getSidebar().setActive(ac);
            app.navigateTo(ac, app.getBody());
        });

    }
}
