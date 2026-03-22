package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.TeamDTO;
import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class CheckTeamsController extends StackPane {
    private TeamFacade tm;
    private List<TeamDTO> teams;
    private TeamItemController selected;
    //FXML
    @FXML VBox teamsList;
    @FXML VBox membersList;
    @FXML HBox mainCard;
    @FXML VBox leftColumn;
    @FXML VBox rightColumn;
    @FXML Button addMemberBtn;
    @FXML VBox addMembersContainer;

    private TeamLedenController currentLedenController;
    private StackPane overlay;

    public CheckTeamsController(TeamFacade tm, int autoSelectTeamId) {
        this(tm);
        for (Node node : teamsList.getChildren()) {
            if (node instanceof TeamItemController item && item.getTeam().id() == autoSelectTeamId) {
                setSelected(item);
                break;
            }
        }
    }

    public CheckTeamsController(TeamFacade tm) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/teamspagina/CheckTeams.fxml"));
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
        addMembersContainer.setVisible(false);
        leftColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        rightColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        rightColumn.setMaxWidth(Double.MAX_VALUE);

        teams = tm.getAlleTeams();
        teams.forEach(teamDTO -> {
            TeamItemController item = new TeamItemController(teamDTO, this::setSelected);
            teamsList.getChildren().add(item);
        });

        membersList.getChildren().add(noItems());


        addMemberBtn.setOnAction(e -> {
            if (selected != null && currentLedenController != null) {
                currentLedenController.showBeschikbareWerknemers();
            }
        });

        teamsList.setOnMouseClicked(e -> {
            Node node = (Node) e.getTarget();
            while (node != null) {
                if (node instanceof TeamItemController) return;
                node = node.getParent();
            }
            clearSelection();
        });
    }

    private void setSelected(TeamItemController selectedItem) {
        this.selected = selectedItem;
        teamsList.getChildren().forEach(node -> {
            if (node instanceof TeamItemController item) item.setSelected(false);
        });
        selectedItem.setSelected(true);
        membersList.getChildren().clear();
        currentLedenController = new TeamLedenController(selectedItem.getTeam().id(), tm, this::refreshTeams);
        membersList.getChildren().add(currentLedenController);
        addMembersContainer.setVisible(currentLedenController.getTeamleden().size() < 4);
    }

    private void refreshTeams() {
        if (selected != null) {
            setSelected(selected);
        }
    }


    public Pane noItems() {
        VBox v = new VBox();
        v.setStyle("-fx-alignment: CENTER");
        VBox.setVgrow(v, Priority.ALWAYS);
        Label l = new Label();
        l.setText("geen team geselecteerd");
        v.getChildren().add(l);

        return v;
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
