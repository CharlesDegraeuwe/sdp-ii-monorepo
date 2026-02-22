package hogent.sdp2.sdpii.gui.app.account;

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

    private final MainFrameController mf;
    private final Stage stage;

    public AccountController(Stage stage, MainFrameController mf) {
        this.mf = mf;
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/AccountPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AccountFormController form = new AccountFormController(mf, stage);
        StageHeaderController controls = new StageHeaderController(stage);
        setCenter(form);
        setTop(controls);
    }
}
