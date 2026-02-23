package hogent.sdp2.sdpii.gui.app.account;

import domain.Werknemer;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.app.AccountFormController;
import hogent.sdp2.sdpii.gui.components.app.header.StageHeaderController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountController extends BorderPane {

    private final AppController app;
    private final Stage stage;

    public AccountController(Stage stage, AppController app) {
        this.app = app;
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/AccountPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AccountFormController form = new AccountFormController(app);
        setCenter(form);
    }
}
