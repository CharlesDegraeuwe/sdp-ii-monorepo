package domain.facades;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.services.LogService;
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
        String result = api.wijsTaakToe(taakId, werknemerId);
        LogService.log("UPDATE", "taken", "Taak toegewezen – taakId: " + taakId + ", werknemerId: " + werknemerId);
        return result;
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
        String result = api.maakTaakAan(werknemerId, titel, beschrijving, deadline, siteId);
        LogService.log("CREATE", "taken", "Taak aangemaakt – titel: " + titel + ", deadline: " + deadline);
        return result;
    }

    public String wijzigTaak(int taakId, String titel, String beschrijving) {
        String result = api.wijzigTaak(taakId, titel, beschrijving);
        LogService.log("UPDATE", "taken", "Taak gewijzigd – taakId: " + taakId + ", titel: " + titel);
        return result;

    }

    public String verwijderTaak(int taakId) {
        String result = api.verwijderTaak(taakId);
        LogService.log("DELETE", "taken", "Taak verwijderd – taakId: " + taakId);
        return result;
    }

    public String markeerAfgewerkt(int taakId) {
        String result = api.markeerAfgewerkt(taakId);
        LogService.log("UPDATE", "taken", "Taak afgewerkt – taakId: " + taakId);
        return result;
    }
}