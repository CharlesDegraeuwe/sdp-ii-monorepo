package hogent.sdp2.backend.REST.dto.request;

import java.time.LocalDate;

public record VerlofAanvragenDTO(
        int werknemerId,
        LocalDate startDatum,
        LocalDate eindDatum,
        String type
) {}