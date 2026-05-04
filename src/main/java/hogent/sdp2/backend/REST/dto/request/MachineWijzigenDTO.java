package hogent.sdp2.backend.REST.dto.request;

public record MachineWijzigenDTO(
        String naam,
        String status,
        Integer siteId
) {}