package hogent.sdp2.sdpii.gui.components.auth;

import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashController extends VBox {
    private MainFrameController mf;
    private Stage stage;
    public SplashController(Stage stage, MainFrameController mf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/auth/Splash.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //toekenningen
        this.mf = mf;
        this.stage = stage;

    }


    private void navigeerNaarActivatie() {

        this.mf.setCenter(new AppController(stage, mf)
        );
    }
}
