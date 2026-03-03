package hogent.sdp2.sdpii.gui.app.afwezigheid;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AfwezigheidsController extends BorderPane {
    public AfwezigheidsController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/AbsensePage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTop(new PageTitleController("Afwezigheden"));
    }
}
