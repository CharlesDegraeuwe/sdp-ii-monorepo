package hogent.sdp2.backend.dto.request;

import java.math.BigDecimal;

public record SiteWijzigenDTO(
        String naam,
        String locatie,
        Integer capaciteit,
        String status
) {}