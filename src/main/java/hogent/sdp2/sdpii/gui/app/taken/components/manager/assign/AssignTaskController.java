package hogent.sdp2.sdpii.gui.app.taken.components.manager.assign;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignMemberItemController;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignTaakItemController;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignTeamItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AssignTaskController extends BorderPane {
    @FXML ComboBox Locatie;
    @FXML ComboBox Team;
    @FXML VBox taskListContainer;
    @FXML VBox teamListContainer;
    @FXML VBox memberListContainer;

    public AssignTaskController()  {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/assign/AssignTaskLayout.fxml"));
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
            teamListContainer.setVisible(false);
            teamListContainer.setManaged(false);

        }

        teamListContainer.getChildren().addAll(
            new AssignTeamItemController("Team A"),
            new AssignTeamItemController("Team B"),
            new AssignTeamItemController("Team C"),
            new AssignTeamItemController("Team D")
        );

        memberListContainer.getChildren().addAll(
            new AssignMemberItemController("Charles Degraeuwe", "#D6E8EF"),
                new AssignMemberItemController("Marte De Backer", "#D5EFE3"),
                new AssignMemberItemController("Milan Van Bellingen", "#F6ECCF")
        );

        taskListContainer.getChildren().addAll(
                new AssignTaakItemController("Verslag opmaken", "Tegen vandaag 12:30"),
                new AssignTaakItemController("Shift ruilen met Jan", "Tegen vandaag 15:00"),
                new AssignTaakItemController("Inventaris controleren", "Tegen vandaag 17:00")
        );
    }
}
