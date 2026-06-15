package hogent.sdp2.sdpii.gui.components.app;

import domain.Sessie;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.absense.AbsenseController;
import hogent.sdp2.sdpii.gui.app.admin.GebruikerslijstController;
import hogent.sdp2.sdpii.gui.app.admin.WerknemerslijstController;
import hogent.sdp2.sdpii.gui.app.dashboard.DashboardController;
import hogent.sdp2.sdpii.gui.app.planning.PlanningController;
import hogent.sdp2.sdpii.gui.app.plants.PlantsController;
import hogent.sdp2.sdpii.gui.app.tasks.TasksController;
import hogent.sdp2.sdpii.gui.app.teams.TeamsController;
import hogent.sdp2.sdpii.gui.components.app.header.StageHeaderController;
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
    private Node activeItem;
    private Boolean small;
    private AppController app;
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
    @FXML private VBox gebruikerslijst;

    public SidebarController(AppController app, Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/Sidebar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.app = app;
        this.sh = new StageHeaderController(stage);
        this.getChildren().add(0, sh);
        this.burger_button.setImage(new Image(getClass().getResourceAsStream("/icons/sidebar_collapse.png")));
        this.Router();
        this.configureerRolGebaseerdeItems();
    }

    public void setActive(Node item) {
        if (activeItem != null) {
            activeItem.getStyleClass().remove("active");
        }
        item.getStyleClass().add("active");
        activeItem = item;
    }

    private void Router() {
        burger.setOnMouseClicked(e -> this.app.resize());
        dashboard.setOnMouseClicked(e -> { this.app.navigateTo(new DashboardController(), this.app.getBody()); setActive(dashboard); });
        planning.setOnMouseClicked(e -> { this.app.navigateTo(new PlanningController(), this.app.getBody()); setActive(planning); });
        tasks.setOnMouseClicked(e -> { this.app.navigateTo(new TasksController(), this.app.getBody()); setActive(tasks); });
        plants.setOnMouseClicked(e -> { this.app.navigateTo(new PlantsController(), this.app.getBody()); setActive(plants); });
        absense.setOnMouseClicked(e -> { this.app.navigateTo(new AbsenseController(), this.app.getBody()); setActive(absense); });
        teams.setOnMouseClicked(e -> { this.app.navigateTo(new TeamsController(), this.app.getBody()); setActive(teams); });

        // Gebruikerslijst: admin ziet alle werknemers, manager ziet zijn team
        if (gebruikerslijst != null) {
            gebruikerslijst.setOnMouseClicked(e -> {
                if (Sessie.isAdmin()) {
                    this.app.navigateTo(new GebruikerslijstController(), this.app.getBody());
                } else {
                    this.app.navigateTo(new WerknemerslijstController(), this.app.getBody());
                }
                setActive(gebruikerslijst);
            });
        }

        setActive(dashboard);
    }

    private void configureerRolGebaseerdeItems() {
        if (gebruikerslijst == null) return;

        String rol = Sessie.getIngelogdeWerknemer() != null ? Sessie.getIngelogdeWerknemer().getRol() : "";
        boolean tonen = "Admin".equalsIgnoreCase(rol) || "Manager".equalsIgnoreCase(rol);
        gebruikerslijst.setVisible(tonen);
        gebruikerslijst.setManaged(tonen);
    }

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