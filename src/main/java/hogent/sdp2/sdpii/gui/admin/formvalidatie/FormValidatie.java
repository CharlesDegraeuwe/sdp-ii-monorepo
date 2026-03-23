package hogent.sdp2.sdpii.gui.admin.formvalidatie;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class FormValidatie {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern TELEFOON_PATTERN = Pattern.compile(
            "^\\+?[0-9]{8,15}$"
    );

    public static String valideer(String naam, String voornaam, String email, String telefoon, LocalDate geboortedatum) {
        if (naam == null || naam.isBlank()) return "Naam is verplicht.";
        if (naam.length() < 2) return "Naam moet minstens 2 tekens zijn.";

        if (voornaam == null || voornaam.isBlank()) return "Voornaam is verplicht.";
        if (voornaam.length() < 2) return "Voornaam moet minstens 2 tekens zijn.";

        if (email == null || email.isBlank()) return "Email is verplicht.";
        if (!EMAIL_PATTERN.matcher(email).matches()) return "Ongeldig e-mailadres.";

        if (telefoon == null || telefoon.isBlank()) return "Telefoonnummer is verplicht.";
        String schoonTelefoon = telefoon.replaceAll("[\\s\\-/.]", "");
        if (!TELEFOON_PATTERN.matcher(schoonTelefoon).matches()) return "Ongeldig telefoonnummer (8-15 cijfers).";

        if (geboortedatum == null) return "Geboortedatum is verplicht.";
        if (geboortedatum.isAfter(LocalDate.now().minusYears(16))) return "Medewerker moet minstens 16 jaar oud zijn.";
        if (geboortedatum.isBefore(LocalDate.now().minusYears(100))) return "Ongeldige geboortedatum.";

        return null;
    }
}