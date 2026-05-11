package hogent.sdp2.backend.rest.dto.request;

public record MachineWijzigenDTO(
        String naam,
        String status,
        Integer siteId
) {}