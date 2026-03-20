package hogent.sdp2.sdpii.gui.router;

import domain.Beheerder;
import hogent.sdp2.sdpii.gui.admin.beheerGebruikers.BeheerGebruikersController;
import hogent.sdp2.sdpii.gui.admin.creeerManager.CreeerManagerController;
import hogent.sdp2.sdpii.gui.admin.creeerMedewerker.CreeerMedewerkerController;
import hogent.sdp2.sdpii.gui.admin.home.AdminHomeController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;
import hogent.sdp2.sdpii.gui.app.afwezigheid.AfwezigheidsController;
import hogent.sdp2.sdpii.gui.app.overzicht.DashboardController;
import hogent.sdp2.sdpii.gui.app.instellingen.InstellingenController;
import hogent.sdp2.sdpii.gui.app.locaties.LocatiesController;
import hogent.sdp2.sdpii.gui.app.notificaties.NotificatiesController;
import hogent.sdp2.sdpii.gui.app.planning.PlanningController;
import hogent.sdp2.sdpii.gui.app.taken.TakenController;
import hogent.sdp2.sdpii.gui.app.teams.TeamsController;

public class SchermFactory {
    private final AppController app;
    private final Beheerder beheerder = Beheerder.getInstance();

    public SchermFactory(AppController app) {
        this.app = app;
    }

    //voeg hier telkens nieuwe schermen toe

    //app
    public AfwezigheidsController afwezigheidsScherm(boolean verlof) {
        return new AfwezigheidsController(verlof);
    }

    public AccountController accountScherm() {
        return new AccountController(app);
    }

    public DashboardController dashboardScherm() {

        return new DashboardController(Beheerder.getInstance().getOverzichtFacade());
    }

    public NotificatiesController notificatieScherm() {
        return new NotificatiesController(Beheerder.getInstance().getNotificatieFacade());
    }

    public PlanningController planningScherm() { return new PlanningController();}

    public LocatiesController locatieScherm() {
        return new LocatiesController();
    }

    public InstellingenController instellingenScherm() {
        return new InstellingenController();
    }

    public TakenController taskenScherm() {
        return new TakenController();
    }

    public TeamsController teamsScherm() {

        return new TeamsController(Beheerder.getInstance().getTeamFacade(), Beheerder.getInstance().getWerknemersFacade());
    }


    //auth
    public CreeerMedewerkerController creeerMedewerkerScherm() {
        return new CreeerMedewerkerController();
    }

    public CreeerManagerController creeerManagerScherm() {
        return new CreeerManagerController();
    }

    public BeheerGebruikersController beheerGebruikersScherm() {
        return new BeheerGebruikersController();
    }

    public AdminHomeController adminHomeScherm() {
        return new AdminHomeController();
    }


}
