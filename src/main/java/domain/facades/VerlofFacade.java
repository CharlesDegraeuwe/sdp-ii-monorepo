package domain.facades;

import domain.dto.VerlofAanvragenDTO;
import domain.services.VerlofApiService;

import java.time.LocalDate;

public class VerlofFacade {
    private final VerlofApiService api = new VerlofApiService();

    public String vraagVerlofAan(int werknemerId, LocalDate startDatum, LocalDate eindDatum, String type) {
        VerlofAanvragenDTO dto = new VerlofAanvragenDTO(werknemerId, startDatum, eindDatum, type);
        return api.vraagVerlofAan(dto);
    }

    public String keurVerlofGoed(int verlofId) {
        return api.keurVerlofGoed(verlofId);
    }

    public String wijsVerlofAf(int verlofId) {
        return api.wijsVerlofAf(verlofId);
    }
}