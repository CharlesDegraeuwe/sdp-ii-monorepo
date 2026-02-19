package hogent.sdp2.sdpii.gui.components.app;

import hogent.sdp2.sdpii.gui.MainFrameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class BodyController extends BorderPane {
    public BodyController(MainFrameController mainFrame) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/Body.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

    }
}
