package hogent.sdp2.backend.rest.dto.request;

import java.time.LocalDate;

public record VerlofAanvragenDTO(
        int werknemerId, LocalDate startDatum, LocalDate eindDatum, String type) {}
