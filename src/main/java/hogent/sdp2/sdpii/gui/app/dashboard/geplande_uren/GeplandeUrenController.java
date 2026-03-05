package hogent.sdp2.sdpii.gui.app.dashboard.geplande_uren;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class GeplandeUrenController extends VBox {
    public GeplandeUrenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/geplande_uren/GeplandeUren.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
