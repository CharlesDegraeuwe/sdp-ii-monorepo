package domain.facades;

import domain.dto.VerlofAanvragenDTO;
import domain.services.VerlofApiService;

import java.time.LocalDate;

public class VerlofFacade {
    private final VerlofApiService api = new VerlofApiService();

    public String vraagVerlofAan(int werknemerId, LocalDate startDatum, LocalDate eindDatum, String type) {
        if (startDatum == null)
            throw new IllegalArgumentException("Startdatum is verplicht.");
        if (eindDatum == null)
            throw new IllegalArgumentException("Einddatum is verplicht.");
        if (eindDatum.isBefore(startDatum))
            throw new IllegalArgumentException("Einddatum mag niet vóór de startdatum liggen.");
        if (startDatum.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Startdatum mag niet in het verleden liggen.");
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Verloftype is verplicht.");

        VerlofAanvragenDTO dto = new VerlofAanvragenDTO(werknemerId, startDatum, eindDatum, type);
        return api.vraagVerlofAan(dto);
    }

    public String geefVerlofStatus(int verlofId) {
        return api.geefVerlofStatus(verlofId);
    }

    public String keurVerlofGoed(int verlofId) {
        return api.keurVerlofGoed(verlofId);
    }

    public String wijsVerlofAf(int verlofId) {
        return api.wijsVerlofAf(verlofId);
    }

    public String annuleerVerlof(int verlofId) {
        return api.annuleerVerlof(verlofId);
    }
}