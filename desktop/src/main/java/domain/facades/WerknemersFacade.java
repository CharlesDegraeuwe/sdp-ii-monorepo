package domain.facades;

import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import domain.services.WerknemersApiService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

public class WerknemersFacade {
    private WerknemersApiService api = new WerknemersApiService();

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern TELEFOON_PATTERN = Pattern.compile(
            "^\\+?[0-9]{8,15}$"
    );

    public List<WerknemerDTO> geefAlleWerknemers() {
        return api.getAlleWerknemers();
    }

    public void activeerWerknemer(String code) {
        api.activeerWerknemer(code);
    }

    public void deactiveerWerknemer(int werknemerId) {
        // TODO
    }

    public WerknemerDTO zoekOpEmail(String email) {
        return api.zoekOpEmail(email);
    }

    public WerknemerDTO zoekOpId(int id) {
        return api.zoekOpId(id);
    }

    public void update(UpdateWerknemerDTO werknemer) {
        api.update(werknemer);
    }

    public boolean veranderStatus(int werknemerId, String actie) {
        return api.veranderStatus(werknemerId, actie);
    }

    /**
     * Registreert een nieuwe werknemer met validatie.
     * Gooit IllegalArgumentException als de input ongeldig is.
     */
    public boolean registreerWerknemer(String naam, String voornaam, String email, String telefoon, String geboortedatum, String rol) {
        // Naam validatie
        if (naam == null || naam.isBlank())
            throw new IllegalArgumentException("Naam is verplicht.");
        if (naam.length() < 2)
            throw new IllegalArgumentException("Naam moet minstens 2 tekens zijn.");

        // Voornaam validatie
        if (voornaam == null || voornaam.isBlank())
            throw new IllegalArgumentException("Voornaam is verplicht.");
        if (voornaam.length() < 2)
            throw new IllegalArgumentException("Voornaam moet minstens 2 tekens zijn.");

        // Email validatie
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is verplicht.");
        if (!EMAIL_PATTERN.matcher(email).matches())
            throw new IllegalArgumentException("Ongeldig e-mailadres.");

        // Telefoon validatie
        if (telefoon == null || telefoon.isBlank())
            throw new IllegalArgumentException("Telefoonnummer is verplicht.");
        String schoonTelefoon = telefoon.replaceAll("[\\s\\-/.]", "");
        if (!TELEFOON_PATTERN.matcher(schoonTelefoon).matches())
            throw new IllegalArgumentException("Ongeldig telefoonnummer (8-15 cijfers).");

        // Geboortedatum validatie
        if (geboortedatum == null || geboortedatum.isBlank())
            throw new IllegalArgumentException("Geboortedatum is verplicht.");
        LocalDate datum;
        try {
            datum = LocalDate.parse(geboortedatum);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ongeldige geboortedatum.");
        }
        if (datum.isAfter(LocalDate.now().minusYears(16)))
            throw new IllegalArgumentException("Medewerker moet minstens 16 jaar oud zijn.");
        if (datum.isBefore(LocalDate.now().minusYears(100)))
            throw new IllegalArgumentException("Ongeldige geboortedatum.");

        // Rol validatie
        if (rol == null || rol.isBlank())
            throw new IllegalArgumentException("Selecteer een rol.");

        return api.registreerWerknemer(naam, voornaam, email, telefoon, geboortedatum, rol);
    }
}