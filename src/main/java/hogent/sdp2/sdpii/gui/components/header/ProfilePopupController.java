package hogent.sdp2.sdpii.gui.components.header;

import hogent.sdp2.sdpii.gui.MainFrameController;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class ProfilePopupController {
    public ProfilePopupController(MainFrameController mainframe) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/header/Header.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

    }
}
