package hogent.sdp2.sdpii.gui.app.planning.components.kalender;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PlanningShiftTagController extends HBox {
    @FXML
    Label lblShift;

    public PlanningShiftTagController(String tekst, boolean isVacation) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/PlanningShiftTag.fxml"));
        loader.setRoot(this); loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        lblShift.setText(tekst);
        this.getStyleClass().add(isVacation ? "shift_tag_vacation" : "shift_tag_shift");
        this.setMaxWidth(Double.MAX_VALUE);
    }
}
