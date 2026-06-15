package hogent.sdp2.sdpii.gui;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.auth.LoginController;
import hogent.sdp2.sdpii.gui.router.Router;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

public class MainFrameController extends BorderPane {
    //variables
    @Getter
    @FXML
    private AppController app;

    @Getter
    @FXML
    private LoginController login;
    private Boolean sidebarSmall;
    private double xOffset;
    private double yOffset;
    private Router router;


    //constructor
    public MainFrameController(Stage mf) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/MainFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }

        // layout instellen
        login = new LoginController(mf, this);

        if(Sessie.getInstance().getIngelogdeWerknemer() != null) {
            app = new AppController(mf, this);
            setCenter(app);
        } else {
            setCenter(login);
        }
        windowFunctionality(mf);        //custom window functionality//routing methode
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