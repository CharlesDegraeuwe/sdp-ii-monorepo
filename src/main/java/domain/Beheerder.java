package domain;

import domain.facades.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//voeg hier telkens gewoon getters vr facades toe
//vergeet ze wel ni aan te maken in de constructor!!!

public class Beheerder {

    private static Beheerder instance;

    //alle facades
    @Getter private AfwezigheidFacade afwezigheidFacade;
    @Getter private ChathistorieFacade chathistorieFacade;
    @Getter private MachineFacade machineFacade;
    @Getter private NotificatieFacade notificatieFacade;
    @Getter private ShiftFacade shiftFacade;
    @Getter private SiteFacade siteFacade;
    @Getter private TakenFacade takenFacade;
    @Getter private TeamFacade teamFacade;
    @Getter private TeamKpiFacade teamKpiFacade;
    @Getter private VerlofFacade verlofFacade;
    @Getter private WerknemersFacade werknemersFacade;


    private Beheerder() {
        this.afwezigheidFacade = new AfwezigheidFacade();
        this.chathistorieFacade = new ChathistorieFacade();
        this.machineFacade = new MachineFacade();
        this.notificatieFacade = new NotificatieFacade();
        this.shiftFacade = new ShiftFacade();
        this.siteFacade = new SiteFacade();
        this.takenFacade = new TakenFacade();
        this.teamFacade = new TeamFacade();
        this.teamKpiFacade = new TeamKpiFacade();
        this.verlofFacade = new VerlofFacade();
        this.werknemersFacade = new WerknemersFacade();
    }

    public static Beheerder getInstance() {
        if (instance == null) {
            instance = new Beheerder();
        }
        return instance;
    }



}
