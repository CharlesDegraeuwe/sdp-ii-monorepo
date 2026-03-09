package hogent.sdp2.sdpii.gui.app.taken.components.manager.check;

import domain.auth.Sessie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

import java.io.IOException;

public class CheckTaskController extends BorderPane {
    @FXML Label edit_button;
    @FXML Label afgewerkte_taken_titel;
    @FXML @Getter BorderPane page_container;
    @FXML ComboBox Locatie;
    @FXML ComboBox Team;
    String geselecteerd;

    public CheckTaskController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/manager/check/CheckTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Init();
    }

    public void Init() {
        boolean role = Sessie.getInstance().isWerknemer();
        edit_button.setVisible(!role);
        afgewerkte_taken_titel.setText(geselecteerd + "'s afgewerkte taken:");

        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        if(isSupervisor) {
            Locatie.setVisible(false);
            Locatie.setManaged(false);
            Team.setVisible(false);
            Team.setManaged(false);

        }

    }
}
