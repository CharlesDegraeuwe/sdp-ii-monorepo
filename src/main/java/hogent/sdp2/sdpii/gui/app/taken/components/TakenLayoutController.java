package hogent.sdp2.sdpii.gui.app.taken.components;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TakenLayoutController extends VBox {
    public TakenLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/TakenLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
