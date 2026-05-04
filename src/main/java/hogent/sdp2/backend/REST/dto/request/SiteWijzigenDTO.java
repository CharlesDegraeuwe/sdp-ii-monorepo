package hogent.sdp2.backend.REST.dto.request;

public record SiteWijzigenDTO(
        String naam,
        String locatie,
        Integer capaciteit,
        String status
) {}