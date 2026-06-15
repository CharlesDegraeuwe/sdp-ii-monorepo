package hogent.sdp2.sdpii.gui.components.app;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.components.app.header.StageHeaderController;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SidebarController extends VBox {
    //variables
    private Node activeItem;
    private StageHeaderController sh;
    private Map<Scherm, VBox> schermItems;

    @Getter @Setter @FXML private ImageView burger_button;
    @FXML @Getter private VBox burger;
    @FXML private VBox dashboard;
    @FXML private VBox planning;
    @FXML private VBox tasks;
    @FXML private VBox plants;
    @FXML private VBox absense;
    @FXML private VBox teams;
    @FXML private VBox admin;

    //construtor
    public SidebarController(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/components/app/Sidebar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();

            initSchermItems();
            switch(Sessie.getInstance().userRole().toLowerCase()) {
                case "admin"      -> showAdminOnly();
                case "supervisor" -> showSupervisorOnly();
                case "werknemer"  -> showEmployeeOnly();
                case "manager"    -> showManagerOnly();
                default           -> showEmployeeOnly();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.sh = new StageHeaderController(stage);
        this.getChildren().add(0, sh);
        this.burger_button.setImage(new Image(getClass().getResourceAsStream("/icons/sidebar_collapse.png")));
        this.initNavigatie();
    }

    //hele routing
    private void initSchermItems() {
        schermItems = new EnumMap<>(Scherm.class);
        schermItems.put(Scherm.DASHBOARD, dashboard);
        schermItems.put(Scherm.PLANNING, planning);
        schermItems.put(Scherm.TAKEN, tasks);
        schermItems.put(Scherm.LOCATIES, plants);
        schermItems.put(Scherm.ZIEKTE, absense);
        schermItems.put(Scherm.TEAMS, teams);
        schermItems.put(Scherm.ADMIN_HOME, admin);
    }

    public void setActiveScherm(Scherm scherm) {
        VBox item = schermItems.get(scherm);
        if (item != null) setActive(item);
    }

    public void setActive(Node item) {
        if (activeItem != null) {
            activeItem.getStyleClass().remove("active");
        }

        item.getStyleClass().add("active");
        activeItem = item;
    }



    private void initNavigatie() {
        dashboard.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.DASHBOARD));
        planning.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.PLANNING));
        tasks.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.TAKEN));
        plants.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.LOCATIES));
        absense.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.ZIEKTE));
        teams.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.TEAMS));

        if (admin != null) {
            admin.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.ADMIN_HOME));
        }
    }

    //views
    private void configureVisibility(Set<VBox> visible) {
        List<VBox> all = List.of(dashboard, planning, tasks, plants, absense, teams, admin);
        all.forEach(v -> {
            if (v == null) return;
            boolean show = visible.contains(v);
            v.setVisible(show);
            v.setManaged(show);
        });
    }

    private void showAdminOnly() {
        configureVisibility(Set.of(admin));
        setActive(admin);
    }

    private void showEmployeeOnly() {
        configureVisibility(Set.of(dashboard, planning, tasks, absense, teams));
        setActive(dashboard);
    }

    private void showSupervisorOnly() {
        configureVisibility(Set.of(dashboard, planning, tasks, teams, absense));
        setActive(dashboard);
    }

    private void showManagerOnly() {
        configureVisibility(Set.of(dashboard, planning, tasks, plants, teams, absense));
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
