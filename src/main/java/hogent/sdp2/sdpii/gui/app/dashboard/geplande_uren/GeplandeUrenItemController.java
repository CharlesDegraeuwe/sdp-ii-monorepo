package hogent.sdp2.sdpii.gui.app.dashboard.geplande_uren;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class GeplandeUrenItemController extends HBox {
    public GeplandeUrenItemController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/geplande_uren/GeplandeUrenItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
