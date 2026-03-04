package hogent.sdp2.sdpii.gui.app.teams;

import hogent.sdp2.sdpii.gui.app.teams.teamspagina.TeamsPaginaController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TeamsLayoutController extends VBox {
    @FXML HBox top_bar;
    @FXML BorderPane child_container;

    //buttons
    @FXML Button teams_button;
    @FXML Button users_button;

    public TeamsLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/TeamsLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initEvents();


    }

    public void initEvents() {
        child_container.setCenter(new TeamsPaginaController());
        users_button.setOnMouseClicked(e -> {});
        teams_button.setOnMouseClicked(e -> {});
    }
}
