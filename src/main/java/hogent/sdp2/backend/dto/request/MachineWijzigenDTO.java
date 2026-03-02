package hogent.sdp2.backend.dto.request;

public record MachineWijzigenDTO(
        String naam,
        String status,
        Integer siteId
) {}