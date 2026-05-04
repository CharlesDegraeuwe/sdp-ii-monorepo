package hogent.sdp2.backend.auth;

import hogent.sdp2.backend.REST.dto.auth.AuthDTO;

public class Sessie {

    private AuthDTO ingelogdeWerknemer;

    private static class SessieHolder {
        private static final Sessie INSTANCE = new Sessie();
    }

    private Sessie() {}

    public AuthDTO getIngelogdeWerknemer() {
        return ingelogdeWerknemer;
    }

    public void setIngelogdeWerknemer(AuthDTO werknemer) {

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
        return SessieHolder.INSTANCE;
    }
}
