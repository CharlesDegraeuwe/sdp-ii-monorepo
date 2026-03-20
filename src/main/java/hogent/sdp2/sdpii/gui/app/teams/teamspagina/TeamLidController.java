package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.WerknemerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;


public class TeamLidController extends HBox {
    //FXML
    @FXML Label naam;
    @FXML HBox team_item;

    private WerknemerDTO werknemer;
    private int i;

    public TeamLidController(WerknemerDTO werknemer, int i) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/teamspagina/TeamLid.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.werknemer = werknemer;
        this.i = i;
        this.init();
    }

    public void init() {
        naam.setText(werknemer.voornaam() + " " + werknemer.naam());
        this.setStyle("-fx-background-color: " + pickColor(i));
    }

    private String pickColor(int i) {
        switch(i) {
            case 0 -> {return "rgba(117, 188, 218, 0.25)";}
            case 1 -> {return "rgba(115, 220, 169, 0.25)";}
            case 2 -> {return "rgba(246, 184, 91, 0.25)";}
            case 3 -> {return "rgba(246, 180, 180, 0.25)";}
        }
        return null;
    }
}
