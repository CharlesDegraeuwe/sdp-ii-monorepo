package hogent.sdp2.sdpii.gui.app.instellingen;
import javafx.fxml.FXMLLoader;
import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class InstellingenController extends BorderPane {
    public InstellingenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/instellingen/SettingsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        setTop(new PageTitleController("Instellingen"));

    }
}
