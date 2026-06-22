package hogent.sdp2.backend.rest.dto.response;

import java.time.LocalDate;

public record GeschiedenisItemMetWerknemerDTO(
        Integer id,
        String type,
        LocalDate startDatum,
        LocalDate eindDatum,
        String status,
        String omschrijving,
        int werknemerId,
        String werknemerVoornaam,
        String werknemerNaam) {}
