package hogent.sdp2.sdpii.gui.router;

import domain.Beheerder;
import domain.auth.Sessie;
import hogent.sdp2.sdpii.gui.MainFrameController;
import hogent.sdp2.sdpii.gui.admin.home.AdminHomeController;
import hogent.sdp2.sdpii.gui.app.AppController;
import hogent.sdp2.sdpii.gui.app.account.AccountController;

public class Router {
    //
    private static Router instance;
    private AppController app;
    private MainFrameController mf;
    private final Beheerder beheerder = Beheerder.getInstance();

    private Router() {}

    public static Router getInstance() {
        if (instance == null) instance = new Router();
        return instance;
    }

    public void init(MainFrameController mf, AppController app) {
        this.mf = mf;
        this.app = app;
    }

    public void navigeerNaar(Scherm scherm) {
        SchermFactory factory = new SchermFactory(app);
        switch (scherm) {
            //app
            case ACCOUNT -> app.navigateTo(factory.accountScherm());
            case AFWEZIGHEID -> app.navigateTo(factory.afwezigheidsScherm());
            case DASHBOARD -> app.navigateTo(factory.dashboardScherm());
            case INSTELLINGEN -> app.navigateTo(factory.instellingenScherm());
            case LOCATIES -> app.navigateTo(factory.locatieScherm());
            case NOTIFICATIES -> app.navigateTo(factory.notificatieScherm());
            case PLANNING -> app.navigateTo(factory.planningScherm());
            case TAKEN -> app.navigateTo(factory.taskenScherm());
            case TEAMS -> app.navigateTo(factory.teamsScherm());
            case CREEER_MEDEWERKER -> app.navigateTo(factory.creeerMedewerkerScherm());
            case CREEER_MANAGER -> app.navigateTo(factory.creeerManagerScherm());
            case ADMIN_HOME -> app.navigateTo(factory.adminHomeScherm());
            case VIEW_LOGS -> app.navigateTo(factory.logViewScherm());

            //auth
            case LOGIN -> {}
            case LOGOUT -> {
                Sessie.getInstance().uitloggen();
                app.getMainframe().getLogin().getForm().reset();
                app.getMainframe().setCenter(app.getMainframe().getLogin());
            }

        }
        app.getSidebar().setActiveScherm(scherm);
    }
}
