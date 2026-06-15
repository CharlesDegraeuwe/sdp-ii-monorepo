package domain.auth;

import domain.dto.WerknemerDTO;

public class Sessie {
    private static Sessie instance;
    private static WerknemerDTO ingelogdeWerknemer;


    public WerknemerDTO getIngelogdeWerknemer() {
        return ingelogdeWerknemer;
    }

    public void setIngelogdeWerknemer(WerknemerDTO werknemer) {

        this.ingelogdeWerknemer = werknemer;
    }

    public void uitloggen() {
        this.ingelogdeWerknemer = null;
    }

    public boolean isAdmin() {
        return ingelogdeWerknemer != null && userRole().equals("Admin");
    }

    public String userRole() {
            if (ingelogdeWerknemer == null){
                throw new IllegalArgumentException("geen gebruiker gevonden");
            }



            return ingelogdeWerknemer.rol();


    }

    public static Sessie getInstance() {
        if (instance == null) {
            instance = new Sessie();
        }
        return instance;
    }
}