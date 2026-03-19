package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class TeamLidController extends HBox {
    public TeamLidController(int TeamId) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/teamspagina/TeamLid.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
