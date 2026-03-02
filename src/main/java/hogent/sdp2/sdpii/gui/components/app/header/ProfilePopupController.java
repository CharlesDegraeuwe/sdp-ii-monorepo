package hogent.sdp2.sdpii.gui.components.app.header;

import domain.Sessie;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.admin.home.AdminHomeController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;
import hogent.sdp2.sdpii.gui.app.notifications.NotificationsController;
import hogent.sdp2.sdpii.gui.app.settings.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ProfilePopupController extends VBox {
    Boolean isOpen = false;
    @FXML HBox settings_trigger;
    @FXML HBox admin_trigger;
    @FXML HBox logout_trigger;

    public ProfilePopupController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/header/ProfilePopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        if(!Sessie.userRole().equals("Manager") && !Sessie.isAdmin()) {
            admin_trigger.setVisible(false);
            admin_trigger.setManaged(false);
        }

        Router(app);
    }

    private void Router(AppController app) {
        settings_trigger.setOnMouseClicked(e -> {
            SettingsController nc = new SettingsController();
            app.getSidebar().setActive(nc);
            app.navigateTo(nc, app.getBody());
        });

        admin_trigger.setOnMouseClicked(e -> {
            AdminHomeController ac = new AdminHomeController(app);
            app.navigateTo(ac, app.getBody());
        });

        logout_trigger.setOnMouseClicked(e -> {
            Sessie.uitloggen();
            app.getMainframe().getLogin().getForm().reset();
            app.getMainframe().setCenter(app.getMainframe().getLogin());
        });

    }

}
