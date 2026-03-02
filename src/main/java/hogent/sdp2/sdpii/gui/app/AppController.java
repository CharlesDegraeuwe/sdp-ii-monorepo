package hogent.sdp2.sdpii.gui.app;

import domain.oud.auth.Sessie;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.admin.home.AdminHomeController;
import hogent.sdp2.sdpii.gui.app.dashboard.DashboardController;
import hogent.sdp2.sdpii.gui.components.app.BodyController;
import hogent.sdp2.sdpii.gui.components.app.SidebarController;
import hogent.sdp2.sdpii.gui.components.app.header.HeaderController;
import hogent.sdp2.sdpii.gui.components.app.header.StageHeaderController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;

import java.io.IOException;

public class AppController extends BorderPane {
    @Getter
    private SidebarController sidebar;
    @Getter private HeaderController header;
    @Getter private BodyController body;
    @Getter private StageHeaderController controls;
    @Getter private MainFrameController mainframe;
    private Boolean sidebarSmall;
    @Getter private Stage stage;

    public AppController(Stage st, MainFrameController mf) {
        this.stage = st;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/MainFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        //sidebar config
        mainframe = mf;
        sidebar = new SidebarController(this, st);
        header = new HeaderController(this);
        body = new BodyController(mf);


        // layout instellen
        setLeft(sidebar);
        sidebar.prefWidthProperty().bind(widthProperty().multiply(0.1));
        sidebar.prefHeightProperty().bind(prefHeightProperty().multiply(1));
        this.sidebarSmall = false;
        setCenter(body);
        body.setTop(header);
        //custom window functionality
        if (Sessie.isAdmin()){
            navigateTo(new AdminHomeController(this), body);
        }else {
            navigateTo(new DashboardController(), body);
        }

    }

    public void resize() {
        sidebar.prefWidthProperty().unbind();

        double targetWidth = this.sidebarSmall ? 0.1 : 0.06;
        double startwidth = sidebar.getWidth();
        double endWidth = getWidth() * targetWidth;

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(sidebar.prefWidthProperty(), endWidth, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        // labels tonen/verbergen
        if (this.sidebarSmall) {
            sidebar.lookupAll(".label").forEach(node -> node.setVisible(true));
            sidebar.getBurger_button().getImage().cancel();
            sidebar.getBurger_button().setImage(new Image(getClass().getResourceAsStream("/icons/sidebar_collapse.png")));
            this.sidebarSmall = false;
        } else {
            sidebar.lookupAll(".label").forEach(node -> node.setVisible(false));
            sidebar.getBurger_button().getImage().cancel();
            sidebar.getBurger_button().setImage(new Image(getClass().getResourceAsStream("/icons/burger.png")));
            this.sidebarSmall = true;

        }
    }

    //routing
    public void navigateTo(Node view, BodyController body) {
        body.setCenter(view);

    }
}
