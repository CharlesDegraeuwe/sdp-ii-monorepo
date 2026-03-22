package hogent.sdp2.sdpii.gui.app.teams.teamspagina.components;

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
    @FXML HBox team_item;

    private WerknemerDTO werknemer;
    private int i;
    private int teamId;
    private TeamFacade facade;
    private Runnable onRefresh;
    private Consumer<Integer> onNavigeerNaarUser;

    public TeamLidController(WerknemerDTO werknemer, int i, int teamId, TeamFacade facade, Runnable onRefresh, Consumer<Integer> onNavigeerNaarUser) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/teamspagina/TeamLid.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.werknemer = werknemer;
        this.i = i;
        this.teamId = teamId;
        this.facade = facade;
        this.onRefresh = onRefresh;
        this.onNavigeerNaarUser = onNavigeerNaarUser;
        init();
    }

    public void init() {
        naam.setText(werknemer.voornaam() + " " + werknemer.naam());
        this.setStyle("-fx-background-color: " + pickColor(i));

        // Klik op naam → navigeer naar user
        naam.setStyle("-fx-cursor: hand;");
        naam.setOnMouseClicked(e -> {
            if (onNavigeerNaarUser != null) {
                onNavigeerNaarUser.accept(werknemer.id());
            }
            e.consume();
        });
    }

    @FXML
    private void handleDelete() {
        facade.verwijderLid(teamId, werknemer.id());
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
