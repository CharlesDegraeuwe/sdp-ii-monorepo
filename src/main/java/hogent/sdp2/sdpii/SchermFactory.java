package hogent.sdp2.sdpii;

import domain.Beheerder;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.afwezigheid.AfwezigheidsController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;
import hogent.sdp2.sdpii.gui.app.dashboard.DashboardController;
import hogent.sdp2.sdpii.gui.app.notificaties.NotificatiesController;
import hogent.sdp2.sdpii.gui.app.planning.PlanningController;
import hogent.sdp2.sdpii.gui.app.locaties.LocatiesController;
import hogent.sdp2.sdpii.gui.app.instellingen.InstellingenController;
import hogent.sdp2.sdpii.gui.app.taken.TakenController;
import hogent.sdp2.sdpii.gui.app.teams.TeamsController;

public class SchermFactory {

    //
    private final AppController app;
    private final Beheerder beheerder = Beheerder.getInstance();

    public SchermFactory(AppController app) {
        this.app = app;
    }


    //voeg hier telkens nieuwe schermen toe
    public AfwezigheidsController absenseScherm() {
        return new AfwezigheidsController();
    }

    public AccountController accountScherm() {
        return new AccountController(app);
    }

    public DashboardController dashboardScherm() {
        return new DashboardController();
    }

    public NotificatiesController notificationsScherm() {
        return new NotificatiesController();
    }

    public PlanningController planningScherm() {
        return new PlanningController();
    }

    public LocatiesController plantsScherm() {
        return new LocatiesController();
    }

    public InstellingenController settingssScherm() {
        return new InstellingenController();
    }

    public TakenController tasksScherm() {
        return new TakenController();
    }

    public TeamsController teamsScherm() {
        return new TeamsController();
    }

}
