package hogent.sdp2.backend.REST.dto.request;

public record TeamLidRequestDTO(
        int werknemerId,
        boolean isSupervisor
) {}