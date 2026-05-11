package hogent.sdp2.backend.rest.dto.request;

public record SiteWijzigenDTO(
        String naam,
        String locatie,
        Integer capaciteit,
        String status
) {}