package hogent.sdp2.sdpii.gui.components.app.header;

import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;
import hogent.sdp2.sdpii.gui.app.admin.AdminHomeController;
import hogent.sdp2.sdpii.gui.app.notifications.NotificationsController;
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

        Router(app);
    }

    //wsss iets van styleclass toevoegen fz
    public void triggerPopup() {
        if(isOpen) {

        }
    }
    private void Router(AppController app) {
        settings_trigger.setOnMouseClicked(e -> {
            NotificationsController nc = new NotificationsController();
            app.getSidebar().setActive(nc);
            app.navigateTo(nc, app.getBody());
        });

        admin_trigger.setOnMouseClicked(e -> {
            AdminHomeController ac = new AdminHomeController();
            app.navigateTo(ac, app.getBody());
        });

        logout_trigger.setOnMouseClicked(e -> {
            AccountController ac = new AccountController(app.getStage(), app);
            app.getSidebar().setActive(ac);
            app.navigateTo(ac, app.getBody());
        });

    }

}
