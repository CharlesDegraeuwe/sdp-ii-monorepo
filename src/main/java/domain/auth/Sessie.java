package domain.auth;

import domain.interfaces.IWerknemer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Sessie {
    private static Sessie instance;
    private static IWerknemer ingelogdeWerknemer;


    public IWerknemer getIngelogdeWerknemer() {
        return ingelogdeWerknemer;
    }

    public void setIngelogdeWerknemer(IWerknemer werknemer) {
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

            return ingelogdeWerknemer.getRol();

    }

    public static Sessie getInstance() {
        if (instance == null) {
            instance = new Sessie();
        }
        return instance;
    }
}