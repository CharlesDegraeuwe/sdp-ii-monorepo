package hogent.sdp2.sdpii.gui.app.teams.teamspagina.components;

import domain.auth.Sessie;
import domain.dto.TeamDTO;
import domain.dto.TeamLidDTO;
import domain.dto.WerknemerDTO;
import domain.facades.TeamFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;


public class TeamLidController extends HBox {
    @FXML Label naam;
    @FXML Label isSupervisor;
    @FXML HBox team_item;

    private TeamLidDTO lid;
    private int i;
    private int teamId;
    private TeamFacade facade;
    private Runnable onRefresh;
    private Consumer<Integer> onNavigeerNaarUser;
    private int totaalLeden;

    public TeamLidController(TeamLidDTO lid, int i, int teamId, int totaalLeden, TeamFacade facade, Runnable onRefresh, Consumer<Integer> onNavigeerNaarUser) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/teamspagina/TeamLid.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.lid = lid;
        this.i = i;
        this.teamId = teamId;
        this.facade = facade;
        this.onRefresh = onRefresh;
        this.onNavigeerNaarUser = onNavigeerNaarUser;
        this.totaalLeden = totaalLeden;
        init();
    }

    public void init() {
        naam.setText(lid.voornaam() + " " + lid.naam());
        this.setStyle("-fx-background-color: " + pickColor(i));

        if (lid.isSupervisor()) {
            isSupervisor.setText("supervisor");
        } else {
            isSupervisor.setVisible(false);
            isSupervisor.setManaged(false);
        }

        // Verberg delete knop voor supervisors
        boolean isSupervisorRole = Sessie.getInstance().isSuperVisor();
        if (isSupervisorRole) {
            // Zoek de FontIcon (delete knop) en verberg die
            this.getChildren().stream()
                    .filter(n -> n.getStyleClass().contains("taak_delete_icon"))
                    .forEach(n -> { n.setVisible(false); n.setManaged(false); });
        }

        naam.setStyle("-fx-cursor: hand;");
        naam.setOnMouseClicked(e -> {
            if (onNavigeerNaarUser != null) {
                onNavigeerNaarUser.accept(lid.werknemerId());
            }
            e.consume();
        });
    }
    @FXML
    private void handleDelete() {
        if (totaalLeden <= 1) return; // min 1 lid
        facade.verwijderLid(teamId, lid.werknemerId());
        if (onRefresh != null) onRefresh.run();
    }

    private String pickColor(int i) {
        return switch (i) {
            case 0 -> "rgba(117, 188, 218, 0.25)";
            case 1 -> "rgba(115, 220, 169, 0.25)";
            case 2 -> "rgba(246, 184, 91, 0.25)";
            case 3 -> "rgba(246, 180, 180, 0.25)";
            default -> "transparent";
        };
    }
}
