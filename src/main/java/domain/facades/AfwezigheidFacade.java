package domain.facades;

import domain.dto.AfwezigheidAanmakenDTO;
import domain.services.AfwezigheidApiService;

import java.time.LocalDate;

public class AfwezigheidFacade {
    private final AfwezigheidApiService api = new AfwezigheidApiService();

    public String meldAfwezigheid(int werknemerId, LocalDate startDatum, LocalDate eindDatum, String reden, byte[] certificaat) {
        if (reden == null || reden.isBlank()) {
            throw new IllegalArgumentException("Reden mag niet leeg zijn.");
        }
        if (startDatum == null || eindDatum == null) {
            throw new IllegalArgumentException("Start- en einddatum zijn verplicht.");
        }
        if (eindDatum.isBefore(startDatum)) {
            throw new IllegalArgumentException("Einddatum mag niet voor startdatum liggen.");
        }
        if (certificaat == null || certificaat.length == 0) {
            throw new IllegalArgumentException("Certificaat is verplicht bij ziekte.");
        }

        AfwezigheidAanmakenDTO dto = new AfwezigheidAanmakenDTO(werknemerId, startDatum, eindDatum, reden, certificaat);
        return api.meldAfwezigheid(dto);
    }
}