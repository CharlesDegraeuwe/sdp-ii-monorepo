package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TeamLedenController extends VBox {
    //variables
    private int TeamID;
    private TeamFacade facade;
    private List<WerknemerDTO> teamleden;
    private Consumer<WerknemerDTO> onMemberClick;

    @FXML VBox container;

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
        this.onMemberClick = onMemberClick;
        this.init();
    }

    public void init() {
        teamleden = facade.getTeamLeden(TeamID);
        for (int i = 0; i < teamleden.size(); i++) {
            container.getChildren().add(new TeamLidController(teamleden.get(i), i));
        }
    }
}
