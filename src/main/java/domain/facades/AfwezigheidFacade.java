package domain.facades;

import domain.dto.AfwezigheidAanmakenDTO;
import domain.services.AfwezigheidApiService;

import java.time.LocalDate;

public class AfwezigheidFacade {
    private final AfwezigheidApiService api = new AfwezigheidApiService();

    public String meldAfwezigheid(int werknemerId, LocalDate startDatum, LocalDate eindDatum, String reden, byte[] certificaat) {
        if (startDatum == null)
            throw new IllegalArgumentException("Startdatum is verplicht.");
        if (eindDatum == null)
            throw new IllegalArgumentException("Einddatum is verplicht.");
        if (eindDatum.isBefore(startDatum))
            throw new IllegalArgumentException("Einddatum mag niet vóór de startdatum liggen.");
        if (reden == null || reden.isBlank())
            throw new IllegalArgumentException("Reden is verplicht.");

        AfwezigheidAanmakenDTO dto = new AfwezigheidAanmakenDTO(werknemerId, startDatum, eindDatum, reden, certificaat);
        return api.meldAfwezigheid(dto);
    }
}