package hogent.sdp2.sdpii.gui.app.overzicht.uren;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class UrenItemController extends HBox {
    @FXML CheckBox checkBox;
    @FXML Label title;
    @FXML Label datum;

    public UrenItemController(String itemTitle, String uur) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/uren/GeplandeUrenItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        title.setText(itemTitle);
        datum.setText(uur);
    }
}
