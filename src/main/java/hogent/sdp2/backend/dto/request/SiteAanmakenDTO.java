package hogent.sdp2.backend.dto.request;

import java.math.BigDecimal;

public record SiteAanmakenDTO(
        String naam,
        String stad,
        String land,
        BigDecimal longitude,
        BigDecimal latitude
) {}