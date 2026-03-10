package hogent.sdp2.sdpii.gui.app.planning.components;

import hogent.sdp2.sdpii.gui.app.planning.components.kalender.PlanningKalenderController;
import hogent.sdp2.sdpii.gui.app.planning.components.sidebar.PlanningSidebarController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

public class PlanningLayoutController extends BorderPane {
    public PlanningLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/planning/charles/PlanningLayout.fxml"));
        loader.setRoot(this); loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        HBox center = (HBox) this.getCenter();

        PlanningKalenderController kalender = new PlanningKalenderController();
        PlanningSidebarController sidebar = new PlanningSidebarController();
        HBox.setHgrow(kalender, Priority.ALWAYS);

        center.getChildren().setAll(kalender, sidebar);
    }
}
