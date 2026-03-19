package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class TeamLedenController extends VBox {
    //variables
    private int TeamID;
    private TeamFacade facade;
    private List<WerknemerDTO> teamleden;

    //controller
    public TeamLedenController(int TeamId, TeamFacade facade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/teamspagina/TeamLeden.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.TeamID = TeamId;
        this.facade = facade;
        this.init();
    }

    public void init() {
        teamleden = facade.getTeamLeden(TeamID);
    }
}
