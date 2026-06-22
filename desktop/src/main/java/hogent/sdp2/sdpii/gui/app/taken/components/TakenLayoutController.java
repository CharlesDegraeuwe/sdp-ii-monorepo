package hogent.sdp2.sdpii.gui.app.taken.components;

import domain.auth.Sessie;
import domain.facades.TakenFacade;
import hogent.sdp2.sdpii.gui.app.taken.components.manager.TeamTaskController;
import hogent.sdp2.sdpii.gui.app.taken.components.manager.create.CreateTaskController;
import hogent.sdp2.sdpii.gui.app.taken.components.werknemer.OwnTaskController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class TakenLayoutController extends StackPane { // <-- Aangepast van VBox naar StackPane
    @FXML private HBox controls;
    @FXML private HBox page_buttons;

    @FXML private Button jouwTaken;
    @FXML private Button teamTaken;
    @FXML private Button creeerKnop;

    @FXML private BorderPane outer_container;
    @FXML private StackPane overlayContainer; // <-- Onze nieuwe popup laag

    private OwnTaskController ownTaskController;
    private TeamTaskController teamTaskController;
    private String actieveTab = "jouwTaken";
    private TakenFacade takenFacade;

    public TakenLayoutController(TakenFacade takenFacade) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/components/TakenLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException e) { throw new RuntimeException(e); }
        this.init(takenFacade);
    }

    public void init(TakenFacade takenFacade) {
        this.takenFacade = takenFacade;
        boolean isWerknemer = Sessie.getInstance().isWerknemer();

        ownTaskController = new OwnTaskController(takenFacade);
        outer_container.setCenter(ownTaskController);

        if (!isWerknemer) {
            teamTaskController = new TeamTaskController(takenFacade);

            jouwTaken.setOnMouseClicked(e -> schakelNaarTab("jouwTaken", ownTaskController));
            teamTaken.setOnMouseClicked(e -> {
                teamTaskController.herlaad();
                schakelNaarTab("teamTaken", teamTaskController);
            });

            // Als we op Creëer klikken, openen we de interne popup!
            creeerKnop.setOnMouseClicked(e -> openNieuweTaakPopup());

        } else {
            teamTaken.setVisible(false); teamTaken.setManaged(false);
            creeerKnop.setVisible(false); creeerKnop.setManaged(false);
        }

        updateTabStyling();
    }

    // --- IN-APP POPUP LOGICA MET ANIMATIE ---
    private void openNieuweTaakPopup() {
        try {
            // 1. Maak het formulier aan
            CreateTaskController createTaskController = new CreateTaskController(takenFacade);

            // 2. Maak een transparante StackPane die als donkere waas dient
            StackPane overlay = new StackPane(createTaskController);
            // Je kunt hier de kleur donkerder/lichter maken (0.4 = 40% zichtbaar)
            overlay.setStyle("-fx-background-color: rgba(15, 23, 42, 0.4); -fx-padding: 40;");

            // 3. Maak de nieuwe Stage aan ZONDER Windows/Mac randen
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT); // Geen titelbalk of randen!
            popupStage.initModality(Modality.APPLICATION_MODAL); // Blokkeer de rest van de app

            // Zorg dat hij de hoofdapplicatie als 'eigenaar' heeft
            Window mainWindow = this.getScene().getWindow();
            popupStage.initOwner(mainWindow);

            // 4. Acties voor annuleren en succes
            createTaskController.setOnAnnuleer(() -> popupStage.close());
            createTaskController.setOnAangemaakt(() -> {
                teamTaskController.herlaad();
                ownTaskController.herlaad();
                popupStage.close();
            });

            // 5. Maak de Scene transparant
            Scene scene = new Scene(overlay);
            scene.setFill(Color.TRANSPARENT); // De scene zelf is doorzichtig
            scene.getStylesheets().add(getClass().getResource("/css/tasks.css").toExternalForm());
            popupStage.setScene(scene);

            // 6. Zorg dat de popup EXACT over je huidige applicatie valt
            popupStage.setX(mainWindow.getX());
            popupStage.setY(mainWindow.getY());
            popupStage.setWidth(mainWindow.getWidth());
            popupStage.setHeight(mainWindow.getHeight());

            // Toon de perfecte popup!
            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void schakelNaarTab(String tabNaam, javafx.scene.Node scherm) {
        this.actieveTab = tabNaam;
        outer_container.setCenter(scherm);
        updateTabStyling();
    }

    private void updateTabStyling() {
        jouwTaken.getStyleClass().setAll(actieveTab.equals("jouwTaken") ? "filter-knop-actief" : "filter-knop");
        if (teamTaken != null) {
            teamTaken.getStyleClass().setAll(actieveTab.equals("teamTaken") ? "filter-knop-actief" : "filter-knop");
        }
    }
}
