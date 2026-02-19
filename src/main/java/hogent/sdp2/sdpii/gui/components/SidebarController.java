package hogent.sdp2.sdpii.gui.components;

import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.app.*;
import hogent.sdp2.sdpii.gui.components.header.StageHeaderController;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class SidebarController extends VBox {
    //variables
    private Node activeItem;
    private Boolean small;
    private MainFrameController mf;
    private StageHeaderController sh;
    @Getter
    @Setter
    @FXML private ImageView burger_button;
    @FXML private VBox burger;
    @FXML private VBox dashboard;
    @FXML private VBox planning;
    @FXML private VBox tasks;
    @FXML private VBox plants;
    @FXML private VBox absense;
    @FXML private VBox teams;

    //construtor
    public SidebarController(MainFrameController mainFrame, Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/Sidebar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.mf = mainFrame;
        this.sh = new StageHeaderController(stage);
        this.getChildren().add(0, sh);
        this.burger_button.setImage(new Image(getClass().getResourceAsStream("/icons/sidebar_collapse.png")));
        this.Router();
    }

    //hele routing
    public void setActive(Node item) {
        if (activeItem != null) {
            activeItem.getStyleClass().remove("active");
        }

        item.getStyleClass().add("active");
        activeItem = item;
    }

    private void Router() {
        burger.setOnMouseClicked(e -> {this.mf.resize();});
        dashboard.setOnMouseClicked(e -> { this.mf.navigateTo(new DashboardController(), this.mf.getBody()); setActive(dashboard); });
        planning.setOnMouseClicked(e -> { this.mf.navigateTo(new PlanningController(), this.mf.getBody()); setActive(planning); });
        tasks.setOnMouseClicked(e -> { this.mf.navigateTo(new TasksController(), this.mf.getBody()); setActive(tasks); });
        plants.setOnMouseClicked(e -> { this.mf.navigateTo(new PlantsController(), this.mf.getBody()); setActive(plants); });
        absense.setOnMouseClicked(e -> { this.mf.navigateTo(new AbsenseController(), this.mf.getBody()); setActive(absense); });
        teams.setOnMouseClicked(e -> { this.mf.navigateTo(new TeamsController(), this.mf.getBody()); setActive(teams); });
        setActive(dashboard);
    }


    //is transition animatietje
    // we kunnen nog zien of we t gebruiken
    // kvin t nie zo smooth fz
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
