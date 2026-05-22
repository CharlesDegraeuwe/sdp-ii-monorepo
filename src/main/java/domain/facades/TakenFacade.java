package domain.facades;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.services.LogService;
import domain.services.TakenApiService;

import java.time.LocalDate;
import java.util.List;

public class TakenFacade {
    private TakenApiService api = new TakenApiService();

    public List<TaakDTO> geefEigenTaken() {
        int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
        return api.geefTakenVanWerknemer(werknemerId);
    }

    public List<TaakDTO> geefTakenVanWerknemer(int werknemerId) {
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

    public String maakTaakAan(String naam, String specificaties, LocalDate deadline, int siteId) {
        if (naam == null || naam.isBlank())
            throw new IllegalArgumentException("Naam is verplicht.");
        if (specificaties == null || specificaties.isBlank())
            throw new IllegalArgumentException("Specificaties zijn verplicht.");
        if (deadline == null)
            throw new IllegalArgumentException("Deadline is verplicht.");
        if (deadline.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Deadline mag niet in het verleden liggen.");
        if (siteId <= 0)
            throw new IllegalArgumentException("Selecteer een geldige locatie.");

        String result = api.maakTaakAan(naam, specificaties, deadline, siteId);
        LogService.log("CREATE", "taken", "Taak aangemaakt – naam: " + naam + ", deadline: " + deadline);
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