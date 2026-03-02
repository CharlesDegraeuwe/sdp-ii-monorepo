package hogent.sdp2.sdpii.gui.components.app.header;

import domain.auth.Sessie;
import domain.werknemer.Werknemer;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;
import hogent.sdp2.sdpii.gui.app.notifications.NotificationsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class HeaderController extends HBox {
    //vars
    @FXML private Button search;
    @FXML private Button notifications;
    @FXML private Button account;
    @FXML private Button dropdown_button;
    @FXML private FontIcon button_icon;
    @FXML private HBox trigger_button;
    @FXML private Label lblUsername;
    private ProfilePopupController pfc;
    private Popup popup;


    public HeaderController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/header/Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        pfc = new ProfilePopupController(app);

        // Popup aanmaken
        popup = new Popup();
        popup.setAutoHide(true); // sluit als je ergens anders klikt
        popup.getContent().add(pfc);

        // Trigger op dropdown button
        trigger_button.setOnMouseClicked(e -> togglePopup(trigger_button));
        Werknemer ingelogd = Sessie.getIngelogdeWerknemer();
        if (ingelogd != null) {
            lblUsername.setText(ingelogd.getNaam());
        } else {
            lblUsername.setText("Gast");
        }
        Router(app);
    }

    private void togglePopup(HBox trigger) {
        if (popup.isShowing()) {
            popup.hide();
        } else {
            var bounds = trigger.localToScreen(trigger.getBoundsInLocal());
            popup.show(
                    trigger,
                    bounds.getMaxX() -190,
                    bounds.getMaxY() -10
            );
        }
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
