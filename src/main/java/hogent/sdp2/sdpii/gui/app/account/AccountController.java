package hogent.sdp2.sdpii.gui.app.account;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class AccountController extends GridPane {
    public AccountController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/AccountPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
