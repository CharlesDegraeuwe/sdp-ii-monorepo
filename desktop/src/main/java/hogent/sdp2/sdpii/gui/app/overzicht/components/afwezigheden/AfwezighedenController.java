package hogent.sdp2.sdpii.gui.app.overzicht.components.afwezigheden;

import domain.auth.Sessie;
import domain.dto.AfwezigheidsOverzichtDTO;
import domain.facades.NotificatieFacade;
import domain.facades.PlanningFacade;
import hogent.sdp2.sdpii.gui.router.Router;
import hogent.sdp2.sdpii.gui.router.Scherm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AfwezighedenController extends VBox {

    @FXML VBox itemContainer;
    @FXML Button see_more;
    @FXML Label statAfwezig;
    @FXML Label statAfwachting;
    @FXML Label statOngelezen;

    public AfwezighedenController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/overzicht/components/afwezigheden/Afwezigheden.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        see_more.setOnMouseClicked(e -> Router.getInstance().navigeerNaar(Scherm.ZIEKTE));
        laadData();
    }

    private void laadData() {
        if (Sessie.getInstance().getIngelogdeWerknemer() == null) return;
        int mijnId = Sessie.getInstance().getIngelogdeWerknemer().id();
        LocalDate vandaag = LocalDate.now();

        new Thread(() -> {
            try {
                List<AfwezigheidsOverzichtDTO> alle = new PlanningFacade()
                        .geefAfwezighedenVanTeam(mijnId, vandaag, vandaag.plusMonths(1))
                        .stream()
                        .filter(dto -> "goedgekeurd".equalsIgnoreCase(dto.status())
                                || "Ziekte".equals(dto.type()))
                        .collect(Collectors.toList());

                List<AfwezigheidsOverzichtDTO> afwezigVandaag = alle.stream()
                        .filter(a -> !vandaag.isBefore(a.startDatum()) && !vandaag.isAfter(a.eindDatum()))
                        .collect(Collectors.toList());

                List<AfwezigheidsOverzichtDTO> inAfwachting = new PlanningFacade()
                        .geefAfwezighedenVanTeam(mijnId, vandaag, vandaag.plusMonths(1))
                        .stream()
                        .filter(a -> "In afwachting".equalsIgnoreCase(a.status()))
                        .collect(Collectors.toList());

                long ongelezen = 0;
                try {
                    ongelezen = new NotificatieFacade().geefAantalOngelezen(mijnId);
                } catch (Exception ignored) {}

                final long ongelStr = ongelezen;
                Platform.runLater(() -> {
                    toonAfwezigen(afwezigVandaag);
                    statAfwezig.setText(String.valueOf(afwezigVandaag.size()));
                    statAfwachting.setText(String.valueOf(inAfwachting.size()));
                    statOngelezen.setText(String.valueOf(ongelStr));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> toonMelding("Iedereen is aanwezig vandaag!"));
            }
        }).start();
    }

    private void toonAfwezigen(List<AfwezigheidsOverzichtDTO> lijst) {
        itemContainer.getChildren().clear();

        if (lijst == null || lijst.isEmpty()) {
            toonMelding("Iedereen is aanwezig vandaag!");
            return;
        }

        int maxTonen = Math.min(lijst.size(), 4);
        for (int i = 0; i < maxTonen; i++) {
            AfwezigheidsOverzichtDTO dto = lijst.get(i);
            itemContainer.getChildren().add(
                    new AfwezighedenItemController(dto.voornaam() + " " + dto.naam(), dto.type())
            );
        }

        if (lijst.size() > 4) {
            Label meerLabel = new Label("+ nog " + (lijst.size() - 4) + " afwezigen");
            meerLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-padding: 3 0 0 4;");
            itemContainer.getChildren().add(meerLabel);
        }
    }

    private void toonMelding(String text) {
        itemContainer.getChildren().clear();
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #10B981; -fx-font-style: italic; -fx-font-size: 11px; -fx-padding: 4;");
        itemContainer.getChildren().add(lbl);
    }
}
