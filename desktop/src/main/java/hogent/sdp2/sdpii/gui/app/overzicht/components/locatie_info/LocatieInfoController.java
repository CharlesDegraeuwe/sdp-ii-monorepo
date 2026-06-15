package hogent.sdp2.sdpii.gui.app.overzicht.components.locatie_info;

import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LocatieInfoController extends VBox {

    @FXML Button btnKaart;

    public LocatieInfoController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/locatie_info/LocatieInfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        btnKaart.setCursor(Cursor.HAND);
        btnKaart.setOnAction(e -> Router.getInstance().navigeerNaar(Scherm.LOCATIES));
    }
}
