package domain.facades;

import domain.dto.VerlofAanvragenDTO;
import domain.services.LogService;
import domain.services.VerlofApiService;

import java.time.LocalDate;

public class VerlofFacade {
    private final VerlofApiService api = new VerlofApiService();

    public String vraagVerlofAan(int werknemerId, LocalDate startDatum, LocalDate eindDatum, String type) {
        VerlofAanvragenDTO dto = new VerlofAanvragenDTO(werknemerId, startDatum, eindDatum, type);
        String result = api.vraagVerlofAan(dto);
        LogService.log("CREATE", "verlof", "Verlof aangevraagd – type: " + type + ", van: " + startDatum + " tot: " + eindDatum);
        return result;
    }

    public String geefVerlofStatus(int verlofId) {
        return api.geefVerlofStatus(verlofId);
    }

    public String keurVerlofGoed(int verlofId) {
        String result = api.keurVerlofGoed(verlofId);
        LogService.log("UPDATE", "verlof", "Verlof goedgekeurd – verlofId: " + verlofId);
        return result;
    }

    public String wijsVerlofAf(int verlofId) {
        String result = api.wijsVerlofAf(verlofId);
        LogService.log("UPDATE", "verlof", "Verlof afgewezen – verlofId: " + verlofId);
        return result;
    }

    public String annuleerVerlof(int verlofId) {
        String result = api.annuleerVerlof(verlofId);
        LogService.log("DELETE", "verlof", "Verlof geannuleerd – verlofId: " + verlofId);
        return result;
    }
}