package hogent.sdp2.sdpii.gui.app.planning.components.kalender;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PlanningDagCelController extends VBox {
    @FXML
     Label lblDag;
    @FXML VBox tagsContainer;

    public PlanningDagCelController(int dag) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/charles/PlanningDagCel.fxml"));
        loader.setRoot(this); loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        lblDag.setText(String.valueOf(dag));
        this.setMaxWidth(Double.MAX_VALUE);
    }

    public void addShift(String tijd) {
        tagsContainer.getChildren().add(new PlanningShiftTagController(tijd, false));
    }

    public void addVacation() {
        tagsContainer.getChildren().add(new PlanningShiftTagController("Vakantie - hele dag", true));
    }
}