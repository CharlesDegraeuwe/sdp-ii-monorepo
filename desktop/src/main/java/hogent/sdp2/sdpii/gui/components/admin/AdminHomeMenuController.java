package hogent.sdp2.sdpii.gui.components.admin;


import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdminHomeMenuController extends VBox {
    @FXML Label welcome_msg;
    @FXML HBox rgstr_mngr;
    @FXML HBox rgstr_emp;
    @FXML HBox mng_usr;
    @FXML HBox log;

    public AdminHomeMenuController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/admin/AdminHomeMenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        welcome_msg.setText("Welcome, " + Sessie.getInstance().getIngelogdeWerknemer().voornaam());
        this.Router();
    }

    private void Router() {
        rgstr_mngr.setOnMouseClicked(e ->  {Router.getInstance().navigeerNaar(Scherm.CREEER_MANAGER);});
        rgstr_emp.setOnMouseClicked(e ->  {Router.getInstance().navigeerNaar(Scherm.CREEER_MEDEWERKER);});
        mng_usr.setOnMouseClicked(e ->  {Router.getInstance().navigeerNaar(Scherm.BEHEER_GEBRUIKERS);});
        log.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.VIEW_LOGS));
    }
}
