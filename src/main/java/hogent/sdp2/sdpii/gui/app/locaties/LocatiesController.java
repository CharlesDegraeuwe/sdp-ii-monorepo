package hogent.sdp2.sdpii.gui.app.locaties;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class LocatiesController extends BorderPane {
    public LocatiesController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/locaties/PlantsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTop(new PageTitleController("Locaties"));
    }
}
