package hogent.sdp2.sdpii.gui.app.taken.components.manager.check;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.app.taken.components.items.FinishedTaakItem;
import hogent.sdp2.sdpii.gui.app.taken.components.items.TaakItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;

public class CheckTaskController extends BorderPane {    @FXML Label titel_taken;
    @FXML Label afgewerkte_taken_titel;
    @FXML Label edit_button;
    //containers
    @FXML
    VBox finished_task_container;
    @FXML VBox taken_container;
    @FXML @Getter BorderPane page_container;
    @FXML ComboBox Locatie;
    @FXML ComboBox Team;
    String geselecteerd = "Milan";

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
        afgewerkte_taken_titel.setText("afgewerkte taken:");

        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        if(isSupervisor) {
            Locatie.setVisible(false);
            Locatie.setManaged(false);
            Team.setVisible(false);
            Team.setManaged(false);

        }

        // Belangrijke taken
        taken_container.getChildren().addAll(
                new TaakItemController("Verslag opmaken", "Tegen vandaag 12:30"),
                new TaakItemController("Shift ruilen met Jan", "Tegen vandaag 15:00"),
                new TaakItemController("Inventaris controleren", "Tegen vandaag 17:00"),
                new TaakItemController("Teamvergadering voorbereiden", "Tegen vandaag 10:00"),
                new TaakItemController("Nieuwe werknemer inwerken", "Tegen vandaag 14:00"),
                new TaakItemController("Weekplanning nakijken", "Tegen vandaag 16:30"),
                new TaakItemController("Klantbespreking", "Tegen morgen 09:00"),
                new TaakItemController("Maandrapport indienen", "Tegen morgen 12:00")
        );


        // Afgewerkte taken
        finished_task_container.getChildren().addAll(
                new FinishedTaakItem("Loonstroken versturen", "Afgewerkt gisteren 19:25"),
                new FinishedTaakItem("Planning week 11 goedkeuren", "Afgewerkt gisteren 16:40"),
                new FinishedTaakItem("Verlofaanvraag behandelen", "Afgewerkt eergisteren 14:10"),
                new FinishedTaakItem("Veiligheidscontrole uitvoeren", "Afgewerkt eergisteren 11:00")
        );

    }
}
