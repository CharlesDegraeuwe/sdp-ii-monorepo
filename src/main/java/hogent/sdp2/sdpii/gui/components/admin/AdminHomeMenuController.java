package hogent.sdp2.sdpii.gui.components.admin;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.admin.create_employee.CreateEmployeeController;
import hogent.sdp2.sdpii.gui.admin.create_manager.CreateManagerController;
import hogent.sdp2.sdpii.gui.admin.manage_users.ManageUsersController;
import hogent.sdp2.sdpii.gui.app.AppController;
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
    private AppController app;

    public AdminHomeMenuController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/admin/AdminHomeMenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.app = app;
        welcome_msg.setText("Welcome, " + Sessie.getInstance().getIngelogdeWerknemer().getNaam());
        this.Router();
    }

    private void Router() {
        rgstr_mngr.setOnMouseClicked(e -> { this.app.navigateTo(new CreateManagerController(this.app), this.app.getBody());  System.out.println("manager aanmaken geklikt");});
        rgstr_emp.setOnMouseClicked(e -> { this.app.navigateTo(new CreateEmployeeController(this.app), this.app.getBody());System.out.println("employee aanmaken geklikt");});
        mng_usr.setOnMouseClicked(e -> { this.app.navigateTo(new ManageUsersController(), this.app.getBody());System.out.println("users beheren geklikt");});

    }
}
