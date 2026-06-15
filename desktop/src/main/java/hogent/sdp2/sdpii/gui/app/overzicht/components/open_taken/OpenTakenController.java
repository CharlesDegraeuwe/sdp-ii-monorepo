package hogent.sdp2.sdpii.gui.app.overzicht.components.open_taken;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.facades.TakenFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OpenTakenController extends VBox {
    @FXML VBox itemContainer;
    @FXML ComboBox<String> teamPicker;
    @FXML Button see_more;

    private List<TaakDTO> alleTaken;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

    public OpenTakenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/open_taken/OpenTaken.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            TakenFacade facade = new TakenFacade();
            if (Sessie.getInstance().isWerknemer() || Sessie.getInstance().isSuperVisor()) {
                alleTaken = facade.geefEigenTaken();
            } else {
                alleTaken = facade.geefAlleTaken();
            }
        } catch (Exception e) {
            alleTaken = List.of();
        }

        boolean isWerknemerOfSupervisor = Sessie.getInstance().isWerknemer() || Sessie.getInstance().isSuperVisor();
        if (isWerknemerOfSupervisor) {
            teamPicker.setVisible(false);
        } else {
            vulTeamPicker();
            teamPicker.setOnAction(e -> filterOpTeam(teamPicker.getValue()));
        }

        toonTaken(alleTaken);
        see_more.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.TAKEN));
    }

    private void vulTeamPicker() {
        List<String> teams = alleTaken.stream()
                .filter(t -> t.teamId() != null)
                .map(t -> "Team " + t.teamId())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        teams.add(0, "Alle teams");
        teamPicker.setItems(FXCollections.observableArrayList(teams));
        teamPicker.setValue("Alle teams");
    }

    private void filterOpTeam(String keuze) {
        if (keuze == null || keuze.equals("Alle teams")) {
            toonTaken(alleTaken);
        } else {
            int teamId = Integer.parseInt(keuze.replace("Team ", ""));
            toonTaken(alleTaken.stream()
                    .filter(t -> t.teamId() != null && t.teamId() == teamId)
                    .collect(Collectors.toList()));
        }
    }

    private void toonTaken(List<TaakDTO> taken) {
        itemContainer.getChildren().clear();
        taken.stream()
                .filter(t -> !"true".equals(t.afgewerkt()))
                .forEach(t -> itemContainer.getChildren().add(
                        new OpenTakenItemController(
                                t.titel(),
                                t.deadline() != null ? t.deadline().format(formatter) : ""
                        )
                ));
    }
}