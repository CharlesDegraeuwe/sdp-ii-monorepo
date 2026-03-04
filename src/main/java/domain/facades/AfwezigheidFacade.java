package domain.facades;

import domain.dto.AfwezigheidAanmakenDTO;
import domain.services.AfwezigheidApiService;

import java.time.LocalDate;

public class AfwezigheidFacade {
    private final AfwezigheidApiService api = new AfwezigheidApiService();

    public String meldAfwezigheid(int werknemerId, LocalDate startDatum, LocalDate eindDatum, String reden, byte[] certificaat) {

        AfwezigheidAanmakenDTO dto = new AfwezigheidAanmakenDTO(werknemerId, startDatum, eindDatum, reden, certificaat);
        return api.meldAfwezigheid(dto);
    }
}