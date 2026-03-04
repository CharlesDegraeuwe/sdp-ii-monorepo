package hogent.sdp2.sdpii.gui.app.teams;

import hogent.sdp2.sdpii.gui.components.app.PageTitleController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class TeamsController extends BorderPane {
    public TeamsController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/TeamsPage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTop(new PageTitleController("Teams"));
        setCenter(new TeamsLayoutController());
    }
}
