package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.auth.Sessie;
import domain.dto.TeamDTO;
import domain.facades.TeamFacade;
import domain.util.FilteredListUtil;
import hogent.sdp2.sdpii.gui.app.teams.teamspagina.components.TeamItemController;
import hogent.sdp2.sdpii.gui.app.teams.teamspagina.components.TeamLedenController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

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
    @FXML TextField searchField;

    private TeamLedenController currentLedenController;
    private StackPane overlay;
    private Consumer<Integer> onNavigeerNaarUser;

    //dus deze is als ge de pagina nrml laadt
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

    //dit is als ge van t useroverzicht komt
    public CheckTeamsController(TeamFacade tm, Consumer<Integer> onNavigeerNaarUser) {
        this(tm);
        this.onNavigeerNaarUser = onNavigeerNaarUser;
    }

    public CheckTeamsController(TeamFacade tm, int autoSelectTeamId, Consumer<Integer> onNavigeerNaarUser) {
        this(tm, onNavigeerNaarUser);
        for (Node node : teamsList.getChildren()) {
            if (node instanceof TeamItemController item && item.getTeam().id() == autoSelectTeamId) {
                setSelected(item);
                break;
            }
        }
    }


    public void init() {
        addMembersContainer.setVisible(false);
        leftColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        rightColumn.prefWidthProperty().bind(mainCard.widthProperty().subtract(48).multiply(0.5));
        leftColumn.setMaxWidth(Double.MAX_VALUE);
        rightColumn.setMaxWidth(Double.MAX_VALUE);

        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        boolean isWerknemer  = Sessie.getInstance().isWerknemer();

        if (isSupervisor || isWerknemer) {
            addMemberBtn.setVisible(false);
            addMemberBtn.setManaged(false);
        }

        searchField.textProperty().addListener((obs, oud, nieuw) -> {
            String query = nieuw.toLowerCase().trim();
            if (query.isEmpty()) {
                vulTeamsList(teams);
            } else {
                List<TeamDTO> gefilterd = FilteredListUtil.filter(teams, t ->
                        t.naam().toLowerCase().contains(query) ||
                                (t.managerNaam() != null && t.managerNaam().toLowerCase().contains(query)) ||
                                (t.siteNaam() != null && t.siteNaam().toLowerCase().contains(query))
                );
                vulTeamsList(gefilterd);
            }
            clearSelection();
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

        new Thread(() -> {
            try {
                boolean eigenTeams = isSupervisor || isWerknemer;
                List<TeamDTO> geladen = eigenTeams
                        ? tm.getTeamsVanWerknemer(Sessie.getInstance().getIngelogdeWerknemer().id())
                        : tm.getAlleTeams();
                Platform.runLater(() -> {
                    teams = geladen;
                    vulTeamsList(teams);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void vulTeamsList(List<TeamDTO> teamsToShow) {
        teamsList.getChildren().clear();
        teamsToShow.forEach(teamDTO -> {
            TeamItemController item = new TeamItemController(teamDTO, this::setSelected);
            teamsList.getChildren().add(item);
        });
    }

    private void setSelected(TeamItemController selectedItem) {
        this.selected = selectedItem;
        teamsList.getChildren().forEach(node -> {
            if (node instanceof TeamItemController item) item.setSelected(false);
        });
        selectedItem.setSelected(true);
        membersList.getChildren().clear();
        currentLedenController = new TeamLedenController(selectedItem.getTeam(), tm, this::refreshTeams, onNavigeerNaarUser);
        membersList.getChildren().add(currentLedenController);
        addMembersContainer.setVisible(currentLedenController.getTeamleden().size() < 4);
    }

    private void refreshTeams() {
        teamsList.getChildren().clear();
        membersList.getChildren().clear();
        membersList.getChildren().add(noItems());
        addMembersContainer.setVisible(false);
        selected = null;
        currentLedenController = null;
        searchField.clear();

        boolean eigenTeams = Sessie.getInstance().isSuperVisor() || Sessie.getInstance().isWerknemer();
        new Thread(() -> {
            try {
                List<TeamDTO> geladen = eigenTeams
                        ? tm.getTeamsVanWerknemer(Sessie.getInstance().getIngelogdeWerknemer().id())
                        : tm.getAlleTeams();
                Platform.runLater(() -> {
                    teams = geladen;
                    vulTeamsList(teams);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
