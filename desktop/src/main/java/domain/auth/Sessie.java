package domain.auth;

import domain.dto.WerknemerDTO;
import lombok.Getter;
import lombok.Setter;

public class Sessie {

    private WerknemerDTO ingelogdeWerknemer;
    @Setter
    @Getter
    private String jwtToken;

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
        this.jwtToken = null;
    }

    public boolean isAdmin() {
        return ingelogdeWerknemer != null && userRole().equalsIgnoreCase("Admin");
    }

    public boolean isMangerOrAdmin() {
        return userRole().equalsIgnoreCase("Manager") || userRole().equalsIgnoreCase("Admin");
    }

    public boolean isSuperVisor() {
        return userRole().equalsIgnoreCase("Supervisor");
    }

    public boolean isWerknemer() {
        return userRole().equalsIgnoreCase("Werknemer");
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