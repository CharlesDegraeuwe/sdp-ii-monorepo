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
        int werknemerId = Sessie.getInstance().getIngelogdeWerknemer().id();
        return api.maakTaakAan(werknemerId, titel, beschrijving, deadline, siteId);
    }

    public String wijzigTaak(int taakId, String titel, String beschrijving) {
        return api.wijzigTaak(taakId, titel, beschrijving);
    }

    public String verwijderTaak(int taakId) {
        return api.verwijderTaak(taakId);
    }

    public String markeerAfgewerkt(int taakId) {
        return api.markeerAfgewerkt(taakId);
    }
}