package hogent.sdp2.sdpii.gui.components.header;

import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;
import hogent.sdp2.sdpii.gui.app.notifications.NotificationsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class HeaderController extends HBox {
    //vars
    @FXML private Button search;
    @FXML private Button notifications;
    @FXML private Button account;
    @FXML private HBox dropdown;

    public HeaderController(MainFrameController mainframe) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/header/Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        Router(mainframe);
    }

    private void Router(MainFrameController mainFrame) {
        notifications.setOnMouseClicked(e -> {
            NotificationsController nc = new NotificationsController();
            mainFrame.getSidebar().setActive(nc);
            mainFrame.navigateTo(nc, mainFrame.getBody());
        });

        account.setOnMouseClicked(e -> {
            AccountController ac = new AccountController();
            mainFrame.getSidebar().setActive(ac);
            mainFrame.navigateTo(ac, mainFrame.getBody());
        });

    }
}
