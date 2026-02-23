package hogent.sdp2.sdpii.gui.components.app.header;

import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.AppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ProfilePopupController extends VBox {
    Boolean isOpen = false;

    public ProfilePopupController(AppController app) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/header/ProfilePopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

    }

    //wsss iets van styleclass toevoegen fz
    public void triggerPopup() {
        if(isOpen) {

        }
    }
}
