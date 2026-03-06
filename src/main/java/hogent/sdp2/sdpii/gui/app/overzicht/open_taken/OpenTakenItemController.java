package hogent.sdp2.sdpii.gui.app.overzicht.open_taken;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class OpenTakenItemController extends HBox {
    @FXML CheckBox checkBox;
    @FXML Label title;
    @FXML Label datum;

    public OpenTakenItemController(String taskTitle, String date) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/open_taken/OpenTakenItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        title.setText(taskTitle);
        datum.setText(date);
    }
}
