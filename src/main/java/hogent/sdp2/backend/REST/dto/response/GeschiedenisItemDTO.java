package hogent.sdp2.backend.REST.dto.response;

import java.time.LocalDate;

public record GeschiedenisItemDTO(
        Integer id,
        String type,
        LocalDate startDatum,
        LocalDate eindDatum,
        String status,
        String omschrijving
) {}