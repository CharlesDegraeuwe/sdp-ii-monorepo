package hogent.sdp2.sdpii.gui.app.overzicht.components.afwezigheden;

import domain.auth.Sessie;
import domain.dto.AfwezigheidsOverzichtDTO;
import domain.dto.TeamDTO;
import domain.dto.TeamInfoDTO;
import domain.facades.PlanningFacade;
import domain.facades.TeamFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AfwezighedenController extends VBox {
    @FXML VBox itemContainer;
    @FXML ComboBox<String> teamPicker;
    @FXML Button filterBtn;
    @FXML Button see_more;

    // We bewaren de teams zodat we de naam aan het ID kunnen koppelen
    private List<TeamInfoDTO> mijnTeams;

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
        // We verbergen de oude filterknop, dit gaat nu vanzelf!
        filterBtn.setVisible(false);
        filterBtn.setManaged(false);

        // Check of er iemand is ingelogd
        if (Sessie.getInstance().getIngelogdeWerknemer() == null) return;

        // Haal het ID van de ingelogde werknemer op (Pas dit aan als jouw Sessie of DTO iets anders gebruikt!)
        int mijnId = Sessie.getInstance().getIngelogdeWerknemer().id();

        new Thread(() -> {
            try {
                if (Sessie.getInstance().isMangerOrAdmin()) {
                    mijnTeams = new TeamFacade().geefTeamsVanManager(mijnId);

                    Platform.runLater(() -> {
                        if (mijnTeams != null && !mijnTeams.isEmpty()) {
                            teamPicker.setVisible(true);

                            // Vul de dropdown met namen
                            List<String> namen = mijnTeams.stream().map(TeamInfoDTO::naam).collect(Collectors.toList());
                            teamPicker.setItems(FXCollections.observableArrayList(namen));

                            // Luisteraar: Laad data in als de dropdown verandert
                            teamPicker.valueProperty().addListener((obs, oud, nieuwNaam) -> {
                                if (nieuwNaam != null) laadAfwezigenVoorTeam(nieuwNaam);
                            });

                            // Selecteer automatisch het eerste team
                            teamPicker.getSelectionModel().selectFirst();
                        } else {
                            teamPicker.setVisible(false);
                            toonMelding("Je beheert nog geen teams.");
                        }
                    });
                } else {
                    // Logica voor een gewone werknemer (Haalt gewoon zijn eigen team op)
                    teamPicker.setVisible(false);
                    LocalDate vandaag = LocalDate.now();
                    List<AfwezigheidsOverzichtDTO> afwezigen = new PlanningFacade()
                            .geefAfwezighedenVanTeam(mijnId, vandaag, vandaag.plusMonths(1))
                            .stream()
                            .filter(dto -> "goedgekeurd".equalsIgnoreCase(dto.status()))
                            .collect(Collectors.toList());

                    Platform.runLater(() -> toonAfwezigen(afwezigen));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> toonMelding("Fout bij laden."));
            }
        }).start();
    }

    private void laadAfwezigenVoorTeam(String teamNaam) {
        // Zoek het juiste TeamDTO op basis van de gekozen naam in de dropdown
        TeamInfoDTO gekozenTeam = mijnTeams.stream()
                .filter(t -> t.naam().equals(teamNaam))
                .findFirst()
                .orElse(null);

        if (gekozenTeam != null) {
            itemContainer.getChildren().clear();

            new Thread(() -> {
                try {
                    LocalDate vandaag = LocalDate.now();
                    List<AfwezigheidsOverzichtDTO> afwezigen = new PlanningFacade()
                            .geefAfwezighedenVanSpecifiekTeam(gekozenTeam.id(), vandaag, vandaag.plusMonths(1))
                            .stream()
                            .filter(dto -> "goedgekeurd".equalsIgnoreCase(dto.status()))
                            .collect(Collectors.toList());

                    Platform.runLater(() -> toonAfwezigen(afwezigen));
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> toonMelding("Kon afwezigen niet laden."));
                }
            }).start();
        }
    }

    private void toonAfwezigen(List<AfwezigheidsOverzichtDTO> lijst) {
        itemContainer.getChildren().clear();

        if (lijst == null || lijst.isEmpty()) {
            toonMelding("Iedereen is aanwezig vandaag!");
            return;
        }

        // Toon max 4 mensen
        int maxTonen = Math.min(lijst.size(), 4);
        for (int i = 0; i < maxTonen; i++) {
            AfwezigheidsOverzichtDTO dto = lijst.get(i);
            String naam = dto.voornaam() + " " + dto.naam();
            itemContainer.getChildren().add(new AfwezighedenItemController(naam, dto.type()));
        }

        if (lijst.size() > 4) {
            Label meerLabel = new Label("+ nog " + (lijst.size() - 4) + " afwezigen");
            meerLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px; -fx-padding: 5 0 0 5;");
            itemContainer.getChildren().add(meerLabel);
        }
    }

    private void toonMelding(String text) {
        itemContainer.getChildren().clear();
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #10B981; -fx-font-style: italic; -fx-padding: 10;");
        itemContainer.getChildren().add(lbl);
    }
}