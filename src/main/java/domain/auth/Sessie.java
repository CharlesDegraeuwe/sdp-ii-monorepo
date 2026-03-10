package domain.auth;

import domain.dto.WerknemerDTO;
import lombok.Getter;
import lombok.Setter;

public class Sessie {

    private WerknemerDTO ingelogdeWerknemer;
    @Setter
    @Getter
    private String sessionId; 

    private static class SessieHolder {
        private static final Sessie INSTANCE = new Sessie();
    }

    private Sessie() {}

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

    public boolean isMangerOrAdmin() {
        return userRole().equals("Manager") || userRole().equals("Admin");
    }

    public boolean isSuperVisor() {
        return userRole().equals("Supervisor");
    }

    public boolean isWerknemer() {
        return userRole().equals("Werknemer");
    }

    public String userRole() {
            if (ingelogdeWerknemer == null){
                throw new IllegalArgumentException("geen gebruiker gevonden");
            }



            return ingelogdeWerknemer.rol();
    }


    public static Sessie getInstance() {
        return SessieHolder.INSTANCE;
    }
}