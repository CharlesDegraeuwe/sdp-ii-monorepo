package hogent.sdp2.sdpii.gui.app.teams.teamspagina;

import domain.dto.WerknemerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;


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
        this.setOnMouseClicked(e -> new TeamLidDetailsController());
        this.setStyle("-fx-background-color: " + pickColor(i));
    }

    private String pickColor(int i) {
        switch(i) {
            case 0 -> {return "#D6E8EF";}
            case 1 -> {return "#D5EFE";}
            case 2 -> {return "#F6ECCF";}
            case 3 -> {return "#F6B4B4";}
            case 4 -> {return "#F6B85B";}
        }
        return null;
    }
}
