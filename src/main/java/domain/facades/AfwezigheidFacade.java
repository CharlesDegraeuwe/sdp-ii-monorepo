package domain.facades;

import domain.dto.AfwezigheidAanmakenDTO;
import domain.services.AfwezigheidApiService;
import domain.services.LogService;

import java.time.LocalDate;

public class AfwezigheidFacade {
    private final AfwezigheidApiService api = new AfwezigheidApiService();

    public String meldAfwezigheid(int werknemerId, LocalDate startDatum, LocalDate eindDatum, String reden, byte[] certificaat) {

        AfwezigheidAanmakenDTO dto = new AfwezigheidAanmakenDTO(werknemerId, startDatum, eindDatum, reden, certificaat);
        String result = api.meldAfwezigheid(dto);
        LogService.log("CREATE", "afwezigheid", "Ziekte gemeld – werknemerId: " + werknemerId + ", van: " + startDatum + " tot: " + eindDatum + ", reden: " + reden);
        return result;
    }
}