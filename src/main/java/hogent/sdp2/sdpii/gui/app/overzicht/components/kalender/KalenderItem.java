package hogent.sdp2.sdpii.gui.app.overzicht.components.kalender;

import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class KalenderItem extends HBox {
    @FXML Label dag;
    @FXML HBox container;

    public KalenderItem(int dag){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/kalender/KalenderItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.dag.setText(String.valueOf(dag));
        this.container.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.PLANNING));
    }
}
