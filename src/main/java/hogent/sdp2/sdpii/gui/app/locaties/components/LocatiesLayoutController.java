package hogent.sdp2.sdpii.gui.app.locaties.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LocatiesLayoutController extends VBox {
    public LocatiesLayoutController(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/locaties/components/LocatieLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
