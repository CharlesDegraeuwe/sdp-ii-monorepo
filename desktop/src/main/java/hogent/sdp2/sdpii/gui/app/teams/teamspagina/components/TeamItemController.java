package hogent.sdp2.sdpii.gui.app.teams.teamspagina.components;

import domain.dto.TeamDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;

import java.io.IOException;

public class TeamItemController extends HBox {
    //variables
    @Getter private TeamDTO team;
    private OnTeamSelected onSelect;



    //FXML
    @FXML HBox container;
    @FXML Label teamNaam;

    public TeamItemController(TeamDTO team, OnTeamSelected onSelect) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/teamspagina/TeamItem.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.team = team;
        this.onSelect = onSelect;
        this.init();
    }

    private void init() {
        teamNaam.setText(team.naam());

        this.setOnMouseClicked(e -> onSelect.select(this));

    }

    public void setSelected(boolean selected) {
        if (selected) {
            container.getStyleClass().add("selected");
        } else {
            container.getStyleClass().remove("selected");
        }
    }

}
