package hogent.sdp2.backend.rest.dto.request;

import java.time.LocalDate;

public record NotificatieDTO(
        int id,
        int werknemerId,
        String titel,
        String bericht,
        String gelezen,
        LocalDate datum,
        Integer referentieId
) {}