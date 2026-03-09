package hogent.sdp2.sdpii.gui.app.taken.components.manager.assign;

import domain.auth.Sessie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AssignTaskController extends BorderPane {
    @FXML ComboBox Locatie;
    @FXML ComboBox Team;

    public AssignTaskController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/manager/assign/AssignTaskLayout.fxml"));
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
        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        if(isSupervisor) {
            Locatie.setVisible(false);
            Locatie.setManaged(false);
            Team.setVisible(false);
            Team.setManaged(false);

        }
    }
}
