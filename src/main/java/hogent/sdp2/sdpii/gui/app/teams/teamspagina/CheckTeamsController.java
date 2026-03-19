package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.TeamDTO;
import domain.facades.TeamFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class CheckTeamsController extends VBox {
    private TeamFacade tm;
    private List<TeamDTO> teams;
    private TeamItemController selected;
    //FXML
    @FXML VBox teamsList;
    @FXML VBox membersList;

    public CheckTeamsController(TeamFacade tm) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/teamspagina/CheckTeams.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.tm = tm;
        init();
    }

    public void init() {
        teams = tm.getAlleTeams();

        teams.forEach(teamDTO -> {
            TeamItemController item = new TeamItemController(teamDTO, this::setSelected);
            teamsList.getChildren().add(item);
        });

        membersList.getChildren().add(noItems());
        teamsList.setOnMouseClicked(e -> {
            Node node = (Node) e.getTarget();

            while (node != null) {
                if (node instanceof TeamItemController) {
                    return;
                }
                node = node.getParent();
            }

            clearSelection();
        });
    }


    public Pane noItems() {
        VBox v = new VBox();
        v.setStyle("-fx-alignment: CENTER");
        VBox.setVgrow(v, Priority.valueOf("always"));
        Label l = new Label();
        l.setText("geen team geselecteerd");
        v.getChildren().add(l);

        return v;
    }

    private void setSelected(TeamItemController selectedItem) {
        this.selected = selectedItem;

        teamsList.getChildren().forEach(node -> {
            if (node instanceof TeamItemController item) {
                item.setSelected(false);
            }
        });

        selectedItem.setSelected(true);
        membersList.getChildren().clear();
        membersList.getChildren().add(new TeamLedenController(selectedItem.getTeam().id(), tm));
    }

    private void clearSelection() {
        selected = null;

        teamsList.getChildren().forEach(node -> {
            if (node instanceof TeamItemController item) {
                item.setSelected(false);
            }
        });

        membersList.getChildren().clear();
        membersList.getChildren().add(noItems());
    }
}
