package hogent.sdp2.backend.REST.dto.request;

public record SiteAanmakenDTO(
        String naam,
        String locatie,
        Integer capaciteit,
        String status
) {}