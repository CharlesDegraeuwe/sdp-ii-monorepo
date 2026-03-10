package hogent.sdp2.sdpii.gui.app.planning.components.sidebar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class PlanningShiftTodayItemController extends HBox {
    @FXML
    FontIcon icon;
    @FXML
    Label lblTijd;
    @FXML Label lblLocatie;

    public PlanningShiftTodayItemController(String tijd, String locatie) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/charles/PlanningShiftTodayItem.fxml"));
        loader.setRoot(this); loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        lblTijd.setText(tijd);
        lblLocatie.setText(locatie);
    }
}