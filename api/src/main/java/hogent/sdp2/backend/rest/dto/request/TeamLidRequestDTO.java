package hogent.sdp2.backend.rest.dto.request;

public record TeamLidRequestDTO(
        int werknemerId,
        boolean isSupervisor
) {}