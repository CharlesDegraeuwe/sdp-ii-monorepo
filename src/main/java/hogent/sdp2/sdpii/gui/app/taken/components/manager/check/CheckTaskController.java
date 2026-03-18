package hogent.sdp2.sdpii.gui.app.taken.components.manager.check;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.facades.TakenFacade;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CheckTaskController extends BorderPane {
    @FXML Label titel_taken;
    @FXML Label afgewerkte_taken_titel;
    @FXML Label edit_button;
    @FXML VBox finished_task_container;
    @FXML VBox taken_container;
    @FXML @Getter BorderPane page_container;
    @FXML ComboBox locatie;
    @FXML ComboBox team;

    public CheckTaskController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/check/CheckTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        init(takenFacade);
    }

    public void init(TakenFacade takenFacade) {
        boolean role = Sessie.getInstance().isWerknemer();
        edit_button.setVisible(!role);
        afgewerkte_taken_titel.setText("afgewerkte taken:");

        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        if (isSupervisor) {
            locatie.setVisible(false);
            locatie.setManaged(false);
            team.setVisible(false);
            team.setManaged(false);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<TaakDTO> taken = takenFacade.geefAlleTaken();

        for (TaakDTO taak : taken) {
            String deadlineTekst = "Deadline: " + taak.deadline().format(formatter);
            if (taak.afgewerkt().equals("nee")) {
                taken_container.getChildren().add(new TaakItemController(taak.titel(), deadlineTekst));
            } else {
                finished_task_container.getChildren().add(new FinishedTaakItem(taak.titel(), "Afgewerkt"));
            }
        }
    }
}
