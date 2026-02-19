package hogent.sdp2.sdpii.gui;

import hogent.sdp2.sdpii.gui.app.*;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;

public class SidebarController extends VBox {
    //variables
    private VBox activeItem;
    @FXML private VBox burger;
    @FXML private VBox dashboard;
    @FXML private VBox planning;
    @FXML private VBox tasks;
    @FXML private VBox plants;
    @FXML private VBox absense;
    @FXML private VBox teams;

    //construtor
    public SidebarController(MainFrameController mainFrame) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/Sidebar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.Router(mainFrame);
    }

    //hele routing
    private void setActive(VBox item) {
        if (activeItem != null) {
            activeItem.getStyleClass().remove("active");
        }
        item.getStyleClass().add("active");
        activeItem = item;
    }

    private void Router(MainFrameController mainFrame) {
        dashboard.setOnMouseClicked(e -> { mainFrame.navigateTo(new DashboardController()); setActive(dashboard); });
        planning.setOnMouseClicked(e -> { mainFrame.navigateTo(new PlanningController()); setActive(planning); });
        tasks.setOnMouseClicked(e -> { mainFrame.navigateTo(new TasksController()); setActive(tasks); });
        plants.setOnMouseClicked(e -> { mainFrame.navigateTo(new PlantsController()); setActive(plants); });
        absense.setOnMouseClicked(e -> { mainFrame.navigateTo(new AbsenseController()); setActive(absense); });
        teams.setOnMouseClicked(e -> { mainFrame.navigateTo(new TeamsController()); setActive(teams); });
        setActive(dashboard);
    }

    //is transition animatietje, we kunnen nog zien of we t gebruiken kvin t nie zo smooth fz
    private void addHoverAnimation(VBox vbox) {
        vbox.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), vbox);
            st.setToX(0.98);
            st.setToY(0.98);
            st.play();
        });

        vbox.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), vbox);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        vbox.setOnMouseClicked(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), vbox);
            st.setToX(0.95);
            st.setToY(0.95);
            st.play();
        });
    }

}
