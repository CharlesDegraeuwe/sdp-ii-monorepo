package hogent.sdp2.sdpii.gui.components.app.header;

import domain.auth.Sessie;

import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
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

    public ProfilePopupController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/header/ProfilePopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }


        if(!Sessie.getInstance().userRole().equals("Manager") && !Sessie.getInstance().isAdmin()) {
            admin_trigger.setVisible(false);
            admin_trigger.setManaged(false);
        }

        Router();
    }

    private void Router() {
        settings_trigger.setOnMouseClicked(e -> {
            Router.getInstance().navigeerNaar(Scherm.INSTELLINGEN);

        });

        admin_trigger.setOnMouseClicked(e -> {
            Router.getInstance().navigeerNaar(Scherm.INSTELLINGEN);
        });

        logout_trigger.setOnMouseClicked(e -> {
            Router.getInstance().navigeerNaar(Scherm.LOGOUT);
        });

    }

}