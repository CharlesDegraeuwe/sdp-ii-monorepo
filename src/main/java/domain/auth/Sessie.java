package domain.auth;

import domain.werknemer.Werknemer;

public class Sessie {
    private static Werknemer ingelogdeWerknemer;

    public static Werknemer getIngelogdeWerknemer() {
        return ingelogdeWerknemer;
    }

    public static void setIngelogdeWerknemer(Werknemer werknemer) {
        ingelogdeWerknemer = werknemer;
    }

    public static void uitloggen() {
        ingelogdeWerknemer = null;
    }

    public static boolean isAdmin() {
        return ingelogdeWerknemer != null && userRole().equals("Admin");
    }

    public static String userRole() {
            if (ingelogdeWerknemer == null){
                throw new IllegalArgumentException("geen gebruiker gevonden");
            }

            return ingelogdeWerknemer.getRol();

    }

}