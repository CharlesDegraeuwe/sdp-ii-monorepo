package hogent.sdp2.sdpii.gui.app.taken.components.items;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AssignMemberItemController extends HBox {
    @FXML Label title;
    @FXML HBox taak_container;
    public AssignMemberItemController(String title, String kleur) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/items/AssignMemberItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.title.setText(title);
        this.taak_container.setStyle("-fx-background-color: " + kleur);
    }
}
