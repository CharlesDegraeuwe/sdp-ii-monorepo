package hogent.sdp2.backend.dto.request;

public record MachineAanmakenDTO(
        String naam,
        String status,
        Integer siteId
) {}