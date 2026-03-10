package hogent.sdp2.sdpii.gui.app.teams.userspagina;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CheckUserpage extends VBox {
    public CheckUserpage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/userspagina/CheckUsers.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
