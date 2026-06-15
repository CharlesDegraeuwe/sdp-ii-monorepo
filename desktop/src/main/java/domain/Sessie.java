package domain;

public class Sessie {
    private static Werknemer ingelogdeWerknemer;

    public static Werknemer getIngelogdeWerknemer() {
        return ingelogdeWerknemer;
    }

    public static void setIngelogdeWerknemer(Werknemer werknemer) {
        ingelogdeWerknemer = werknemer;
    }

    public static boolean isAdmin() {
        return ingelogdeWerknemer != null && ingelogdeWerknemer.getRol().equals("Admin");
    }
}