package hogent.sdp2.sdpii.gui.app.taken.components.manager.assign;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.dto.WerknemerDTO;
import domain.facades.TakenFacade;
import domain.facades.WerknemersFacade;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignMemberItemController;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignTaakItemController;
import hogent.sdp2.sdpii.gui.app.taken.components.items.AssignTeamItemController;
import hogent.sdp2.sdpii.gui.app.taken.components.items.TaakItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssignTaskController extends BorderPane {
    @FXML ComboBox Locatie;
    @FXML ComboBox Team;
    @FXML VBox taskListContainer;
    @FXML VBox teamListContainer;
    @FXML VBox memberListContainer;
    @FXML Button assignButton;

    private TaakDTO geselecteerdeTaak;
    private WerknemerDTO geselecteerdeLid;

    private static final String SELECTED_STYLE = "-fx-background-color: #cce5ff; -fx-background-radius: 6;";

    public AssignTaskController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/manager/assign/AssignTaskLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Init(takenFacade);
    }

    public void Init(TakenFacade takenFacade) {
        boolean isSupervisor = Sessie.getInstance().isSuperVisor();
        if(isSupervisor) {
            Locatie.setVisible(false);
            Locatie.setManaged(false);
            Team.setVisible(false);
            Team.setManaged(false);
            teamListContainer.setVisible(false);
            teamListContainer.setManaged(false);

        }

        WerknemersFacade werknemersFacade = new WerknemersFacade();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<TaakDTO> taken = takenFacade.geefAlleTaken();
        for (TaakDTO taak : taken) {
            if (taak.afgewerkt().equals("nee")) {
                String deadlineTekst = "Deadline: " + taak.deadline().format(formatter);
                AssignTaakItemController item = new AssignTaakItemController(taak.titel(), deadlineTekst);
                item.setOnMouseClicked(e -> {
                    taskListContainer.getChildren().forEach(n -> n.setStyle(""));
                    item.setStyle(SELECTED_STYLE);
                    geselecteerdeTaak = taak;
                });
                taskListContainer.getChildren().add(item);
            }
        }

        List<WerknemerDTO> werknemers = werknemersFacade.geefAlleWerknemers();
        for (WerknemerDTO w : werknemers) {
            AssignMemberItemController item = new AssignMemberItemController(
                    w.voornaam() + " " + w.naam(), "#D6E8EF");
            item.setOnMouseClicked(e -> {
                memberListContainer.getChildren().forEach(n -> n.setStyle(""));
                item.setStyle(SELECTED_STYLE);
                geselecteerdeLid = w;
            });
            memberListContainer.getChildren().add(item);
        }

        assignButton.setOnAction(e -> {
            if (geselecteerdeTaak == null || geselecteerdeLid == null) {
                assignButton.setText("Selecteer eerst een taak en medewerker");
                return;
            }
            try {
                String resultaat = takenFacade.wijsTaakToe(geselecteerdeTaak.id(), geselecteerdeLid.id());
                assignButton.setText(resultaat);
                geselecteerdeTaak = null;
                geselecteerdeLid = null;
                taskListContainer.getChildren().forEach(n -> n.setStyle(""));
                memberListContainer.getChildren().forEach(n -> n.setStyle(""));
            } catch (Exception ex) {
                assignButton.setText("Fout: " + ex.getMessage());
            }
        });
    }
}