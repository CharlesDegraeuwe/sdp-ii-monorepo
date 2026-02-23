package hogent.sdp2.sdpii.gui.admin.home;

import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.admin.AdminHomeMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AdminHomeController extends BorderPane {
    public AdminHomeController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/admin/home/AdminHomePage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AdminHomeMenuController ac = new AdminHomeMenuController(app);
        setCenter(ac);
    }
}