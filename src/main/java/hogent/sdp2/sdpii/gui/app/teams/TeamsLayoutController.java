package hogent.sdp2.sdpii.gui.app.teams;

import hogent.sdp2.sdpii.gui.admin.creeerMedewerker.CreeerMedewerkerController;
import hogent.sdp2.sdpii.gui.app.teams.teamspagina.CheckTeamsController;
import hogent.sdp2.sdpii.gui.app.teams.teamspagina.CreateTeamsController;
import hogent.sdp2.sdpii.gui.app.teams.userspagina.CheckUserpage;
import hogent.sdp2.sdpii.gui.app.teams.userspagina.CreateUserPage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TeamsLayoutController extends VBox {
    @FXML BorderPane outer_container;
    @FXML Button checkKnop;
    @FXML Button creeerKnop;
    @FXML Button teamsPagina;
    @FXML Button usersPagina;
    @FXML HBox page_buttons;

    private CheckTeamsController checkTeamsController;
    private CreateTeamsController createTeamsController;
    private CheckUserpage checkUserpage;
    private CreateUserPage createUserPage;

    private String tab = "check";
    private String pagina = "teams";

    public TeamsLayoutController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/TeamsLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        init();
    }

    public void init() {
        checkTeamsController = new CheckTeamsController();
        outer_container.setCenter(checkTeamsController);

        // Tab knoppen
        checkKnop.setOnMouseClicked(e -> {
            if (pagina.equals("teams")) {
                if (checkTeamsController == null) checkTeamsController = new CheckTeamsController();
                outer_container.setCenter(checkTeamsController);
            } else {
                if (checkUserpage == null) checkUserpage = new CheckUserpage();
                outer_container.setCenter(checkUserpage);
            }
            tab = "check";
            updateTabs();
        });

        creeerKnop.setOnMouseClicked(e -> {
            if (pagina.equals("teams")) {
                if (createTeamsController == null) createTeamsController = new CreateTeamsController();
                outer_container.setCenter(createTeamsController);
            } else {
                if (createUserPage == null) createUserPage = new CreateUserPage();
                outer_container.setCenter(new CreeerMedewerkerController());
            }
            tab = "create";
            updateTabs();
        });

        // Pagina knoppen
        teamsPagina.setOnMouseClicked(e -> {
            pagina = "teams";
            tab = "check";
            if (checkTeamsController == null) checkTeamsController = new CheckTeamsController();
            outer_container.setCenter(checkTeamsController);
            updatePages();
            updateTabs();
        });

        usersPagina.setOnMouseClicked(e -> {
            pagina = "users";
            tab = "check";
            if (checkUserpage == null) checkUserpage = new CheckUserpage();
            outer_container.setCenter(checkUserpage);
            updatePages();
            updateTabs();
        });
        updateTabs();
        updatePages();
    }

    public void updateTabs() {
        checkKnop.getStyleClass().setAll(tab.equals("check") ? "filter-knop-actief" : "filter-knop");
        creeerKnop.getStyleClass().setAll(tab.equals("create") ? "filter-knop-actief" : "filter-knop", "filter-knop-werk");
    }

    public void updatePages() {
        teamsPagina.getStyleClass().setAll(pagina.equals("teams") ? "filter-knop-actief" : "filter-knop");
        usersPagina.getStyleClass().setAll(pagina.equals("users") ? "filter-knop-actief" : "filter-knop", "filter-knop-werk");
    }
}