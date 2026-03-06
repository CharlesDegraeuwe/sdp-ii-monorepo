package hogent.sdp2.sdpii.gui.app.planning.components.sidebar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PlanningTaakItemController extends HBox {
    @FXML
    CheckBox checkBox;
    @FXML
    Label lblNaam;
    @FXML Label lblDeadline;

    public PlanningTaakItemController(String naam, String deadline, boolean finished) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/PlanningTaakItem.fxml"));
        loader.setRoot(this); loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        lblNaam.setText(naam);
        lblDeadline.setText(deadline);
        checkBox.setSelected(finished);
        if (finished) {
            lblNaam.setStyle("-fx-text-fill: #aaaaaa; -fx-strikethrough: true;");
        }
    }
}