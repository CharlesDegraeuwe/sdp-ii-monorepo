package hogent.sdp2.sdpii.gui;

import hogent.sdp2.sdpii.gui.app.dashboard.DashboardController;
import hogent.sdp2.sdpii.gui.components.BodyController;
import hogent.sdp2.sdpii.gui.components.SidebarController;
import hogent.sdp2.sdpii.gui.components.header.HeaderController;
import hogent.sdp2.sdpii.gui.components.header.StageHeaderController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;

import java.io.IOException;

public class MainFrameController extends BorderPane {
    //variables
    @Getter private SidebarController sidebar;
    @Getter private HeaderController header;
    @Getter private BodyController body;
    @Getter private StageHeaderController controls;
    private Boolean sidebarSmall;
    private double xOffset;
    private double yOffset;


    //constructor
    public MainFrameController(Stage mf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/MainFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        //sidebar config
        sidebar = new SidebarController(this, mf);
        header = new HeaderController (this);
        body = new BodyController(this);


        // layout instellen
        setLeft(sidebar);
        sidebar.prefWidthProperty().bind(widthProperty().multiply(0.1));
        sidebar.prefHeightProperty().bind(prefHeightProperty().multiply(1));
        this.sidebarSmall = false;
        setCenter(body);
        body.setTop(header);

        windowFunctionality(mf);        //custom window functionality
        navigateTo(new DashboardController(), body);        //routing methode
    }

    //routing
    public void navigateTo(Node view, BodyController body) {
        body.setCenter(view);

    }

    //sidebar resizing
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

    private void windowFunctionality(Stage mf) {
        //cliping instellen zodat alles binnen onze window blijft
        Rectangle clip = new Rectangle();
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        setOnMouseDragged(e -> {
            mf.setX(e.getScreenX() - xOffset);
            mf.setY(e.getScreenY() - yOffset);
        });
    }



}
