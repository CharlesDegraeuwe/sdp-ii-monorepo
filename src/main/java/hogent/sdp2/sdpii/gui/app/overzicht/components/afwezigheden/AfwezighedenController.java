package hogent.sdp2.sdpii.gui.app.overzicht.components.afwezigheden;

import domain.auth.Sessie;
import domain.dto.AfwezigheidsOverzichtDTO;
import domain.dto.TeamDTO;
import domain.facades.LocatieFacade;
import domain.facades.PlanningFacade;
import domain.facades.TeamFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AfwezighedenController extends VBox {
    @FXML VBox itemContainer;
    @FXML ComboBox<String> teamPicker;
    @FXML Button filterBtn;
    @FXML Button see_more;

    private List<AfwezigheidsOverzichtDTO> alleAfwezigen;
    private Map<Integer, String> werknemerTeamMap = new HashMap<>();

    public AfwezighedenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/afwezigheden/Afwezigheden.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        laadData();
        see_more.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.ZIEKTE));
    }

    private void laadData() {
        try {
            PlanningFacade planningFacade = new PlanningFacade();
            LocalDate vandaag = LocalDate.now();

            filterBtn.setVisible(false);

            if (Sessie.getInstance().isMangerOrAdmin()) {
                alleAfwezigen = planningFacade.geefAlleAfwezigheden(vandaag, vandaag.plusMonths(1))
                        .stream()
                        .filter(dto -> "goedgekeurd".equalsIgnoreCase(dto.status()))
                        .collect(Collectors.toList());

                bouwWerknemerTeamMap();
                vulTeamPicker();

                teamPicker.setVisible(true);
                filterBtn.setVisible(true);
                filterBtn.setOnMouseClicked(e -> filterOpTeam(teamPicker.getValue()));
            } else {
                teamPicker.setVisible(false);
                int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
                alleAfwezigen = planningFacade.geefAfwezighedenVanTeam(werknemerId, vandaag, vandaag.plusMonths(1))
                        .stream()
                        .filter(dto -> "goedgekeurd".equalsIgnoreCase(dto.status()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            alleAfwezigen = List.of();
        }

        toonAfwezigen(alleAfwezigen);
    }

    private void bouwWerknemerTeamMap() {
        try {
            TeamFacade teamFacade = new TeamFacade();
            LocatieFacade locatieFacade = new LocatieFacade();

            locatieFacade.geefAlleLocaties().forEach(locatie -> {
                teamFacade.geefTeamsVanSite(locatie.id()).forEach(team -> {
                    teamFacade.geefWerknemersVanTeam(team.id()).forEach(w ->
                            werknemerTeamMap.put(w.id(), team.naam())
                    );
                });
            });
        } catch (Exception e) {
            // map blijft leeg
        }
    }

    private void vulTeamPicker() {
        List<String> teams = werknemerTeamMap.values().stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        teams.add(0, "Alle teams");
        teamPicker.setItems(FXCollections.observableArrayList(teams));
        teamPicker.setValue("Alle teams");
    }

    private void filterOpTeam(String keuze) {
        if (keuze == null || keuze.equals("Alle teams")) {
            toonAfwezigen(alleAfwezigen);
        } else {
            toonAfwezigen(alleAfwezigen.stream()
                    .filter(dto -> keuze.equals(werknemerTeamMap.get(dto.werknemerId())))
                    .collect(Collectors.toList()));
        }
    }

    private void toonAfwezigen(List<AfwezigheidsOverzichtDTO> lijst) {
        itemContainer.getChildren().clear();
        lijst.forEach(dto -> {
            String naam = dto.voornaam() + " " + dto.naam();
            itemContainer.getChildren().add(new AfwezighedenItemController(naam, dto.type()));
        });
    }
}