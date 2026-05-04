package hogent.sdp2.backend.REST.dto.request;

public record MachineAanmakenDTO(
        String naam,
        String status,
        Integer siteId
) {}