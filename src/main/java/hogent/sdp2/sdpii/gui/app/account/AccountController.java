package hogent.sdp2.sdpii.gui.app.account;

import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.components.app.AccountFormController;
import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AccountController extends BorderPane {

    private final AppController app;

    public AccountController(AppController app) {
        this.app = app;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/afwezigheden/AccountPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AccountFormController form = new AccountFormController();

        setTop(new PageTitleController("Account"));

        setCenter(form);
    }
}
