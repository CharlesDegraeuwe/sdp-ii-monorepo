package hogent.sdp2.sdpii.gui.app.taken.components.items;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TaakItemController extends HBox {
    @FXML Label title;
    @FXML Label datum;
    public TaakItemController(String title, String datum) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/items/TaakItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.title.setText(title);
        this.datum.setText(datum);
    }
}
