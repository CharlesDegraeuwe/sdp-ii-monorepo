package hogent.sdp2.sdpii.gui.admin.manage_users;

import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.admin.AdminHomeMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class ManageUsersController extends BorderPane {
    public ManageUsersController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/admin/manage_users/ManageUsers.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
