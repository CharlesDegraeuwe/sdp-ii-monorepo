package hogent.sdp2.sdpii.gui;

import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.auth.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFrameController extends BorderPane {
    //variables
    @FXML private AppController app;
    @FXML private LoginController login;
    private Boolean sidebarSmall;
    private double xOffset;
    private double yOffset;


    //constructor
    public MainFrameController(Stage mf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/MainFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        // layout instellen
        app = new AppController(mf, this);
        login = new LoginController(mf);

        setCenter(login);
        windowFunctionality(mf);        //custom window functionality//routing methode
    }

    //routing


    //sidebar resizing


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
