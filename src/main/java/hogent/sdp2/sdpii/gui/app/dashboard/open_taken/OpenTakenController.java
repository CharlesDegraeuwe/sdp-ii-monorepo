package hogent.sdp2.sdpii.gui.app.dashboard.open_taken;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class OpenTakenController extends VBox {
    @FXML VBox itemContainer;
    public OpenTakenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/open_taken/OpenTaken.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        itemContainer.getChildren().add(new OpenTakenItemController("Taak 1", "13/04/26"));
        itemContainer.getChildren().add(new OpenTakenItemController("Taak 2", "13/04/26"));
    }
}
