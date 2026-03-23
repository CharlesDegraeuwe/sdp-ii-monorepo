package domain.facades;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.services.TakenApiService;

import java.time.LocalDate;
import java.util.List;

public class TakenFacade {
    private final TakenApiService api = new TakenApiService();

    public List<TaakDTO> geefEigenTaken() {
        int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
        return api.geefTakenVanWerknemer(werknemerId);
    }

    public List<TaakDTO> geefAlleTaken() {
        return api.geefAlleTaken();
    }

    public String wijsTaakToe(int taakId, int werknemerId) {
        return api.wijsTaakToe(taakId, werknemerId);
    }

    public String maakTaakAan(String titel, String beschrijving, LocalDate deadline, int siteId) {
        if (titel == null || titel.isBlank())
            throw new IllegalArgumentException("Titel is verplicht.");
        if (beschrijving == null || beschrijving.isBlank())
            throw new IllegalArgumentException("Beschrijving is verplicht.");
        if (deadline == null)
            throw new IllegalArgumentException("Deadline is verplicht.");
        if (deadline.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Deadline mag niet in het verleden liggen.");
        if (siteId <= 0)
            throw new IllegalArgumentException("Selecteer een geldige locatie.");

        int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
        return api.maakTaakAan(werknemerId, titel, beschrijving, deadline, siteId);
    }

    public String wijzigTaak(int taakId, String titel, String beschrijving) {
        if (titel == null || titel.isBlank())
            throw new IllegalArgumentException("Titel is verplicht.");

        return api.wijzigTaak(taakId, titel, beschrijving);
    }

    public String verwijderTaak(int taakId) {
        return api.verwijderTaak(taakId);
    }

    public String markeerAfgewerkt(int taakId) {
        return api.markeerAfgewerkt(taakId);
    }
}