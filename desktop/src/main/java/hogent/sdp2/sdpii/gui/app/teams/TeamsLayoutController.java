package hogent.sdp2.sdpii.gui.app.teams;

import domain.auth.Sessie;
import domain.facades.TeamFacade;
import domain.facades.WerknemersFacade;
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
    @FXML HBox tab_buttons;

    private CheckTeamsController checkTeamsController;
    private CreateTeamsController createTeamsController;
    private CheckUserpage checkUserpage;
    private CreateUserPage createUserPage;
    private TeamFacade tm;
    private WerknemersFacade wm;

    private String tab = "check";
    private String pagina = "teams";


    public TeamsLayoutController(TeamFacade tm, WerknemersFacade wm) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fmxl/app/teams/components/TeamsLayout.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.tm = tm;
        this.wm = wm;

        init();
    }

    public void init() {
        checkTeamsController = new CheckTeamsController(tm, this::navigeerNaarUser);
        outer_container.setCenter(checkTeamsController);

        boolean beperkteToegang = Sessie.getInstance().isSuperVisor() || Sessie.getInstance().isWerknemer();
        if (beperkteToegang) {
            tab_buttons.setVisible(false);
            tab_buttons.setManaged(false);
        }
        if (Sessie.getInstance().isWerknemer()) {
            usersPagina.setVisible(false);
            usersPagina.setManaged(false);
            page_buttons.setVisible(false);
            page_buttons.setManaged(false);
        }
        checkKnop.setOnMouseClicked(e -> {
            if (pagina.equals("teams")) {
                checkTeamsController = new CheckTeamsController(tm, this::navigeerNaarUser);
                outer_container.setCenter(checkTeamsController);
            } else {
                checkUserpage = new CheckUserpage(wm, tm, this::navigeerNaarTeam, this::navigeerNaarCreateUser);
                outer_container.setCenter(checkUserpage);
            }
            tab = "check";
            updateTabs();
        });

        creeerKnop.setOnMouseClicked(e -> {
            if (pagina.equals("teams")) {
                createTeamsController = new CreateTeamsController(tm);
                outer_container.setCenter(createTeamsController);
            } else {
                outer_container.setCenter(new CreateUserPage(wm));
            }
            tab = "create";
            updateTabs();
        });

        teamsPagina.setOnMouseClicked(e -> {
            pagina = "teams";
            tab = "check";
            checkTeamsController = new CheckTeamsController(tm, this::navigeerNaarUser);
            outer_container.setCenter(checkTeamsController);
            updatePages();
            updateTabs();
        });

        usersPagina.setOnMouseClicked(e -> {
            pagina = "users";
            tab = "check";
            checkUserpage = new CheckUserpage(wm, tm, this::navigeerNaarTeam, this::navigeerNaarCreateUser);
            outer_container.setCenter(checkUserpage);
            updatePages();
            updateTabs();
        });

        updateTabs();
        updatePages();
    }

    private void navigeerNaarUser(int werknemerId) {
        pagina = "users";
        tab = "check";
        checkUserpage = new CheckUserpage(wm, tm, this::navigeerNaarTeam, this::navigeerNaarCreateUser, werknemerId);
        outer_container.setCenter(checkUserpage);
        updatePages();
        updateTabs();
    }

    private void navigeerNaarTeam(int teamId) {
        pagina = "teams";
        tab = "check";
        checkTeamsController = new CheckTeamsController(tm, teamId, this::navigeerNaarUser);
        outer_container.setCenter(checkTeamsController);
        updatePages();
        updateTabs();
    }

    private void navigeerNaarCreateUser() {
        pagina = "users";
        tab = "create";
        outer_container.setCenter(new CreateUserPage(wm));
        updatePages();
        updateTabs();
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