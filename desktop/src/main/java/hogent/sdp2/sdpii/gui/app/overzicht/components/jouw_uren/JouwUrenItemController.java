package hogent.sdp2.sdpii.gui.app.overzicht.components.jouw_uren;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class JouwUrenItemController extends HBox {
    @FXML Label locatie;
    @FXML Label uur;
    @FXML Label datum;

    public JouwUrenItemController(String datum, String uur, String locatie, boolean afwijkend) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/jouw_uren/JouwUrenItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.datum.setText(datum);
        this.locatie.setText(locatie);
        this.uur.setText(uur);
        if (afwijkend) {
            this.uur.setStyle("-fx-text-fill: #B91C1C; -fx-font-weight: bold;");
        }
    }
}
