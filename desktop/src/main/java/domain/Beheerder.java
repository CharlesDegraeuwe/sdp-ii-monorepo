package domain;

import domain.auth.Sessie;
import domain.facades.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Beheerder {

    private static class BeheerderHolder {
        private static final Beheerder INSTANCE = new Beheerder();
    }

    private Beheerder() {
    }

    public static Beheerder getInstance() {
      return BeheerderHolder.INSTANCE;
    }

    public AfwezigheidFacade getAfwezigheidFacade() {
        return new AfwezigheidFacade();
    }

    public ChathistorieFacade getChathistorieFacade() {
        return new ChathistorieFacade();
    }

    public MachineFacade getMachineFacade() {
        return new MachineFacade();
    }

    public NotificatieFacade getNotificatieFacade() {
        return new NotificatieFacade();
    }

    public ShiftFacade getShiftFacade() {
        return new ShiftFacade();
    }

    public SiteFacade getSiteFacade() {
        return new SiteFacade();
    }

    public TakenFacade getTakenFacade() {
        return new TakenFacade();
    }

    public TeamFacade getTeamFacade() {
        return new TeamFacade();
    }

    public TeamKpiFacade getTeamKpiFacade() {
        return new TeamKpiFacade();
    }

    public VerlofFacade getVerlofFacade(){
        return new VerlofFacade();
    }

    public WerknemersFacade getWerknemersFacade() {
        return new WerknemersFacade();
    }

    public AuthFacade getAuthFacade() {
        return new AuthFacade();
    }

    public OverzichtsFacade getOverzichtFacade() {
        return new OverzichtsFacade();
    }

    public PlanningFacade getPlanningFacade() {
        return new PlanningFacade();
    }

    public GeschiedenisFacade getGeschiedenisFacade() {
        return new GeschiedenisFacade();
    }

}
