package hogent.sdp2.sdpii.gui.app.taken.components;

import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.app.taken.components.manager.*;
import hogent.sdp2.sdpii.gui.app.taken.components.manager.assign.AssignTaskController;
import hogent.sdp2.sdpii.gui.app.taken.components.manager.check.CheckTaskController;
import hogent.sdp2.sdpii.gui.app.taken.components.manager.create.CreateTaskController;
import hogent.sdp2.sdpii.gui.app.taken.components.werknemer.OwnTaskController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;

public class TakenLayoutController extends VBox {
    @FXML HBox controls;
    @FXML @Getter Button checkKnop;
    @FXML @Getter Button creeerKnop;
    @FXML @Getter Button toewijzenKnop;

    @FXML HBox tab_buttons;
    @FXML Button jouwTaken;
    @FXML Button teamTaken;
    @FXML BorderPane outer_container;

    private OwnTaskController ownTaskController;
    private TeamTaskController teamTaskController;
    private CheckTaskController checkTaskController;
    private CreateTaskController createTaskController;
    private AssignTaskController assignTaskController;
    private String tab = "check";
    private String pagina = "jouwTaken";

    public TakenLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/taken/TakenLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.init();
    }

    public void init() {
        boolean role = Sessie.getInstance().isWerknemer();

        ownTaskController = new OwnTaskController();
        outer_container.setCenter(ownTaskController);

        if (!role) {
            teamTaskController = new TeamTaskController();
            checkTaskController = new CheckTaskController();
            BorderPane inner_container = teamTaskController.getPage_container();

            setTabButtonsVisible(false);

            jouwTaken.setOnMouseClicked(e -> {
                outer_container.setCenter(ownTaskController);
                pagina = "jouwTaken";
                setTabButtonsVisible(false);
                setPage();
            });

            teamTaken.setOnMouseClicked(e -> {
                outer_container.setCenter(teamTaskController);
                pagina = "teamTaken";
                tab = "check";
                inner_container.setCenter(checkTaskController);
                setTabButtonsVisible(true);
                setPage();
                updateTabs();
            });

            checkKnop.setOnMouseClicked(e -> {
                inner_container.setCenter(checkTaskController);
                BorderPane.setAlignment(checkTaskController, javafx.geometry.Pos.TOP_CENTER);
                tab = "check";
                updateTabs();
            });

            creeerKnop.setOnMouseClicked(e -> {
                if (createTaskController == null) {
                    createTaskController = new CreateTaskController();
                }
                inner_container.setCenter(createTaskController);
                tab = "create";
                updateTabs();
            });

            toewijzenKnop.setOnMouseClicked(e -> {
                if (assignTaskController == null) {
                    assignTaskController = new AssignTaskController();
                }
                inner_container.setCenter(assignTaskController);
                tab = "toewijzen";
                updateTabs();
            });
        } else {
            controls.setVisible(false);
        }

        setPage();
    }

    private void setTabButtonsVisible(boolean visible) {
        tab_buttons.setVisible(visible);
        tab_buttons.setManaged(visible);
    }

    public void setPage() {
        jouwTaken.getStyleClass().setAll(pagina.equals("jouwTaken") ? "filter-knop-actief" : "filter-knop");
        teamTaken.getStyleClass().setAll(pagina.equals("teamTaken") ? "filter-knop-actief" : "filter-knop", "filter-knop-werk");
    }

    public void updateTabs() {
        checkKnop.getStyleClass().setAll(tab.equals("check") ? "filter-knop-actief" : "filter-knop");
        creeerKnop.getStyleClass().setAll(tab.equals("create") ? "filter-knop-actief" : "filter-knop", "filter-knop-werk");
        toewijzenKnop.getStyleClass().setAll(tab.equals("toewijzen") ? "filter-knop-actief" : "filter-knop", "filter-knop-afwezigheid");
    }
}