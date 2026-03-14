package hogent.sdp2.sdpii.gui.app.taken.components.werknemer;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.app.taken.components.items.FinishedTaakItem;
import hogent.sdp2.sdpii.gui.app.taken.components.items.TaakItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class OwnTaskController extends BorderPane {
    @FXML Label titel_taken;
    @FXML Label afgewerkte_taken_titel;
    @FXML Label edit_button;
    //containers
    @FXML VBox finished_task_container;
    @FXML VBox belangrijk_container;
    @FXML VBox vandaag_container;
    @FXML VBox morgen_container;

        public OwnTaskController() {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/werknemer/OwnTaskLayout.fxml"));
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
            titel_taken.setText(Sessie.getInstance().getIngelogdeWerknemer().voornaam() + "'s taken:");
            afgewerkte_taken_titel.setText(Sessie.getInstance().getIngelogdeWerknemer().voornaam() + "'s afgewerkte taken:");
            boolean rol = Sessie.getInstance().isWerknemer();
            edit_button.setVisible(!rol);

            // Belangrijke taken
            belangrijk_container.getChildren().addAll(
                    new TaakItemController("Verslag opmaken", "Tegen vandaag 12:30"),
                    new TaakItemController("Shift ruilen met Jan", "Tegen vandaag 15:00"),
                    new TaakItemController("Inventaris controleren", "Tegen vandaag 17:00")
            );

            // Taken voor vandaag
            vandaag_container.getChildren().addAll(
                    new TaakItemController("Teamvergadering voorbereiden", "Tegen vandaag 10:00"),
                    new TaakItemController("Nieuwe werknemer inwerken", "Tegen vandaag 14:00"),
                    new TaakItemController("Weekplanning nakijken", "Tegen vandaag 16:30")
            );

            // Taken voor morgen
            morgen_container.getChildren().addAll(
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
